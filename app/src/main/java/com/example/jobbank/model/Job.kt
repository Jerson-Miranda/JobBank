package com.example.jobbank.model

import android.graphics.Bitmap

data class Job (
    val id: Int = 0,
    val createdBy: String = "",
    val title: String = "",
    val business: String = "",
    val address: String = "",
    val time: Int = 0,
    val applications: Int = 0,
    val type: String = "",
    val available: Int = 0,
    val students: Boolean = false,
    val speciality: String = "",
    val description: String = "",
    val archive: Boolean = false,
    val status: Boolean = true,
    var imageUrl: String = ""
)