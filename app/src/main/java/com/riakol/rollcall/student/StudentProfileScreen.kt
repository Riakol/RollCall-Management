package com.riakol.rollcall.student

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusGreen
import com.riakol.rollcall.ui.theme.StatusRed
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    navController: NavController,
    viewModel: StudentProfileViewModel = hiltViewModel()
) {
    val name by viewModel.studentName.collectAsState()
    val info by viewModel.studentInfo.collectAsState()
    val scrollState = rememberScrollState()

    val avatarGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF7B61FF), Color(0xFFE040FB))
    )

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            // Аватар
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(avatarGradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(name),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Имя
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )

            // Подзаголовок
            Text(
                text = info,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextGray
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- ACTION BUTTONS ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionCard(
                    icon = Icons.Default.Call,
                    label = "Позвонить",
                    iconTint = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                ActionCard(
                    icon = Icons.Default.ChatBubble,
                    label = "WhatsApp",
                    iconTint = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                ActionCard(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    label = "Заметка",
                    iconTint = TextGray,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- STATS CARD ---
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Статистика посещаемости",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Circular Chart
                        Box(contentAlignment = Alignment.Center) {
                            CircularChart(
                                percentage = 0.85f,
                                radius = 40.dp,
                                color = StatusGreen,
                                trackColor = StatusRed
                            )
                            Text(
                                text = "85%",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Text Stats
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatRow(label = "Посещаемость:", value = "85%", valueColor = TextWhite)
                            StatRow(label = "Пропущено уроков:", value = "4", valueColor = TextGray)
                            StatRow(label = "Опозданий:", value = "1", valueColor = TextGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- HISTORY LIST ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "История последних пропусков",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HistoryItem(date = "20 Октября", status = "Отсутствовал", detail = "(По болезни)")
                HistoryItem(date = "15 Октября", status = "Опоздание", detail = "(10 мин)")
                HistoryItem(date = "11 Октября", status = "Отсутствовал", detail = "(Ув. причина)")
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun ActionCard(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(80.dp),
        onClick = { /* TODO */ }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, color = iconTint, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatRow(label: String, value: String, valueColor: Color) {
    Row {
        Text(text = "$label ", color = TextGray, fontSize = 15.sp)
        Text(text = value, color = valueColor, fontSize = 15.sp)
    }
}

@Composable
fun HistoryItem(date: String, status: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = date, color = TextWhite, fontSize = 16.sp, modifier = Modifier.width(100.dp))
        Text(text = "—", color = TextGray, modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            text = "$status $detail",
            color = TextGray,
            fontSize = 16.sp
        )
    }
}

@Composable
fun CircularChart(
    percentage: Float,
    radius: androidx.compose.ui.unit.Dp,
    color: Color,
    trackColor: Color
) {
    Canvas(modifier = Modifier.size(radius * 2)) {
        val strokeWidth = 8.dp.toPx()

        // Track
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )

        // Progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360 * percentage,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
}

fun getInitials(name: String): String {
    val parts = name.split(" ")
    return if (parts.size >= 2) {
        "${parts[0].first()}${parts[1].first()}"
    } else {
        name.take(2).uppercase()
    }
}