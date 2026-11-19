package mp.android.candelum.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(val route: String, val label: String, val icon: ImageVector) {
    Control("control", "Control", Icons.Default.Tune),
    Modes("modes", "Modes", Icons.Default.AutoAwesome),
    Automations("automations", "Automations", Icons.Default.Schedule)
}

object Routes {
    const val SETTINGS = "settings"
    const val MANAGE_ROOMS = "manage_rooms"
}