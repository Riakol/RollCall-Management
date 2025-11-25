package com.riakol.domain.model

import java.time.LocalDateTime

data class Lesson(
    val id: Long = 0,
    val subjectName: String,
    val className: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val roomNumber: String,
    val isFinished: Boolean
)
