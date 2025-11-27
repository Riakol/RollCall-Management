package com.riakol.data.repository

import com.riakol.data.local.dao.SchoolDao
import com.riakol.data.local.entity.ClassEntity
import com.riakol.data.local.entity.StudentEntity
import com.riakol.data.mapper.toDomain
import com.riakol.data.mapper.toEntity
import com.riakol.data.util.calculateAge
import com.riakol.domain.model.AttendanceRecord
import com.riakol.domain.model.Lesson
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.model.Student
import com.riakol.domain.repository.SchoolRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

class SchoolRepositoryImpl @Inject constructor(
    private val dao: SchoolDao
) : SchoolRepository {

    override suspend fun getStudentsByClass(classId: Long): List<Student> {
        return dao.getStudentsByClass(classId).map { entity ->
            entity.toDomain()
        }
    }

    override fun getAllStudents(): Flow<List<Student>> {
        return dao.getAllStudentsListItems().map { list ->
            list.map { item ->
                Student(
                    id = item.studentId,
                    firstName = item.firstName,
                    lastName = item.lastName,
                    className = item.className,
                    age = 0,
                    phoneNumber = null,
                    photoUrl = item.photoUrl,
                    classId = 0
                )
            }
        }
    }

    override suspend fun getStudentById(studentId: Long): Student? {
        val entity = dao.getStudentById(studentId) ?: return null
        val classEntity = dao.getClassWithStudentsById(entity.classId)?.classEntity

        return Student(
            id = entity.studentId,
            classId = entity.classId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            middleName = entity.middleName,
            age = calculateAge(entity.birthDate),
            birthDate = entity.birthDate,
            phoneNumber = entity.phoneNumber,
            photoUrl = entity.photoUrl,
            className = classEntity?.name,
            healthInfo = entity.healthInfo,
            teacherNotes = entity.teacherNotes
        )
    }

    override suspend fun deleteStudent(studentId: Long) {
        dao.deleteStudent(studentId)
    }

    override suspend fun saveAttendance(lessonId: Long, students: List<AttendanceRecord>) {
        val entities = students.map { record ->
            record.toEntity()
        }
        dao.insertAttendance(entities)
    }

    override fun getAllClasses(): Flow<List<SchoolClass>> {
        return dao.getClassesWithStudents().map { relations ->
            relations.map { relation ->
                SchoolClass(
                    id = relation.classEntity.classId,
                    name = relation.classEntity.name,
                    description = relation.classEntity.description,
                    studentCount = relation.students.size,
                    previewStudents = relation.students.take(3).map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun getClassById(classId: Long): SchoolClass? {
        val relation = dao.getClassWithStudentsById(classId) ?: return null

        return SchoolClass(
            id = relation.classEntity.classId,
            name = relation.classEntity.name,
            description = relation.classEntity.description,
            studentCount = relation.students.size,
            previewStudents = relation.students.take(3).map { it.toDomain() }
        )
    }

    override suspend fun createClass(name: String, description: String?) {
        val newClass = ClassEntity(
            name = name,
            description = description
        )
        dao.insertClass(newClass)
    }

    override suspend fun updateClass(id: Long, name: String, description: String?) {
        val updatedClass = ClassEntity(
            classId = id,
            name = name,
            description = description
        )
        dao.updateClass(updatedClass)
    }

    override suspend fun deleteClass(id: Long) {
        dao.deleteClass(id)
    }

    override suspend fun createStudent(
        classId: Long,
        firstName: String,
        lastName: String,
        middleName: String?,
        phone: String?
    ) {
        val student = StudentEntity(
            classId = classId,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = 0L,
            phoneNumber = phone,
            parentPhone = null,
            photoUrl = null
        )
        dao.insertStudent(student)
    }

    override suspend fun saveStudentFull(
        classId: Long,
        firstName: String,
        lastName: String,
        middleName: String?,
        birthDate: Long,
        phone: String?,
        healthInfo: String?,
        notes: String?,
        studentId: Long
    ) {
        val entity = StudentEntity(
            studentId = studentId,
            classId = classId,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = birthDate,
            phoneNumber = phone,
            parentPhone = null,
            photoUrl = null,
            healthInfo = healthInfo,
            teacherNotes = notes
        )
        dao.insertStudent(entity)
    }

    override fun getLessonsForDate(date: Long): Flow<List<Lesson>> {
        val zoneId = ZoneId.systemDefault()
        val localDate = Instant.ofEpochMilli(date).atZone(zoneId).toLocalDate()

        val startOfDay = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = localDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant().toEpochMilli()

        return dao.getLessonsWithStatsInRange(startOfDay, endOfDay).map { list ->
            list.map { item ->
                Lesson(
                    id = item.lessonId,
                    subjectName = item.subjectName,
                    className = item.className,
                    startTime = Instant.ofEpochMilli(item.startTime).atZone(zoneId).toLocalDateTime(),
                    endTime = Instant.ofEpochMilli(item.endTime).atZone(zoneId).toLocalDateTime(),
                    roomNumber = item.roomNumber,
                    isFinished = item.isFinished,
                    presentCount = item.presentCount,
                    totalStudents = item.totalStudents
                )
            }
        }
    }
}