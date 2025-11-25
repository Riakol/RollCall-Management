package com.riakol.data.util

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId


fun calculateAge(birthDateMillis: Long): Int {
    if (birthDateMillis == 0L) return 0

    val birthDate = Instant.ofEpochMilli(birthDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val currentDate = LocalDate.now()

    return Period.between(birthDate, currentDate).years
}