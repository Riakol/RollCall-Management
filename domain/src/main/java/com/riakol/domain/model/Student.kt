package com.riakol.domain.model

data class Student(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val phoneNumber: String?,
    val photoUrl: String?,
)