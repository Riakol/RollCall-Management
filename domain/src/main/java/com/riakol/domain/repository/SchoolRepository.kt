package com.riakol.domain.repository

import com.riakol.domain.model.AttendanceRecord
import com.riakol.domain.model.Lesson
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getLessonsForDate(date: Long): Flow<List<Lesson>>
    suspend fun getStudentsByClass(classId: Long): List<Student>
    suspend fun saveAttendance(lessonId: Long, students: List<AttendanceRecord>)
    fun getAllClasses(): Flow<List<SchoolClass>>
    suspend fun getClassById(classId: Long): SchoolClass?
    suspend fun createClass(name: String, description: String?)
    suspend fun createStudent(classId: Long, firstName: String, lastName: String, middleName: String?, phone: String?)
}