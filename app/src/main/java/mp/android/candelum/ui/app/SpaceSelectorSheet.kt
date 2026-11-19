package mp.android.candelum.ui.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import mp.android.candelum.home.Room
import mp.android.candelum.home.Scope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceSelectorSheet(
    rooms: List<Room>,
    selected: Scope,
    onSelect: (Scope) -> Unit,
    onManage: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            SpaceRow("Home", Icons.Default.Home, selected == Scope.Home) { onSelect(Scope.Home) }
            rooms.forEach { room ->
                SpaceRow(
                    room.name,
                    Icons.Default.MeetingRoom,
                    selected == Scope.OfRoom(room.id),
                ) { onSelect(Scope.OfRoom(room.id)) }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SpaceRow("Manage rooms", Icons.Default.Edit, false, onClick = onManage)
        }
    }
}

@Composable
private fun SpaceRow(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(label) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { if (selected) Icon(Icons.Default.Check, contentDescription = "Selected") }
    )
}