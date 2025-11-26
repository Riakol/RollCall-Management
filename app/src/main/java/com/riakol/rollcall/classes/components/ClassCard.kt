package com.riakol.rollcall.classes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.riakol.domain.model.SchoolClass
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusGreen
import com.riakol.rollcall.ui.theme.StatusRed
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite

@Composable
fun ClassCard(
    schoolClass: SchoolClass,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val stripeColor = when (schoolClass.id.toInt() % 3) {
        0 -> StatusGreen
        1 -> PrimaryBlue
        else -> Color(0xFFAB47BC)
    }

    var showMenu by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(stripeColor)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = schoolClass.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = schoolClass.studentCount.toString(), color = TextWhite, fontSize = 16.sp)
                    }
                }

                Text(
                    text = schoolClass.description ?: "Нет описания",
                    color = TextGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Avatars Stack
                Row(verticalAlignment = Alignment.CenterVertically) {
                    schoolClass.previewStudents.forEachIndexed { index, student ->
                        Surface(
                            shape = CircleShape,
                            color = Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = (-10 * index).dp)
                                .zIndex((3 - index).toFloat())
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    val overflowCount = schoolClass.studentCount - schoolClass.previewStudents.size
                    if (overflowCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceDark,
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = (-10 * schoolClass.previewStudents.size).dp)
                                .zIndex(0f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+$overflowCount", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Menu Button
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.padding(top = 8.dp, end = 4.dp)
                ) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options", tint = TextGray)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = SurfaceDark
                ) {
                    DropdownMenuItem(
                        text = { Text("Переименовать", color = TextWhite) },
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
    }
}