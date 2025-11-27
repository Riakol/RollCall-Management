package com.riakol.domain.repository

import com.riakol.domain.model.AttendanceRecord
import com.riakol.domain.model.Lesson
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    // --- РАСПИСАНИЕ ---
    fun getLessonsForDate(date: Long): Flow<List<Lesson>>
    suspend fun saveAttendance(lessonId: Long, students: List<AttendanceRecord>)

    // --- КЛАССЫ ---
    fun getAllClasses(): Flow<List<SchoolClass>>
    suspend fun getClassById(classId: Long): SchoolClass?

    // Управление классами
    suspend fun createClass(name: String, description: String?)
    suspend fun updateClass(id: Long, name: String, description: String?)
    suspend fun deleteClass(id: Long)

    // --- СТУДЕНТЫ ---
    suspend fun getStudentsByClass(classId: Long): List<Student>
    fun getAllStudents(): Flow<List<Student>>
    suspend fun getStudentById(studentId: Long): Student?

    // Управление студентами
    suspend fun createStudent(
        classId: Long,
        firstName: String,
        lastName: String,
        middleName: String?,
        phone: String?
    )

    suspend fun saveStudentFull(
        classId: Long,
        firstName: String,
        lastName: String,
        middleName: String?,
        birthDate: Long,
        phone: String?,
        healthInfo: String?,
        notes: String?,
        studentId: Long = 0
    )

    suspend fun deleteStudent(studentId: Long)
}