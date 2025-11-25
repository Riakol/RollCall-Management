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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SchoolRepository
) : ViewModel() {

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    init {
        loadLessonsForToday()
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