package com.riakol.rollcall.attendance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riakol.domain.model.AttendanceRecord
import com.riakol.domain.model.AttendanceType
import com.riakol.domain.model.Student
import com.riakol.domain.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class AttendanceUiState(
    val lessonTitle: String = "",
    val lessonDate: String = "",
    val students: List<StudentAttendanceItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

data class StudentAttendanceItem(
    val student: Student,
    val status: AttendanceType? = null,
    val comment: String? = null
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: SchoolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lessonId: Long = checkNotNull(savedStateHandle["lessonId"]).toString().toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow(AttendanceUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    var commentDialogStudentId by mutableStateOf<Long?>(null)
    var currentCommentText by mutableStateOf("")

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val lesson = repository.getLessonById(lessonId) ?: return@launch

                val formatter = DateTimeFormatter.ofPattern("d MMMM • HH:mm")
                val dateStr = "${lesson.startTime.format(formatter)}"
                val title = "${lesson.subjectName}, ${lesson.className}"

                val students = repository.getStudentsByClass(lesson.classId)

                val items = students.map { student ->
                    StudentAttendanceItem(
                        student = student,
                        status = null
                    )
                }

                _uiState.update {
                    it.copy(
                        lessonTitle = title,
                        lessonDate = dateStr,
                        students = items,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun markAllPresent() {
        _uiState.update { state ->
            state.copy(
                students = state.students.map { it.copy(status = AttendanceType.PRESENT) }
            )
        }
    }

    fun setStatus(studentId: Long, status: AttendanceType) {
        _uiState.update { state ->
            state.copy(
                students = state.students.map { item ->
                    if (item.student.id == studentId) {
                        val newStatus = if (item.status == status) null else status
                        item.copy(status = newStatus)
                    } else item
                }
            )
        }
    }

    fun openCommentDialog(studentId: Long) {
        val item = _uiState.value.students.find { it.student.id == studentId }
        currentCommentText = item?.comment ?: ""
        commentDialogStudentId = studentId
    }

    fun saveComment() {
        commentDialogStudentId?.let { id ->
            _uiState.update { state ->
                state.copy(
                    students = state.students.map {
                        if (it.student.id == id) it.copy(comment = currentCommentText) else it
                    }
                )
            }
        }
        closeCommentDialog()
    }

    fun closeCommentDialog() {
        commentDialogStudentId = null
        currentCommentText = ""
    }

    fun saveAttendance(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val records = _uiState.value.students.mapNotNull { item ->
                item.status?.let { status ->
                    AttendanceRecord(
                        studentId = item.student.id,
                        lessonId = lessonId,
                        status = status,
                        comment = item.comment
                    )
                }
            }

            repository.saveAttendance(lessonId, records)
            onSuccess()
        }
    }
}