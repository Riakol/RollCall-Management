package com.riakol.rollcall.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riakol.domain.model.Lesson
import com.riakol.domain.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale


data class CalendarDay(
    val date: LocalDate,
    val dayName: String,
    val dayNumber: String,
    val isToday: Boolean
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SchoolRepository
) : ViewModel() {

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _weekDays = MutableStateFlow<List<CalendarDay>>(emptyList())
    val weekDays: StateFlow<List<CalendarDay>> = _weekDays.asStateFlow()

    init {
        generateWeekDays()
        loadLessons(_selectedDate.value)
    }

    private fun generateWeekDays() {
        val today = LocalDate.now()
        val monday = today.minusDays(today.dayOfWeek.value.toLong() - 1)

        val days = (0..6).map { i ->
            val date = monday.plusDays(i.toLong())
            CalendarDay(
                date = date,
                dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).uppercase(),
                dayNumber = date.dayOfMonth.toString(),
                isToday = date == today
            )
        }
        _weekDays.value = days
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadLessons(date)
    }

    private fun loadLessons(date: LocalDate) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val millis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()

            repository.getLessonsForDate(millis).collect { dailyLessons ->
                _lessons.value = dailyLessons
            }
        }
    }

    private fun loadLessonsForToday() {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val todayMillis = LocalDate.now().atStartOfDay(zoneId).toInstant().toEpochMilli()

            repository.getLessonsForDate(todayMillis).collect { dailyLessons ->
                _lessons.value = dailyLessons
            }
        }
    }
}