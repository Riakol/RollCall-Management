package com.riakol.rollcall.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.riakol.domain.model.Lesson
import com.riakol.rollcall.home.components.CalendarStrip
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusGreen
import com.riakol.rollcall.ui.theme.StatusRed
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    navController: androidx.navigation.NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsState()
    val weekDays by viewModel.weekDays.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    val isArchiveMode = selectedDate.isBefore(LocalDate.now())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Расписание",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )
            IconButton(
                onClick = {
                    val dateEpoch = selectedDate.toEpochDay()
                    navController.navigate("lesson_edit/0?date=$dateEpoch")
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2C2C2C), CircleShape)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Lesson", tint = PrimaryBlue)
            }
        }

        // --- CALENDAR STRIP ---
        CalendarStrip(
            weekDays = weekDays,
            selectedDate = selectedDate,
            onDateSelected = { viewModel.selectDate(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- CONTENT ---
        Crossfade(targetState = lessons, label = "List") { currentLessons ->
            if (currentLessons.isEmpty()) {
                EmptyStateView(
                    onAddClick = {
                        val dateEpoch = selectedDate.toEpochDay()
                        navController.navigate("lesson_edit/0?date=$dateEpoch")
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(currentLessons) { lesson ->
                        LessonItemWrapper(
                            lesson = lesson,
                            isArchive = isArchiveMode,
                            onEdit = { navController.navigate("lesson_edit/${lesson.id}") },
                            onDelete = { /* TODO: viewModel.deleteLesson(lesson.id) */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.BeachAccess, contentDescription = null, tint = TextGray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("На сегодня уроков нет", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Добавить урок")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LessonItemWrapper(
    lesson: Lesson,
    isArchive: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Box {
        // Обертка для обработки LongClick
        Box(
            modifier = Modifier.combinedClickable(
                onClick = {},
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showMenu = true
                }
            )
        ) {
            if (isArchive) {
                ArchiveLessonItem(lesson)
            } else {
                ActiveLessonItem(lesson)
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            containerColor = SurfaceDark
        ) {
            DropdownMenuItem(
                text = { Text("Редактировать", color = TextWhite) },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null, tint = TextWhite) },
                onClick = {
                    showMenu = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Удалить", color = StatusRed) },
                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = StatusRed) },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
fun ActiveLessonItem(lesson: Lesson) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${lesson.startTime.toLocalTime()} - ${lesson.endTime.toLocalTime()}",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                val statusText = if (lesson.isFinished) "Завершен" else "Идет сейчас"
                val statusColor = if (lesson.isFinished) TextGray else PrimaryBlue
                val statusBg = if (lesson.isFinished) Color.DarkGray else Color(0xFF1A3B5A)

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${lesson.subjectName}, ${lesson.className}",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Каб. ${lesson.roomNumber}",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Navigate to Attendance */ },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Отметить", color = Color.White)
            }
        }
    }
}

@Composable
fun ArchiveLessonItem(lesson: Lesson) {
    val progress = if (lesson.totalStudents > 0) lesson.presentCount.toFloat() / lesson.totalStudents else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Subject and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${lesson.subjectName}, ${lesson.className}",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Завершено",
                        color = StatusGreen,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Center
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${lesson.presentCount}",
                        color = TextWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = " / ${lesson.totalStudents}",
                        color = TextGray,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
                Text(
                    text = "Присутствовало",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = StatusGreen,
                trackColor = Color(0xFF333333),
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { /* TODO: Edit Attendance */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TextGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Редактировать")
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.BeachAccess,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Уроков нет",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}