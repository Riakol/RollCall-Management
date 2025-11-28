package com.riakol.rollcall.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.model.Student
import com.riakol.domain.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ClassesViewModel @Inject constructor(
    private val repository: SchoolRepository
) : ViewModel() {

    private val _classes = MutableStateFlow<List<SchoolClass>>(emptyList())
    val classes: StateFlow<List<SchoolClass>> = _classes.asStateFlow()

    private val _allStudents = MutableStateFlow<Map<Char, List<Student>>>(emptyMap())
    val allStudents: StateFlow<Map<Char, List<Student>>> = _allStudents.asStateFlow()

    private val _subjects = MutableStateFlow<List<String>>(emptyList())
    val subjects: StateFlow<List<String>> = _subjects.asStateFlow()

    init {
        loadClasses()
        loadAllStudents()
        loadSubjects()
    }

    private fun loadClasses() {
        viewModelScope.launch {
            repository.getAllClasses().collect {
                _classes.value = it
            }
        }
    }

    private fun loadAllStudents() {
        viewModelScope.launch {
            repository.getAllStudents().collect { students ->
                val grouped = students.groupBy { student ->
                    student.lastName.firstOrNull()?.uppercaseChar() ?: '#'
                }.toSortedMap()
                _allStudents.value = grouped
            }
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            repository.getAllSubjects().collect {
                _subjects.value = it
            }
        }
    }

    fun addClass(name: String, description: String) {
        viewModelScope.launch {
            repository.createClass(name, description)
        }
    }

    fun updateClass(id: Long, name: String, description: String) {
        viewModelScope.launch {
            repository.updateClass(id, name, description)
        }
    }

    fun deleteClass(id: Long) {
        viewModelScope.launch {
            repository.deleteClass(id)
        }
    }
}