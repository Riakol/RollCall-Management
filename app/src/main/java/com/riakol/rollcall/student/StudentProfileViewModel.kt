package com.riakol.rollcall.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val studentId: Long = checkNotNull(savedStateHandle["studentId"])

    // моковые данные для UI
    private val _studentName = MutableStateFlow("Авдеев Борис")
    val studentName: StateFlow<String> = _studentName.asStateFlow()

    private val _studentInfo = MutableStateFlow("9 'Б' класс • 15 лет")
    val studentInfo: StateFlow<String> = _studentInfo.asStateFlow()
}