package com.riakol.data.local

import androidx.room.TypeConverter
import com.riakol.domain.model.AttendanceType

class Converters {
    @TypeConverter
    fun fromAttendanceType(value: AttendanceType): String = value.name

    @TypeConverter
    fun toAttendanceType(value: String): AttendanceType = AttendanceType.valueOf(value)
}
