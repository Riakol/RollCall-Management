package com.riakol.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = ClassEntity::class,
            parentColumns = ["classId"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("classId")]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val studentId: Long = 0,
    val classId: Long,
    val firstName: String,
    val lastName: String,
    val middleName: String? = null,
    val birthDate: Long,
    val phoneNumber: String?,
    val parentPhone: String?,
    val photoUrl: String? = null,
    val healthInfo: String? = null,
    val teacherNotes: String? = null
)