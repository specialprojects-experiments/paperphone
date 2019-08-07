package com.withgoogle.experiments.unplugged.model

data class Calendar(
    val id: Long,
    val accountName: String,
    val displayName: String,
    val accountOwner: String,
    val accountType: String,
    val eventsCount: Int = 0
)

data class Event(
    val id: Long,
    val title: String,
    val dateStart: Long,
    val dateEnd: Long
)