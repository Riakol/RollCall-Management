package com.riakol.rollcall.classes

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.riakol.domain.model.SchoolClass
import com.riakol.domain.model.Student
import com.riakol.rollcall.classes.components.AddClassBottomSheet
import com.riakol.rollcall.classes.components.ClassCard
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.StatusRed
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite
import kotlin.math.absoluteValue

val AvatarColors = listOf(
    Color(0xFFF44336), // Red
    Color(0xFFE91E63), // Pink
    Color(0xFF9C27B0), // Purple
    Color(0xFF673AB7), // Deep Purple
    Color(0xFF3F51B5), // Indigo
    Color(0xFF2196F3), // Blue
    Color(0xFF03A9F4), // Light Blue
    Color(0xFF00BCD4), // Cyan
    Color(0xFF009688), // Teal
    Color(0xFF4CAF50), // Green
    Color(0xFF8BC34A), // Light Green
    Color(0xFFFFC107), // Amber
    Color(0xFFFF9800), // Orange
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF795548)  // Brown
)

@Composable
fun ClassesScreen(
    navController: NavController,
    onClassClick: (Long) -> Unit,
    viewModel: ClassesViewModel = hiltViewModel()
) {
    val classes by viewModel.classes.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val allStudentsGrouped by viewModel.allStudents.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    var showAddClassSheet by remember { mutableStateOf(false) }
    var classToEdit by remember { mutableStateOf<SchoolClass?>(null) }
    var classToDelete by remember { mutableStateOf<SchoolClass?>(null) }

    // --- BOTTOM SHEET LOGIC (Create & Edit) ---
    if (showAddClassSheet) {
        AddClassBottomSheet(
            existingSubjects = subjects,
            onDismiss = { showAddClassSheet = false },
            onSave = { name, description ->
                viewModel.addClass(name, description)
                showAddClassSheet = false
            }
        )
    }

    classToEdit?.let { schoolClass ->
        AddClassBottomSheet(
            initialName = schoolClass.name,
            initialDescription = schoolClass.description ?: "",
            existingSubjects = subjects,
            onDismiss = { classToEdit = null },
            onSave = { name, description ->
                viewModel.updateClass(schoolClass.id, name, description)
                classToEdit = null
            }
        )
    }

    // --- DELETE DIALOG ---
    classToDelete?.let { schoolClass ->
        AlertDialog(
            onDismissRequest = { classToDelete = null },
            title = { Text("Удалить класс?", color = TextWhite) },
            text = { Text("Класс \"${schoolClass.name}\" и все его ученики будут удалены.", color = TextGray) },
            containerColor = SurfaceDark,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteClass(schoolClass.id)
                    classToDelete = null
                }) {
                    Text("Удалить", color = StatusRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { classToDelete = null }) {
                    Text("Отмена", color = PrimaryBlue)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        showAddClassSheet = true
                    } else {
                        navController.navigate("student_edit/0")
                    }
                },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- TOP TOGGLE ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(SurfaceDark, RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                TabButton("Классы", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                TabButton("Все ученики", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Поиск...", color = TextGray) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextGray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == 0) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(classes) { schoolClass ->
                        ClassCard(
                            schoolClass = schoolClass,
                            onClick = { onClassClick(schoolClass.id) },
                            onEdit = { classToEdit = schoolClass },
                            onDelete = { classToDelete = schoolClass }
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    allStudentsGrouped.forEach { (letter, students) ->
                        item {
                            Text(letter.toString(), color = TextGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
                        }
                        item {
                            Surface(color = SurfaceDark, shape = RoundedCornerShape(16.dp)) {
                                Column {
                                    students.forEachIndexed { index, student ->
                                        AllStudentsItem(student) { navController.navigate("student/${student.id}") }
                                        if (index < students.size - 1) Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(BackgroundDark.copy(0.5f)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isSelected) PrimaryBlue else Color.Transparent,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else TextGray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AllStudentsItem(student: Student, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val avatarColor = remember(student.id) { if (student.id == 0L) Color.Gray else AvatarColors[student.id.hashCode().absoluteValue % AvatarColors.size] }
        Surface(shape = CircleShape, color = avatarColor, modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${student.lastName.firstOrNull() ?: ""}${student.firstName.firstOrNull() ?: ""}",
                    color = Color.White, fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text("${student.lastName} ${student.firstName}", color = Color.White, modifier = Modifier.weight(1f))
        Surface(color = Color(0xFF2C2C2C), shape = RoundedCornerShape(8.dp)) {
            Text(student.className ?: "?", color = PrimaryBlue, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}