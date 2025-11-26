package com.riakol.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.riakol.data.local.entity.ClassEntity
import com.riakol.data.local.entity.LessonEntity
import com.riakol.data.local.entity.StudentEntity
import com.riakol.data.local.entity.SubjectEntity

/**
 * Combines Lesson + Subject + Class.
 * Used on the Main Screen (Schedule).
 */

data class LessonWithDetails(
    @Embedded val lesson: LessonEntity,

    @Relation(
        parentColumn = "subjectId",
        entityColumn = "subjectId"
    )
    val subject: SubjectEntity,

    @Relation(
        parentColumn = "classId",
        entityColumn = "classId"
    )
    val classInfo: ClassEntity
)

data class ClassWithStudentsRelation(
    @Embedded val classEntity: ClassEntity,

    @Relation(
        parentColumn = "classId",
        entityColumn = "classId"
    )
    val students: List<StudentEntity>
)