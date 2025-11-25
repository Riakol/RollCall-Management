package com.riakol.rollcall.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Главная", Icons.Default.Home)
    object Classes : BottomNavItem("classes", "Классы", Icons.Default.School)
    object Reports : BottomNavItem("reports", "Отчеты", Icons.Default.BarChart)
    object Profile : BottomNavItem("profile", "Профиль", Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Classes,
    BottomNavItem.Reports,
    BottomNavItem.Profile
)