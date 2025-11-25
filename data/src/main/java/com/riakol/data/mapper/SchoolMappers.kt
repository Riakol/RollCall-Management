package com.riakol.data.mapper

import com.riakol.data.local.entity.AttendanceEntity
import com.riakol.data.local.entity.StudentEntity
import com.riakol.data.local.relations.LessonWithDetails
import com.riakol.data.util.calculateAge
import com.riakol.domain.model.AttendanceRecord
import com.riakol.domain.model.Lesson
import com.riakol.domain.model.Student
import java.time.Instant
import java.time.ZoneId

fun StudentEntity.toDomain(): Student {
    return Student(
        id = this.studentId,
        firstName = this.firstName,
        lastName = this.lastName,
        age = calculateAge(this.birthDate),
        phoneNumber = this.phoneNumber,
        photoUrl = this.photoUrl
    )
}


fun LessonWithDetails.toDomain(): Lesson {
    return Lesson(
        id = this.lesson.lessonId,
        subjectName = this.subject.name,
        className = this.classInfo.name,
        startTime = Instant.ofEpochMilli(this.lesson.startTime).atZone(ZoneId.systemDefault()).toLocalDateTime(),
        endTime = Instant.ofEpochMilli(this.lesson.endTime).atZone(ZoneId.systemDefault()).toLocalDateTime(),
        roomNumber = this.lesson.roomNumber,
        isFinished = this.lesson.isFinished
    )
}

fun AttendanceRecord.toEntity(): AttendanceEntity {
    return AttendanceEntity(
        lessonId = this.lessonId,
        studentId = this.studentId,
        status = this.status,
        comment = this.comment
    )
}
