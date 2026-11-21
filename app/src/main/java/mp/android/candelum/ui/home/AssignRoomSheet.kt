package mp.android.candelum.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mp.android.candelum.home.Room

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignRoomSheet(
    lightLabel: String,
    rooms: List<Room>,
    currentRoomId: String?,
    onAssign: (String?) -> Unit,
    onManageRooms: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            "Assign $lightLabel to room",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        if (rooms.isEmpty()) {
            Text("You have no rooms yet.", modifier = Modifier.padding(horizontal = 16.dp))
            Button(onClick = onManageRooms, modifier = Modifier.padding(16.dp)) {
                Text("Create a room")
            }
        }
        rooms.forEach { room ->
            ListItem(
                modifier = Modifier.clickable { onAssign(room.id) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text(room.name) },
                trailingContent = {
                    if (room.id == currentRoomId) Icon(Icons.Default.Check, contentDescription = "Current")
                },
            )
        }
        if (currentRoomId != null) {
            ListItem(
                modifier = Modifier.clickable { onAssign(null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text("Unassigned") },
            )
        }
    }
}