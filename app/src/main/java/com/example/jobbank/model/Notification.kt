package com.example.jobbank.model

class Notification (
    val id: Int = 0,
    val sender: String = "",
    val recipient: String = "",
    val title: String = "",
    val body: String = "",
    val time: Int = 0,
    val status: Boolean = false,
    var image: String = ""
)