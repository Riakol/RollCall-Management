package com.riakol.rollcall.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.riakol.domain.model.Lesson
import com.riakol.rollcall.home.components.CalendarStrip
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusGreen
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
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
        Text(
            text = "История отметок",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        )

        // --- CALENDAR STRIP ---
        CalendarStrip(
            weekDays = weekDays,
            selectedDate = selectedDate,
            onDateSelected = { viewModel.selectDate(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- CONTENT ---
        Crossfade(targetState = lessons, label = "LessonsList") { currentLessons ->
            if (currentLessons.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(currentLessons) { lesson ->
                        if (isArchiveMode) {
                            ArchiveLessonItem(lesson)
                        } else {
                            ActiveLessonItem(lesson)
                        }
                    }
                }
            }
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
                Text(text = "Начать урок", color = Color.White)
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