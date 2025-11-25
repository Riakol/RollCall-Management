package com.riakol.data.repository

import com.riakol.data.local.dao.SchoolDao
import com.riakol.data.mapper.toDomain
import com.riakol.data.mapper.toEntity
import com.riakol.domain.model.AttendanceRecord
import com.riakol.domain.model.Lesson
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

    override fun getLessonsForDate(date: Long): Flow<List<Lesson>> {
        val zoneId = ZoneId.systemDefault()
        val localDate = Instant.ofEpochMilli(date).atZone(zoneId).toLocalDate()

        val startOfDay = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

        val endOfDay = localDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant().toEpochMilli()

        return dao.getLessonsInRange(startOfDay, endOfDay).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getStudentsByClass(classId: Long): List<Student> {
        return dao.getStudentsByClass(classId).map { entity ->
            entity.toDomain()
        }
    }

    override suspend fun saveAttendance(lessonId: Long, students: List<AttendanceRecord>) {
        val entities = students.map { record ->
            record.toEntity()
        }
        dao.insertAttendance(entities)
    }
}