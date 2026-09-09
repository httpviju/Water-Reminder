package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HomeScreen
import com.example.ui.HistoryScreen
import com.example.ui.SettingsScreen
import com.example.ui.WaterViewModel
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.WaterReminderTheme

sealed class Screen(val index: Int, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val testTag: String) {
    object Home : Screen(0, "Home", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop, "nav_item_home")
    object History : Screen(1, "History", Icons.Filled.BarChart, Icons.Outlined.BarChart, "nav_item_history")
    object Settings : Screen(2, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_item_settings")

    companion object {
        val items = listOf(Home, History, Settings)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val waterViewModel: WaterViewModel = viewModel()
            val uiState by waterViewModel.uiState.collectAsStateWithLifecycle()

            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (uiState.darkModeSetting) {
                "dark" -> true
                "light" -> false
                else -> true // Spotify iconic dark aesthetic by default
            }

            WaterReminderTheme(darkTheme = isDarkTheme) {
                WaterApp(
                    viewModel = waterViewModel,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
fun WaterApp(
    viewModel: WaterViewModel,
    uiState: com.example.ui.WaterUiState,
    modifier: Modifier = Modifier
) {
    var selectedScreenIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                Screen.items.forEach { screen ->
                    val isSelected = selectedScreenIndex == screen.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedScreenIndex = screen.index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(screen.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreenIndex) {
                0 -> HomeScreen(
                    uiState = uiState,
                    onAddWater = { amount -> viewModel.addWater(amount) },
                    onDeleteWater = { id -> viewModel.removeWaterLog(id) },
                    onToggleReminder = { enabled -> viewModel.toggleReminder(enabled) }
                )
                1 -> HistoryScreen(
                    uiState = uiState,
                    onDeleteWater = { id -> viewModel.removeWaterLog(id) }
                )
                2 -> SettingsScreen(
                    uiState = uiState,
                    onSetDailyTarget = { target -> viewModel.setDailyTarget(target) },
                    onSetReminderInterval = { interval -> viewModel.setReminderInterval(interval) },
                    onToggleReminder = { enabled -> viewModel.toggleReminder(enabled) },
                    onToggleWaterAlarmSound = { enabled -> viewModel.toggleWaterAlarmSound(enabled) },
                    onTestWaterAlarmSound = { viewModel.playWaterAlarmSound() },
                    onSetDarkMode = { mode -> viewModel.setDarkModeSetting(mode) },
                    onResetData = { viewModel.resetAllData() }
                )
            }
        }
    }
}

// Retained for screenshot test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
