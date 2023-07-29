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

import com.my.kizzyrpc.logger.Logger
import com.my.kizzyrpc.logger.NoOpLogger
import com.my.kizzyrpc.websocket.DiscordWebSocket
import com.my.kizzyrpc.websocket.DiscordWebSocketImpl
import com.my.kizzyrpc.entities.presence.Activity
import com.my.kizzyrpc.entities.presence.Presence

const val TAG = "KizzyRPC"
/**
 * KizzyRPC is a class that implements Discord Rich Presence functionality using WebSockets.
 * @param token discord account token
 */
class KizzyRPC(
    var token: String,
    var logger: Logger = NoOpLogger
) {
    private val discordWebSocket: DiscordWebSocket = DiscordWebSocketImpl(token, logger)
    private var rpc: Presence? = null
    /**
     * Closes the Rich Presence connection.
     */
    fun closeRPC() {
        discordWebSocket.close()
    }

    /**
     * Returns whether the Rich Presence connection is running.
     * @return true if the connection is open, false otherwise.
     */
    fun isRpcRunning(): Boolean {
        return discordWebSocket.isWebSocketConnected()
    }

    /**
     * Sets the activity for the Rich Presence.
     * @param activity the activity to set.
     * @param status the presence status to set.
     * @param since the activity start time.
     */
    suspend fun setActivity(
        activity: Activity,
        status: String = "online",
        since: Long = System.currentTimeMillis()
    ) {
        rpc = Presence(
            activities = listOf(activity),
            afk = false,
            since = since,
            status = status
        )
        connectToWebSocket()
    }

    private suspend fun connectToWebSocket() {
        if (token.isEmpty())
            logger.e(
                tag = TAG,
                event = "Token Seems to be invalid, Please Login if you haven't"
            )
        discordWebSocket.connect()
        rpc?.let { discordWebSocket.sendActivity(it) }
    }

    suspend fun updateRPC(
        activity: Activity,
        status: String = "online",
        since: Long = System.currentTimeMillis()
    ) {
        rpc = Presence(
            activities = listOf(activity),
            afk = false,
            since = since,
            status = status
        )
        if (!isRpcRunning()) {
            logger.d(
                tag = TAG,
                event = "Rpc is currently not running, Trying to connect to a new session"
            )
            connectToWebSocket()
            return
        }
        rpc?.let { discordWebSocket.sendActivity(it) }
    }
}