package com.riakol.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.riakol.domain.model.AttendanceType

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["lessonId"],
            childColumns = ["lessonId"]
        ),
        ForeignKey(entity = StudentEntity::class, parentColumns = ["studentId"], childColumns = ["studentId"])
    ],
    indices = [Index("lessonId"), Index("studentId")]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: Long,
    val studentId: Long,
    val status: AttendanceType,
    val comment: String? = null
)
