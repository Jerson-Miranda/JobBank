package com.example.jobbank.model

class User (
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val typeUser: String = "",
    val speciality: String = "",
    val business: String = "",
    val address: String = "",
    val followers: String = "",
    val status: Boolean = false,
    val tokenNotification: String = "",
    var imageUrl: String = "",
    var imageUrl2: String = "",
    var description: String = ""
)