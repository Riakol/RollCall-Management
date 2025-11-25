package com.riakol.domain.model

enum class AttendanceType {
    PRESENT, ABSENT, LATE, EXCUSED
}

data class AttendanceRecord(
    val studentId: Long,
    val lessonId: Long,
    val status: AttendanceType,
    val comment: String?
)