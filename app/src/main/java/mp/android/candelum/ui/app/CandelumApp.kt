package mp.android.candelum.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mp.android.candelum.ui.lights.LightsScreen

@Composable
fun CandelumApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBars = MainTab.entries.any { it.route == currentRoute }

    val connection by viewModel.connection.collectAsState()
    val lights by viewModel.visibleLights.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val title by viewModel.title.collectAsState()
    var showSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showBars) {
                CandelumTopBar(
                    title = title,
                    connection = connection,
                    onTitleClick = { showSelector = true },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS)},
                )
            }
        },
        bottomBar = {
            if (showBars) {
                CandelumBottomBar(currentRoute = currentRoute) { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainTab.Control.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(MainTab.Control.route) {
                LightsScreen(
                    connection = connection,
                    lights = lights,
                    viewModel::connect,
                    viewModel::disconnect,
                    viewModel::send
                )
            }
            composable(MainTab.Modes.route) { PlaceholderScreen("Modes") }
            composable(MainTab.Automations.route) { PlaceholderScreen("Automations") }
            composable(Routes.SETTINGS) {
                Column {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    PlaceholderScreen("Settings")
                }
            }
            composable(Routes.MANAGE_ROOMS) {
                ManageRoomsScreen(
                    rooms = rooms,
                    onBack = { navController.popBackStack() },
                    onCreate = viewModel::createRoom,
                    onRename = viewModel::renameRoom,
                    onDelete = viewModel::deleteRoom,
                )
            }
        }
    }

    if (showSelector) {
       SpaceSelectorSheet(
           rooms = rooms,
           selected = selected,
           onSelect = { viewModel.select(it); showSelector = false },
           onManage = { showSelector = false; navController.navigate(Routes.MANAGE_ROOMS)},
           onDismiss = { showSelector = false }
       )
    }
}