package com.riakol.rollcall.attendance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.riakol.domain.model.AttendanceType
import com.riakol.rollcall.classes.AvatarColors
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusGreen
import com.riakol.rollcall.ui.theme.StatusRed
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    navController: NavController,
    viewModel: AttendanceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    if (viewModel.commentDialogStudentId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.closeCommentDialog() },
            containerColor = SurfaceDark,
            title = { Text("Комментарий", color = TextWhite) },
            text = {
                OutlinedTextField(
                    value = viewModel.currentCommentText,
                    onValueChange = { viewModel.currentCommentText = it },
                    placeholder = { Text("Причина отсутствия...", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = TextGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.saveComment() }) {
                    Text("Сохранить", color = PrimaryBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeCommentDialog() }) {
                    Text("Отмена", color = TextGray)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.lessonTitle,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = uiState.lessonDate,
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark)
                    .padding(16.dp)
            ) {
                val presentCount = uiState.students.count { it.status == AttendanceType.PRESENT }
                val total = uiState.students.size

                Button(
                    onClick = { viewModel.saveAttendance { navController.popBackStack() } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)), // Темно-серый/синий как на макете
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Сохранить ($presentCount/$total)",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.markAllPresent() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, StatusGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGreen)
                ) {
                    Text("Отметить всех присутствующими")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.students) { item ->
                        AttendanceStudentRow(
                            item = item,
                            onStatusChange = { status -> viewModel.setStatus(item.student.id, status) },
                            onCommentClick = { viewModel.openCommentDialog(item.student.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceStudentRow(
    item: StudentAttendanceItem,
    onStatusChange: (AttendanceType) -> Unit,
    onCommentClick: () -> Unit
) {
    val student = item.student
    val isPresent = item.status == AttendanceType.PRESENT
    val isAbsent = item.status == AttendanceType.ABSENT
    val hasComment = !item.comment.isNullOrBlank()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val avatarColor = AvatarColors[student.id.hashCode().absoluteValue % AvatarColors.size]
        Surface(
            shape = CircleShape,
            color = Color(0xFF2C2C2C),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${student.lastName.firstOrNull() ?: ""}${student.firstName.firstOrNull() ?: ""}",
                    color = TextGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = student.lastName,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = student.firstName,
                color = TextGray,
                fontSize = 14.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = { onStatusChange(AttendanceType.PRESENT) },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isPresent) StatusGreen else Color(0xFF2C2C2C),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Present",
                    tint = if (isPresent) Color.White else TextGray
                )
            }

            IconButton(
                onClick = { onStatusChange(AttendanceType.ABSENT) },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isAbsent) StatusRed else Color(0xFF2C2C2C),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Absent",
                    tint = if (isAbsent) Color.White else TextGray
                )
            }

            IconButton(
                onClick = onCommentClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (hasComment) PrimaryBlue else Color(0xFF2C2C2C),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Comment",
                    tint = if (hasComment) Color.White else TextGray
                )
            }
        }
    }
}