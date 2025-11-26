package com.riakol.rollcall.classes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.riakol.rollcall.classes.components.AddClassBottomSheet
import com.riakol.rollcall.classes.components.ClassCard
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray

@Composable
fun ClassesScreen(
    onClassClick: (Long) -> Unit,
    viewModel: ClassesViewModel = hiltViewModel()
) {
    val classes by viewModel.classes.collectAsState()
    var showAddClassSheet by remember { mutableStateOf(false) }

    if (showAddClassSheet) {
        AddClassBottomSheet(
            onDismiss = { showAddClassSheet = false },
            onSave = { name, description ->
                viewModel.addClass(name, description)
                showAddClassSheet = false
            }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddClassSheet = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Class")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Мои Классы",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Поиск ученика или класса...", color = TextGray) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextGray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Filter Button
                Surface(
                    shape = CircleShape,
                    color = SurfaceDark,
                    modifier = Modifier.size(56.dp).clickable { }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = TextGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(classes) { schoolClass ->
                    ClassCard(schoolClass, onClick = { onClassClick(schoolClass.id) })
                }
            }
        }
    }
}
