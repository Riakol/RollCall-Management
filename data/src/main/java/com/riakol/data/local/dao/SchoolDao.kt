package com.riakol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.riakol.data.local.entity.AttendanceEntity
import com.riakol.data.local.entity.ClassEntity
import com.riakol.data.local.entity.StudentEntity
import com.riakol.data.local.relations.LessonWithDetails
import com.riakol.domain.model.AttendanceType
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {

    // --- РАСПИСАНИЕ (HOME SCREEN) ---
    /**
     * Получает уроки за определенный промежуток времени (например, день).
     * @Transaction нужен, так как LessonWithDetails тянет данные из 3 таблиц.
     */
    @Transaction
    @Query("SELECT * FROM lessons WHERE startTime BETWEEN :start AND :end ORDER BY startTime ASC")
    fun getLessonsInRange(start: Long, end: Long): Flow<List<LessonWithDetails>>

    // --- КЛАССЫ (MY CLASSES SCREEN) ---
    @Query("SELECT * FROM classes")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT count(*) FROM students WHERE classId = :classId")
    suspend fun getStudentCountInClass(classId: Long): Int

    // --- СТУДЕНТЫ (GROUP SCREEN & DETAILS) ---
    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY lastName ASC")
    suspend fun getStudentsByClass(classId: Long): List<StudentEntity>

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentById(studentId: Long): StudentEntity?

    // --- ПОСЕЩАЕМОСТЬ (ATTENDANCE SCREEN) ---
    /**
     * Получает записи посещаемости для конкретного урока.
     */
    @Query("SELECT * FROM attendance WHERE lessonId = :lessonId")
    suspend fun getAttendanceForLesson(lessonId: Long): List<AttendanceEntity>

    /**
     * Сохраняет или обновляет список отметок.
     * Используется при нажатии "Сохранить" на экране отметки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendanceList: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    // --- АНАЛИТИКА (ANALYTICS SCREEN) ---
    /**
     * Считает количество пропусков/опозданий для студента.
     * Используется для карточки студента (85% посещаемость).
     */
    @Query("SELECT count(*) FROM attendance WHERE studentId = :studentId AND status = :status")
    suspend fun getCountByStatus(studentId: Long, status: AttendanceType): Int

    /**
     * Общее количество уроков, которые были у студента (для расчета процента).
     * Считаем уникальные lessonId в таблице attendance для этого студента.
     */
    @Query("SELECT count(DISTINCT lessonId) FROM attendance WHERE studentId = :studentId")
    suspend fun getTotalLessonsCountForStudent(studentId: Long): Int
}
