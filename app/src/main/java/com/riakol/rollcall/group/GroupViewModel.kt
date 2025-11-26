package com.riakol.rollcall.group

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riakol.domain.model.Student
import com.riakol.domain.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: SchoolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classId: Long = checkNotNull(savedStateHandle["classId"])

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _className = MutableStateFlow("Загрузка...")
    val className: StateFlow<String> = _className.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            _students.value = repository.getStudentsByClass(classId)

            val schoolClass = repository.getClassById(classId)
            _className.value = schoolClass?.name ?: "Класс"
        }
    }

    fun addStudent(firstName: String, lastName: String, middleName: String, phone: String) {
        viewModelScope.launch {
            repository.createStudent(
                classId = classId,
                firstName = firstName,
                lastName = lastName,
                middleName = middleName.ifBlank { null },
                phone = phone.ifBlank { null }
            )
            loadStudents()
        }
    }
}