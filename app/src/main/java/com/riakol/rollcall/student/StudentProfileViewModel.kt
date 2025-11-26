package com.riakol.rollcall.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riakol.domain.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    private val repository: SchoolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val studentId: Long = checkNotNull(savedStateHandle["studentId"]).toString().toLongOrNull() ?: 0L

    private val _studentName = MutableStateFlow("")
    val studentName: StateFlow<String> = _studentName.asStateFlow()

    private val _studentInfo = MutableStateFlow("")
    val studentInfo: StateFlow<String> = _studentInfo.asStateFlow()

    // State для заметок учителя
    private val _teacherNotes = MutableStateFlow<String?>(null)
    val teacherNotes: StateFlow<String?> = _teacherNotes.asStateFlow()

    init {
        loadStudent()
    }

    fun refresh() {
        loadStudent()
    }

    private fun loadStudent() {
        viewModelScope.launch {
            val student = repository.getStudentById(studentId) ?: return@launch
            _studentName.value = "${student.lastName} ${student.firstName}"
            _studentInfo.value = "${student.className ?: "Класс не указан"} • ${student.age} лет"
            _teacherNotes.value = student.teacherNotes
        }
    }
}