package com.riakol.rollcall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.riakol.rollcall.classes.ClassesScreen
import com.riakol.rollcall.components.BottomNavigationBar
import com.riakol.rollcall.group.GroupScreen
import com.riakol.rollcall.home.HomeScreen
import com.riakol.rollcall.navigation.BottomNavItem
import com.riakol.rollcall.ui.theme.RollCallTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RollCallTheme(darkTheme = true) {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }
            composable(BottomNavItem.Classes.route) {
                ClassesScreen(
                    onClassClick = { classId ->
                        navController.navigate("group/$classId")
                    }
                )
            }
            composable(
                route = "group/{classId}",
                arguments = listOf(navArgument("classId") { type = NavType.LongType })
            ) {
                GroupScreen(navController = navController)
            }
            composable(BottomNavItem.Reports.route) {
                // Placeholder
            }
            composable(BottomNavItem.Profile.route) {
                // Placeholder
            }
        }
    }
}