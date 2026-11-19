package mp.android.candelum.ui.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.android.candelum.home.Room

@Composable
fun ManageRoomsScreen(
    rooms: List<Room>,
    onBack: () -> Unit,
    onCreate: (String) -> Boolean,
    onRename: (String, String) -> Boolean,
    onDelete: (String) -> Boolean,
) {
    var creating by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf<Room?>(null) }
    var deleting by remember { mutableStateOf<Room?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    fun run(ok: Boolean) { error = if (ok) null else "Not connected to the broker - change not saved." }

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Manage rooms", style = MaterialTheme.typography.titleLarge)
        }
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }
        if (rooms.isEmpty()) {
            Text("No rooms yet. Create your first room.", modifier = Modifier.padding(16.dp))
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(rooms, key = { it.id }) { room ->
                ListItem(
                    headlineContent = { Text(room.name) },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { renaming = room }) {
                                Icon(Icons.Default.Edit, contentDescription = "Rename")
                            }
                            IconButton(onClick = { deleting = room }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    },
                )
            }
        }
        Button(
            onClick = { creating = true },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) { Text("Add room") }
    }

    if (creating) {
        RoomNameDialog("New room", "", { name -> run(onCreate(name)); creating = false }) { creating = false }
    }
    renaming?.let { room ->
        RoomNameDialog("Rename room", room.name, { name -> run(onRename(room.id, name)); renaming = null }) { renaming = null }
    }
    deleting?.let { room ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete ${room.name}?") },
            text = { Text("Lights in this room become unassigned.") },
            confirmButton = { TextButton(onClick = { run(onDelete(room.id)); deleting = null }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun RoomNameDialog(
    title: String,
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true) },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = { onConfirm(name) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}