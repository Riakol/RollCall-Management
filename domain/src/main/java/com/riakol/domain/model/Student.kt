package com.riakol.domain.model

data class Student(
    val id: Long = 0,
    val classId: Long = 0,
    val firstName: String,
    val lastName: String,
    val middleName: String? = null,
    val age: Int = 0,
    val phoneNumber: String?,
    val photoUrl: String?,
    val className: String? = null,
    val healthInfo: String? = null,
    val teacherNotes: String? = null,
    val birthDate: Long = 0
)