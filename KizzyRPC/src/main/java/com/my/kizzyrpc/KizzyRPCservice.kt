package com.my.kizzyrpc

import android.util.ArrayMap
import android.util.Log
import org.java_websocket.client.WebSocketClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.my.kizzyrpc.KizzyRPCservice
import org.java_websocket.handshake.ServerHandshake
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import java.lang.RuntimeException
import java.net.URI
import java.net.URISyntaxException
import java.util.ArrayList
import javax.net.ssl.SSLParameters

class KizzyRPCservice(var token: String) {
    private var applicationId: String? = null
    private var activity_name: String? = null
    private var details: String? = null
    private var state: String? = null
    private var large_image: String? = null
    private var small_image: String? = null
    private var status: String? = null
    private var start_timestamps: Long? = null
    private var stop_timestamps: Long? = null
    private var type = 0
    var rpc = ArrayMap<String, Any>()
    var webSocketClient: WebSocketClient? = null
    var gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    var heartbeatRunnable: Runnable
    var heartbeatThr: Thread? = null
    var heartbeat_interval = 0
    var seq = 0
    private var session_id: String? = null
    private var reconnect_session = false
    private var buttons = ArrayList<String>()
    private var button_url = ArrayList<String>()
    fun closeRPC() {
        if (heartbeatThr != null && !heartbeatThr!!.isInterrupted) heartbeatThr!!.interrupt()
        if (webSocketClient != null) webSocketClient!!.close(1000)
    }

    /**
     * Application Id for Rpc
     * An application id is required for functioning of urls in buttons
     * @param applicationId
     * @return
     */
    fun setApplicationId(applicationId: String?): KizzyRPCservice {
        this.applicationId = applicationId
        return this
    }

    /**
     * Activity Name of Rpc
     *
     * @param activity_name
     * @return
     */
    fun setName(activity_name: String?): KizzyRPCservice {
        this.activity_name = activity_name
        return this
    }

    /**
     * Details of Rpc
     *
     * @param details
     * @return
     */
    fun setDetails(details: String?): KizzyRPCservice {
        this.details = details
        return this
    }

    /**
     * Rpc State
     *
     * @param state
     * @return
     */
    fun setState(state: String?): KizzyRPCservice {
        this.state = state
        return this
    }

    /**
     * Large image on rpc
     * How to get Image ?
     * Upload image to any discord chat and copy its media link it should look like "https://media.discordapp.net/attachments/90202992002/xyz.png" now just use the image link from attachments part
     * so it would look like: .setLargeImage("attachments/90202992002/xyz.png")
     * @param large_image
     * @return
     */
    fun setLargeImage(large_image: String): KizzyRPCservice {
        this.large_image = "mp:$large_image"
        return this
    }

    /**
     * Small image on Rpc
     *
     * @param small_image
     * @return
     */
    fun setSmallImage(small_image: String): KizzyRPCservice {
        this.small_image = "mp:$small_image"
        return this
    }

    /**
     * start timestamps
     *
     * @param start_timestamps
     * @return
     */
    fun setStartTimestamps(start_timestamps: Long?): KizzyRPCservice {
        this.start_timestamps = start_timestamps
        return this
    }

    /**
     * stop timestamps
     *
     * @param stop_timestamps
     * @return
     */
    fun setStopTimestamps(stop_timestamps: Long?): KizzyRPCservice {
        this.stop_timestamps = stop_timestamps
        return this
    }

    /**
     * Activity Types
     * 0: Playing
     * 1: Streaming
     * 2: Listening
     * 3: Watching
     * 5: Competing
     *
     * @param type
     * @return
     */
    fun setType(type: Int): KizzyRPCservice {
        this.type = type
        return this
    }

    /**
     * Status type for profile online,idle,dnd
     *
     * @param status
     * @return
     */
    fun setStatus(status: String?): KizzyRPCservice {
        this.status = status
        return this
    }

    /**
     * Button1 text
     * @param button_label
     * @return
     */
    fun setButton1(button_label: String, link: String): KizzyRPCservice {
        buttons.add(button_label)
        button_url.add(link)
        return this
    }

    /**
     * Button2 text
     * @param button_label
     * @return
     */
    fun setButton2(button_label: String, link: String): KizzyRPCservice {
        buttons.add(button_label)
        button_url.add(link)
        return this
    }

