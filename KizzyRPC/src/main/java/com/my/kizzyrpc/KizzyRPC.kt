/*
 *   Copyright (c) 2023 Kizzy. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.my.kizzyrpc

import android.util.ArrayMap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.my.kizzyrpc.model.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException
import javax.net.ssl.SSLParameters

const val RICH_PRESENCE_OP_CODE = 3
const val TAG = "KizzyRPC"
class KizzyRPC(var token: String) {
    private var rpc: RichPresence? = null
    private var webSocketClient: WebSocketClient? = null
    private var gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var heartbeatRunnable: Runnable
    private var heartbeatThr: Thread? = null
    private var heartbeatInterval = 0
    private var seq = 0
    private var sessionId: String? = null
    private var reconnectSession = false

    fun closeRPC() {
        if (heartbeatThr != null) {
            if (!heartbeatThr!!.isInterrupted) heartbeatThr!!.interrupt()
        }
        if (webSocketClient != null) webSocketClient!!.close(1000)
    }

    fun isRpcRunning(): Boolean {
        return webSocketClient?.isOpen == true
    }

    fun setActivity(
        activity: Activity,
        status: String = "online",
        since: Long = System.currentTimeMillis()
    ) {
        rpc = RichPresence(
            d = RichPresenceData(
                activities = listOf(activity),
                afk = false,
                since = since,
                status = status
            ),
            op = RICH_PRESENCE_OP_CODE
        )
        createWebsocketClient()
    }

    fun sendIdentify() {
        Log.d(TAG, "sendIdentify() called")
        webSocketClient!!.send(
            Identify(
                op = 2,
                d = Data(
                    capabilities = 65,
                    compress = false,
                    largeThreshold = 100,
                    properties = Properties(
                        browser = "Discord Client",
                        device = "disco",
                        os = "Windows"
                    ),
                    token = token
                )
            )
        )
    }

    private fun createWebsocketClient() {
        val uri: URI = try {
            URI("wss://gateway.discord.gg/?encoding=json&v=10")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }
        val headerMap = androidx.collection.ArrayMap<String, String>()
        webSocketClient = Websocket(uri, headerMap)
        (webSocketClient as Websocket).connect()
    }

    init {
        heartbeatRunnable = Runnable {
            try {
                if (heartbeatInterval < 10000) throw RuntimeException("invalid")
                Thread.sleep(heartbeatInterval.toLong())
                webSocketClient.send(
                    HeartBeat(
                        op = 1,
                        d = if (seq == 0) "null" else seq.toString()
                    )
                )
            } catch (_: InterruptedException) {
            }
        }
    }

    inner class Websocket(uri: URI, map: androidx.collection.ArrayMap<String, String>) : WebSocketClient(uri, map) {
        private var gatewayResume = ""

        override fun send(text: String?) {
            super.send(text)
        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            Log.d(TAG, "Socket Opened with: handshake-data = $handshakedata")
        }

        override fun onMessage(message: String) {
            val map = gson.fromJson<ArrayMap<String, Any>>(
                message, object : TypeToken<ArrayMap<String?, Any?>?>() {}.type
            )
            val o = map["s"]
            if (o != null) {
                seq = (o as Double).toInt()
            }
            when ((map["op"] as Double?)!!.toInt()) {
                0 -> if (map["t"] as String? == "READY") {
                    sessionId = (map["d"] as Map<*, *>?)!!["session_id"].toString()
                    gatewayResume = (map["d"] as Map<*, *>?)!!["resume_gateway_url"].toString()
                    Log.d(TAG, "Gateway Url updated to: $gatewayResume")
                    Log.i(TAG, "Sending Activity...")
                    rpc?.let { send(it) }
                    return
                }
                10 -> if (!reconnectSession) {
                    val data = map["d"] as Map<*, *>?
                    heartbeatInterval = (data!!["heartbeat_interval"] as Double?)!!.toInt()
                    heartbeatThr = Thread(heartbeatRunnable)
                    heartbeatThr!!.start()
                    sendIdentify()
                } else {
                    Log.d(TAG, "Sending Resume")
                    val data = map["d"] as Map<*, *>?
                    heartbeatInterval = (data!!["heartbeat_interval"] as Double?)!!.toInt()
                    heartbeatThr = Thread(heartbeatRunnable)
                    heartbeatThr!!.start()
                    reconnectSession = false
                    send(
                        Resume(
                            op = 6,
                            d = D(
                                token = token,
                                sessionId = sessionId,
                                seq = seq
                            )
                        )
                    )
                }
                1 -> {
                    if (!Thread.interrupted()) {
                        heartbeatThr!!.interrupt()
                    }
                    send(
                        HeartBeat(
                            op = 1,
                            d = if (seq == 0) "null" else seq.toString()
                        )
                    )
                }
                11 -> {
                    if (!Thread.interrupted()) {
                        heartbeatThr!!.interrupt()
                    }
                    heartbeatThr = Thread(heartbeatRunnable)
                    heartbeatThr!!.start()
                }
                7 -> {
                    reconnectSession = true
                    Log.e(TAG, "Closing Session and Reconnecting")
                    webSocketClient!!.close(4000)
                }
                9 -> if (!heartbeatThr!!.isInterrupted) {
                    Log.d(TAG, "Reconnect Failed")
                    heartbeatThr!!.interrupt()
                    heartbeatThr = Thread(heartbeatRunnable)
                    heartbeatThr!!.start()
                    sendIdentify()
                }
            }
        }

        override fun onClose(code: Int, reason: String, remote: Boolean) {
            Log.d(TAG, "Closed with: code = $code, reason = $reason, remote = $remote")
            if (code == 4000) {
                reconnectSession = true
                heartbeatThr!!.interrupt()
                Log.e(TAG, "Socket Closed")
                val newTh = Thread {
                    try {
                        Thread.sleep(200)
                        webSocketClient = Websocket(URI(gatewayResume), androidx.collection.ArrayMap<String, String>())
                        (webSocketClient as Websocket).connect()
                    } catch (_: InterruptedException) {
                    }
                }
                newTh.start()
            } else throw RuntimeException("Invalid")
        }

        override fun onError(e: Exception) {
            Log.e(TAG, "onError() called with: e = $e")
            if (e.message != "Interrupt") {
                closeRPC()
            }
        }

        override fun onSetSSLParameters(p: SSLParameters) {
            try {
                super.onSetSSLParameters(p)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }
    }

    private fun WebSocketClient?.send(src: Any) {
        this?.let {
            if (it.isOpen)
                it.send(gson.toJson(src))
        }
    }
}