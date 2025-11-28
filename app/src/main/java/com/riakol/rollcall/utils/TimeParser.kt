package com.riakol.rollcall.utils

fun parseTime(timeStr: String): Pair<Int, Int> {
    val parts = timeStr.split(":")
    return (parts.getOrNull(0)?.toIntOrNull() ?: 8) to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
}