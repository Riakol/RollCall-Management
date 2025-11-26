package com.riakol.rollcall.group.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riakol.domain.model.Student
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray

@Composable
fun StudentItem(student: Student) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Edit Student */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Аватар
        Surface(
            shape = CircleShape,
            color = SurfaceDark,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (student.photoUrl != null) {
                    // AsyncImage here
                } else {
                    Text(
                        text = "${student.firstName.first()}${student.lastName.first()}",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Инфо
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${student.lastName} ${student.firstName}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (student.phoneNumber != null) {
                Text(
                    text = student.phoneNumber!!,
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        }

        IconButton(onClick = { /* Edit */ }) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = TextGray, modifier = Modifier.size(20.dp))
        }
    }
}