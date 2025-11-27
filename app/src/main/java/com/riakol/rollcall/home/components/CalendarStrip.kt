package com.riakol.rollcall.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riakol.rollcall.home.CalendarDay
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusGreen
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite
import java.time.LocalDate

@Composable
fun CalendarStrip(
    weekDays: List<CalendarDay>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { day ->
            DayItem(
                day = day,
                isSelected = day.date == selectedDate,
                onClick = { onDateSelected(day.date) }
            )
        }
    }
}

@Composable
fun DayItem(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isToday = day.isToday

    val shape = RoundedCornerShape(24.dp)
    val backgroundColor = if (isSelected) Color.White.copy(alpha = 0.05f) else Color.Transparent
    val borderColor = if (isSelected) TextWhite else Color.Transparent


    Column(
        modifier = Modifier
            .clip(shape)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = borderColor,
                shape = shape
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayName,
            color = TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isSelected && isToday) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.dayNumber,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = day.dayNumber,
                color = if (isSelected) TextWhite else TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(if(isToday) PrimaryBlue else StatusGreen, CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}