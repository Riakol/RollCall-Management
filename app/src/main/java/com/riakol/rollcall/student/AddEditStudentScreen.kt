package com.riakol.rollcall.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.repository.SchoolRepository
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusRed
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditStudentUiState(
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val selectedClassId: Long? = null,
    val phone: String = "",
    val notes: String = "",
    val health: String = "",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditStudentViewModel @Inject constructor(
    private val repository: SchoolRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    val studentId: Long = checkNotNull(savedStateHandle["studentId"]).toString().toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow(AddEditStudentUiState(isEditMode = studentId != 0L))
    val uiState = _uiState.asStateFlow()

    private val _classes = MutableStateFlow<List<SchoolClass>>(emptyList())
    val classes = _classes.asStateFlow()

    init {
        loadClasses()
        if (studentId != 0L) loadStudent(studentId)
    }

    private fun loadClasses() {
        viewModelScope.launch {
            repository.getAllClasses().collect { list ->
                _classes.value = list
            }
        }
    }

    private fun loadStudent(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val student = repository.getStudentById(id)
            if (student != null) {
                _uiState.update {
                    it.copy(
                        firstName = student.firstName,
                        lastName = student.lastName,
                        middleName = student.middleName ?: "",
                        selectedClassId = student.classId,
                        phone = student.phoneNumber ?: "",
                        notes = student.teacherNotes ?: "",
                        health = student.healthInfo ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Студент не найден") }
            }
        }
    }

    fun onFirstNameChange(value: String) = _uiState.update { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = _uiState.update { it.copy(lastName = value) }
    fun onMiddleNameChange(value: String) = _uiState.update { it.copy(middleName = value) }
    fun onClassSelected(id: Long) = _uiState.update { it.copy(selectedClassId = id) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }
    fun onHealthChange(value: String) = _uiState.update { it.copy(health = value) }

    fun saveStudent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.selectedClassId == null) {
                _uiState.update { it.copy(error = "Выберите класс") }
                return@launch
            }
            if (state.firstName.isBlank() || state.lastName.isBlank()) {
                _uiState.update { it.copy(error = "Заполните обязательные поля") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            try {
                repository.saveStudentFull(
                    studentId = studentId,
                    classId = state.selectedClassId,
                    firstName = state.firstName,
                    lastName = state.lastName,
                    middleName = state.middleName.ifBlank { null },
                    birthDate = 0L,
                    phone = state.phone.ifBlank { null },
                    healthInfo = state.health.ifBlank { null },
                    notes = state.notes.ifBlank { null }
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun deleteStudent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (studentId != 0L) {
                repository.deleteStudent(studentId)
                onSuccess()
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentScreen(
    navController: NavController,
    viewModel: AddEditStudentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val classes by viewModel.classes.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showClassDropdown by remember { mutableStateOf(false) }

    // Обработка ошибок (SnackBar)
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить студента?", color = TextWhite) },
            text = { Text("Это действие нельзя отменить.", color = TextGray) },
            containerColor = SurfaceDark,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteStudent {
                        showDeleteDialog = false
                        navController.popBackStack()
                        navController.popBackStack()
                    }
                }) {
                    Text("Удалить", color = StatusRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена", color = PrimaryBlue)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Редактировать профиль" else "Новый ученик",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextWhite)
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = TextWhite)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                containerColor = SurfaceDark
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Удалить студента", color = StatusRed) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(BackgroundDark).padding(16.dp)) {
                if (uiState.isEditMode) {
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Удалить студента", color = StatusRed, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { viewModel.saveStudent { navController.popBackStack() } },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(25.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Сохранить изменения", fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- АВАТАР ---
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        tint = Color.LightGray
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = 30.dp, y = (-5).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ПОЛЯ ---
            InputLabel("Фамилия")
            AppTextField(value = uiState.lastName, onValueChange = viewModel::onLastNameChange, placeholder = "Введите фамилию")

            InputLabel("Имя")
            AppTextField(value = uiState.firstName, onValueChange = viewModel::onFirstNameChange, placeholder = "Введите имя")

            InputLabel("Отчество (если есть)")
            AppTextField(value = uiState.middleName, onValueChange = viewModel::onMiddleNameChange, placeholder = "Введите отчество")

            InputLabel("Академическая информация")
            Text("Класс", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))

            Box {
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp).clickable { showClassDropdown = true }
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                        val selectedClassName = classes.find { it.id == uiState.selectedClassId }?.name
                        Text(
                            text = selectedClassName ?: "Выберите класс",
                            color = if (selectedClassName != null) TextWhite else TextGray
                        )
                    }
                }
                DropdownMenu(
                    expanded = showClassDropdown,
                    onDismissRequest = { showClassDropdown = false },
                    containerColor = SurfaceDark
                ) {
                    classes.forEach { schoolClass ->
                        DropdownMenuItem(
                            text = { Text(schoolClass.name, color = TextWhite) },
                            onClick = {
                                viewModel.onClassSelected(schoolClass.id)
                                showClassDropdown = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            InputLabel("Контакты")
            Text("Телефон ученика", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
            AppTextField(value = uiState.phone, onValueChange = viewModel::onPhoneChange, placeholder = "+7 (___) ___-__-__")

            Spacer(modifier = Modifier.height(24.dp))

            Text("Заметки", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

//            Text("Медицинские показания", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
//            AppTextField(
//                value = uiState.health,
//                onValueChange = viewModel::onHealthChange,
//                placeholder = "Аллергии, хронические заболевания...",
//                singleLine = false,
//                minLines = 3
//            )
//
//            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
//                Checkbox(checked = false, onCheckedChange = {})
//                Text("Выделять красным в профиле (Critical Alert)", color = TextWhite, fontSize = 14.sp)
//            }

                //Spacer(modifier = Modifier.height(16.dp))

            Text("Заметки для учителя", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
            AppTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                placeholder = "Личные особенности, сильные стороны...",
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        color = TextWhite,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = PrimaryBlue,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = singleLine,
        minLines = minLines
    )
}