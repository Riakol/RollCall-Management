package com.riakol.rollcall.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.repository.SchoolRepository
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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

    init {
        loadClasses()
        if (lessonId != 0L) {
            loadLesson()
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
            if (selectedClassId == null || subjectName.isBlank()) return@launch

            try {
                val startT = LocalTime.parse(startTimeStr)
                val endT = LocalTime.parse(endTimeStr)

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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLessonScreen(
    navController: NavController,
    viewModel: AddEditLessonViewModel = hiltViewModel()
) {
    val classes by viewModel.classes.collectAsState()
    var showClassDropdown by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Новый урок", color = TextWhite, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Выбор Класса", color = TextGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp).clickable { showClassDropdown = true }
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                        val selectedClassName = classes.find { it.id == viewModel.selectedClassId }?.name
                        Text(
                            text = selectedClassName ?: "Выберите класс",
                            color = if (selectedClassName != null) TextWhite else TextGray,
                            fontSize = 16.sp
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
                                viewModel.selectedClassId = schoolClass.id
                                showClassDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Предмет", color = TextGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.subjectName,
                onValueChange = { viewModel.subjectName = it },
                placeholder = { Text("Алгебра", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Время и Кабинет
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Начало", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeInput(value = viewModel.startTimeStr, onValueChange = { viewModel.startTimeStr = it })
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Конец", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeInput(value = viewModel.endTimeStr, onValueChange = { viewModel.endTimeStr = it })
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Кабинет", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.roomNumber,
                        onValueChange = { viewModel.roomNumber = it },
                        placeholder = { Text("301", color = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Повторение
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Повторять еженедельно", color = TextWhite, fontSize = 16.sp)
                        Switch(
                            checked = viewModel.isRepeatEnabled,
                            onCheckedChange = { viewModel.isRepeatEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue,
                                uncheckedThumbColor = TextGray,
                                uncheckedTrackColor = BackgroundDark
                            )
                        )
                    }

                    if (viewModel.isRepeatEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                            days.forEachIndexed { index, day ->
                                val dayNum = index + 1
                                val isSelected = viewModel.selectedDays.contains(dayNum)
                                DayToggle(text = day, isSelected = isSelected) {
                                    viewModel.toggleDay(dayNum)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Цвет карточки
            Text("Цвет карточки", color = TextGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val colors = listOf(
                    "#7986CB", // Indigo
                    "#F48FB1", // Pink
                    "#A5D6A7", // Green
                    "#FFF59D", // Yellow
                    "#FFCC80", // Orange
                    "#90CAF9"  // Blue
                )
                colors.forEach { hex ->
                    ColorCircle(
                        colorHex = hex,
                        isSelected = viewModel.selectedColor == hex,
                        onClick = { viewModel.selectedColor = hex }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveLesson { navController.popBackStack() } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Сохранить в расписание", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TimeInput(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun DayToggle(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) PrimaryBlue else Color(0xFF2C2C2C))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) Color.White else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ColorCircle(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(2.dp, if (isSelected) PrimaryBlue else Color.Transparent, CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(Color(android.graphics.Color.parseColor(colorHex)))
            .clickable { onClick() }
    )
}