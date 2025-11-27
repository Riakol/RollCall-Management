package com.riakol.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.riakol.data.local.dao.SchoolDao
import com.riakol.data.local.entity.AttendanceEntity
import com.riakol.data.local.entity.ClassEntity
import com.riakol.data.local.entity.LessonEntity
import com.riakol.data.local.entity.StudentEntity
import com.riakol.data.local.entity.SubjectEntity

@Database(
    entities = [
        ClassEntity::class,
        StudentEntity::class,
        SubjectEntity::class,
        LessonEntity::class,
        AttendanceEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SchoolDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
}