package mp.android.candelum.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mp.android.candelum.mqtt.ConnectionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandelumTopBar(
    title: String,
    connection: ConnectionStatus,
    onTitleClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = { ConnectionDot(connection) },
        title = {
            Row(
                modifier = Modifier.clickable(onClick = onTitleClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title)
                Icon(Icons.Default.KeyboardArrowDown, "Choose space")
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "Settings")
            }
        },
    )
}

@Composable
private fun ConnectionDot(status: ConnectionStatus) {
    val color = when (status) {
        ConnectionStatus.Connected -> Color(0xFF3DDC84)
        ConnectionStatus.Connecting -> Color(0xFFFFB547)
        ConnectionStatus.Disconnected -> Color(0xFFE5484D)
    }
    Box(
        modifier = Modifier
            .padding(start = 16.dp)
            .size(10.dp)
            .background(color, CircleShape)
    )
}