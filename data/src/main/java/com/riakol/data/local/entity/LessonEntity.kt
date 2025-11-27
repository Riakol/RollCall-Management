package com.riakol.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = ClassEntity::class,
            parentColumns = ["classId"],
            childColumns = ["classId"]
        ),
        ForeignKey(entity = SubjectEntity::class, parentColumns = ["subjectId"], childColumns = ["subjectId"])
    ],
    indices = [Index("classId"), Index("subjectId")]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val lessonId: Long = 0,
    val classId: Long,
    val subjectId: Long,
    val startTime: Long,
    val endTime: Long,
    val roomNumber: String,
    val topic: String? = null,
    val isFinished: Boolean = false,
    val color: String? = null
)
