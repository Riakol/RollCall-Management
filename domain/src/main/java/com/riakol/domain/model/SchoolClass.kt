package com.riakol.domain.model

data class SchoolClass(
    val id: Long,
    val name: String,
    val description: String?,
    val studentCount: Int,
    val previewStudents: List<Student>
)