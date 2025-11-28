package com.riakol.rollcall.lesson

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddEditLessonViewModel @Inject constructor(
    private val repository: SchoolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val lessonId: Long = checkNotNull(savedStateHandle["lessonId"]).toString().toLongOrNull() ?: 0L
    val dateEpoch: Long = checkNotNull(savedStateHandle["date"]).toString().toLongOrNull() ?: 0L

    private val initialDate = if (dateEpoch > 0) LocalDate.ofEpochDay(dateEpoch) else LocalDate.now()

    private val _classes = MutableStateFlow<List<SchoolClass>>(emptyList())
    val classes = _classes.asStateFlow()

    var selectedClassId by mutableStateOf<Long?>(null)
    var subjectName by mutableStateOf("")
    var startTimeStr by mutableStateOf("08:30")
    var endTimeStr by mutableStateOf("09:15")
    var roomNumber by mutableStateOf("")
    var isRepeatEnabled by mutableStateOf(false)
    var selectedDays by mutableStateOf(setOf<Int>()) // 1=Mon, 7=Sun
    var selectedColor by mutableStateOf<String?>(null)

    private val _subjects = MutableStateFlow<List<String>>(emptyList())
    val subjects = _subjects.asStateFlow()

    var timeError by mutableStateOf<String?>(null)

    init {
        loadClasses()
        loadSubjects()
        if (lessonId != 0L) {
            loadLesson()
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            repository.getAllSubjects().collect { _subjects.value = it }
        }
    }

    private fun loadClasses() {
        viewModelScope.launch {
            repository.getAllClasses().collect { _classes.value = it }
        }
    }

    private fun loadLesson() {
        viewModelScope.launch {
            val lesson = repository.getLessonById(lessonId) ?: return@launch
            val classObj = _classes.value.find { it.name == lesson.className }
            selectedClassId = classObj?.id // Note: In real app, lesson stores classId, handled by repo
            subjectName = lesson.subjectName
            startTimeStr = lesson.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            endTimeStr = lesson.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            roomNumber = lesson.roomNumber
            selectedColor = lesson.color
        }
    }

    fun toggleDay(day: Int) {
        selectedDays = if (selectedDays.contains(day)) {
            selectedDays - day
        } else {
            selectedDays + day
        }
    }

    fun saveLesson(onSuccess: () -> Unit) {
        viewModelScope.launch {
            timeError = null
            if (selectedClassId == null || subjectName.isBlank()) return@launch

            try {
                val startT = LocalTime.parse(startTimeStr)
                val endT = LocalTime.parse(endTimeStr)

                if (!endT.isAfter(startT)) {
                    timeError = "Конец урока должен быть позже начала"
                    return@launch
                }

                val zoneId = ZoneId.systemDefault()
                val startMillis = initialDate.atTime(startT).atZone(zoneId).toInstant().toEpochMilli()
                val endMillis = initialDate.atTime(endT).atZone(zoneId).toInstant().toEpochMilli()

                val hasOverlap = repository.hasLessonOverlap(
                    classId = selectedClassId!!,
                    startMillis = startMillis,
                    endMillis = endMillis,
                    excludeLessonId = lessonId
                )

                if (hasOverlap) {
                    timeError = "В это время у класса уже есть урок!"
                    return@launch
                }
                repository.saveLesson(
                    lessonId = lessonId,
                    classId = selectedClassId!!,
                    subjectName = subjectName,
                    date = initialDate,
                    startTime = startT,
                    endTime = endT,
                    room = roomNumber,
                    color = selectedColor,
                    repeatDays = if (isRepeatEnabled) selectedDays.toList() else emptyList()
                )
                onSuccess()
            } catch (e: Exception) {
                timeError = "Ошибка сохранения: ${e.message}"
            }
        }
    }
}