    fun build() {
        val presence = ArrayMap<String, Any?>()
        val activity = ArrayMap<String, Any?>()
        activity["application_id"] = applicationId
        activity["name"] = activity_name
        activity["details"] = details
        activity["state"] = state
        activity["type"] = type
        val timestamps = ArrayMap<String, Any?>()
        timestamps["start"] = start_timestamps
        timestamps["stop"] = stop_timestamps
        activity["timestamps"] = timestamps
        val assets = ArrayMap<String, Any?>()
        assets["large_image"] = large_image
        assets["small_image"] = small_image
        activity["assets"] = assets
        if (buttons.size > 0) {
            val metadata = ArrayMap<String, Any>()
            activity["buttons"] = buttons
            metadata["button_urls"] = button_url
            activity["metadata"] = metadata
        }
        presence["activities"] = arrayOf<Any>(activity)
        presence["afk"] = true
        presence["since"] = start_timestamps
        presence["status"] = status
        rpc["op"] = 3
        rpc["d"] = presence
        createWebsocketClient()
    }

    fun sendIdentify() {
        val prop = ArrayMap<String, Any>()
        prop["\$os"] = "windows"
        prop["\$browser"] = "Chrome"
        prop["\$device"] = "disco"
        val data = ArrayMap<String, Any>()
        data["token"] = token
        data["properties"] = prop
        data["compress"] = false
        data["intents"] = 0
        val identify = ArrayMap<String, Any>()
        identify["op"] = 2
        identify["d"] = data
        webSocketClient!!.send(gson.toJson(identify))
    }

    fun createWebsocketClient() {
        Log.i("Connecting", "")
        val uri: URI
        uri = try {
            URI("wss://gateway.discord.gg/?encoding=json&v=9")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }
        val headerMap = ArrayMap<String, String>()
        webSocketClient = object : WebSocketClient(uri, headerMap) {
            override fun onOpen(s: ServerHandshake) {
                Log.e("Connection opened", "")
            }

            override fun onMessage(message: String) {
                val map = gson.fromJson<ArrayMap<String, Any>>(
                        message, object : TypeToken<ArrayMap<String?, Any?>?>() {}.type
                )
                val o = map["s"]
                if (o != null) {
                    seq = (o as Double).toInt()
                }
                val opcode = (map["op"] as Double?)!!.toInt()
                when (opcode) {
                    0 -> if (map["t"] as String? == "READY") {
                        session_id = (map["d"] as Map<*, *>?)!!["session_id"].toString()
                        Log.e("Connected", "")
                        webSocketClient!!.send(gson.toJson(rpc))
                        Log.e("", session_id!!)
                        return
                    }
                    10 -> if (!reconnect_session) {
                        val data = map["d"] as Map<*, *>?
                        heartbeat_interval = (data!!["heartbeat_interval"] as Double?)!!.toInt()
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                        sendIdentify()
                    } else {
                        Log.e("Sending Reconnect", "")
                        val data = map["d"] as Map<*, *>?
                        heartbeat_interval = (data!!["heartbeat_interval"] as Double?)!!.toInt()
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                        reconnect_session = false
                        webSocketClient!!.send("{\"op\": 6,\"d\":{\"token\":\"$token\",\"session_id\":\"$session_id\",\"seq\":$seq}}")
                    }
                    1 -> {
                        if (!Thread.interrupted()) {
                            heartbeatThr!!.interrupt()
                        }
                        webSocketClient!!.send("{\"op\":1, \"d\":" + (if (seq == 0) "null" else Integer.toString(seq)) + "}")
                    }
                    11 -> {
                        if (!Thread.interrupted()) {
                            heartbeatThr!!.interrupt()
                        }
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                    }
                    7 -> {
                        reconnect_session = true
                        webSocketClient!!.close(4000)
                    }
                    9 -> if (!heartbeatThr!!.isInterrupted) {
                        heartbeatThr!!.interrupt()
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                        sendIdentify()
                    }
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                if (code == 4000) {
                    reconnect_session = true
                    heartbeatThr!!.interrupt()
                    Log.e("", "Closed Socket")
                    val newTh = Thread {
                        try {
                            Thread.sleep(200)
                            reconnect()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    newTh.start()
                } else throw RuntimeException("Invalid")
            }

            override fun onError(e: Exception) {
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
        (webSocketClient as WebSocketClient).connect()
    }

    init {
        heartbeatRunnable = Runnable {
            try {
                if (heartbeat_interval < 10000) throw RuntimeException("invalid")
                Thread.sleep(heartbeat_interval.toLong())
                webSocketClient!!.send("{\"op\":1, \"d\":" + (if (seq == 0) "null" else Integer.toString(seq)) + "}")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}