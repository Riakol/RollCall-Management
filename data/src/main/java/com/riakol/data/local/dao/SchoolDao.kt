package com.riakol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.riakol.data.local.entity.AttendanceEntity
import com.riakol.data.local.entity.ClassEntity
import com.riakol.data.local.entity.LessonEntity
import com.riakol.data.local.entity.StudentEntity
import com.riakol.data.local.entity.SubjectEntity
import com.riakol.data.local.relations.ClassWithStudentsRelation
import com.riakol.data.local.relations.LessonWithDetails
import com.riakol.domain.model.AttendanceType
import kotlinx.coroutines.flow.Flow

data class StudentWithClassName(
    val studentId: Long,
    val firstName: String,
    val lastName: String,
    val className: String,
    val photoUrl: String?
)

data class LessonStatsTuple(
    val lessonId: Long,
    val classId: Long,
    val subjectName: String,
    val className: String,
    val startTime: Long,
    val endTime: Long,
    val roomNumber: String,
    val isFinished: Boolean,
    val totalStudents: Int,
    val presentCount: Int
)

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

    @Query("""
        SELECT 
            l.lessonId, 
            l.classId,
            s.name AS subjectName, 
            c.name AS className, 
            l.startTime, 
            l.endTime, 
            l.roomNumber, 
            l.isFinished,
            (SELECT COUNT(*) FROM students st WHERE st.classId = l.classId) AS totalStudents,
            (SELECT COUNT(*) FROM attendance a WHERE a.lessonId = l.lessonId AND (a.status = 'PRESENT' OR a.status = 'LATE')) AS presentCount
        FROM lessons l
        INNER JOIN subjects s ON l.subjectId = s.subjectId
        INNER JOIN classes c ON l.classId = c.classId
        WHERE l.startTime BETWEEN :start AND :end
        ORDER BY l.startTime ASC
    """)
    fun getLessonsWithStatsInRange(start: Long, end: Long): Flow<List<LessonStatsTuple>>

    // --- КЛАССЫ (MY CLASSES SCREEN) ---
    @Transaction
    @Query("SELECT * FROM classes")
    fun getClassesWithStudents(): Flow<List<ClassWithStudentsRelation>>

    @Transaction
    @Query("SELECT * FROM classes WHERE classId = :id")
    suspend fun getClassWithStudentsById(id: Long): ClassWithStudentsRelation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntity: ClassEntity)

    @Update
    suspend fun updateClass(classEntity: ClassEntity)

    @Query("DELETE FROM classes WHERE classId = :classId")
    suspend fun deleteClass(classId: Long)

    @Query("SELECT * FROM classes")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT count(*) FROM students WHERE classId = :classId")
    suspend fun getStudentCountInClass(classId: Long): Int

    // --- СТУДЕНТЫ (GROUP SCREEN & DETAILS) ---
    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY lastName ASC")
    suspend fun getStudentsByClass(classId: Long): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentById(studentId: Long): StudentEntity?

    @Query("DELETE FROM students WHERE studentId = :studentId")
    suspend fun deleteStudent(studentId: Long)

    @Query("""
        SELECT s.*, c.name as className 
        FROM students s 
        JOIN classes c ON s.classId = c.classId 
        ORDER BY s.lastName ASC
    """)
    fun getAllStudentsWithClass(): Flow<List<StudentEntity>>

    @Query("""
        SELECT s.studentId, s.firstName, s.lastName, s.photoUrl, c.name as className
        FROM students s
        JOIN classes c ON s.classId = c.classId
        ORDER BY s.lastName ASC
    """)
    fun getAllStudentsListItems(): Flow<List<StudentWithClassName>>

    // --- ПОСЕЩАЕМОСТЬ (ATTENDANCE SCREEN) ---
    /**
     * Получает записи посещаемости для конкретного урока.
     */
    @Query("SELECT * FROM attendance WHERE lessonId = :lessonId")
    suspend fun getAttendanceForLesson(lessonId: Long): List<AttendanceEntity>

    @Query("SELECT count(*) FROM attendance WHERE lessonId = :lessonId AND (status = 'PRESENT' OR status = 'LATE')")
    suspend fun getPresentCountForLesson(lessonId: Long): Int

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

    // --- УПРАВЛЕНИЕ УРОКАМИ ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("DELETE FROM lessons WHERE lessonId = :lessonId")
    suspend fun deleteLesson(lessonId: Long)

    @Query("SELECT * FROM lessons WHERE lessonId = :lessonId")
    suspend fun getLessonById(lessonId: Long): LessonEntity?

    // --- ПРЕДМЕТЫ ---
    @Query("SELECT * FROM subjects WHERE name = :name LIMIT 1")
    suspend fun getSubjectByName(name: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Query("SELECT * FROM subjects WHERE subjectId = :id")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("""
        SELECT count(*) FROM lessons 
        WHERE classId = :classId 
        AND lessonId != :excludeLessonId
        AND startTime < :endTime 
        AND endTime > :startTime
    """)
    suspend fun checkLessonOverlap(
        classId: Long,
        startTime: Long,
        endTime: Long,
        excludeLessonId: Long
    ): Int
}
