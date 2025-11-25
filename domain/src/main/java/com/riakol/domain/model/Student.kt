package com.riakol.domain.model

data class Student(
    val id: Long = 0,
    val name: String,
    val surname: String,
    val age: Int,
    val phoneNumber: String?,
    val photoUrl: String?,
)