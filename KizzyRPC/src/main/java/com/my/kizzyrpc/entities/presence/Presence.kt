package com.my.kizzyrpc.entities.presence

import com.my.kizzyrpc.entities.presence.Activity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Presence(
    @SerialName("activities")
    val activities: List<Activity?>?,
    @SerialName("afk")
    val afk: Boolean? = true,
    @SerialName("since")
    val since: Long? = 0L,
    @SerialName("status")
    val status: String? = "online"
)