package com.riakol.rollcall.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riakol.domain.model.SchoolClass
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

    init {
        loadClasses()
    }

    private fun loadClasses() {
        viewModelScope.launch {
            repository.getAllClasses().collect {
                _classes.value = it
            }
        }
    }

    fun addClass(name: String, description: String) {
        viewModelScope.launch {
            repository.createClass(name, description)
        }
    }
}