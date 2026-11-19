package mp.android.candelum.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.android.candelum.home.HomeOverview
import mp.android.candelum.light.Light
import mp.android.candelum.ui.lights.LightTile
import mp.android.candelum.ui.lights.displayName

@Composable
fun HomeOverviewScreen(
    overview: HomeOverview,
    names: Map<String, String>,
    onToggleLight: (Light) -> Unit,
    onAssign: (Light) -> Unit,
    onHomePower: (Boolean) -> Unit,
    onManageRooms: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val all = overview.rooms.flatMap { it.lights } + overview.unassigned
    val anyOn = all.any { it.online == true && it.power == true }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("All lights", style = MaterialTheme.typography.titleMedium)
                Switch(checked = anyOn, onCheckedChange = onHomePower)
            }
        }

        if (overview.rooms.isEmpty() && overview.unassigned.isEmpty()) {
            item { Text("No lights found yet. Power on a lamp to see it here.") }
        }
        if (overview.rooms.isEmpty()) {
            item {
                Button(onClick = onManageRooms) { Text("Create your first room") }
            }
        }

        overview.rooms.forEach { entry ->
            item(key = "room-${entry.room.id}") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.room.name, style = MaterialTheme.typography.titleMedium)
                    if (entry.lights.isEmpty()) {
                        Text("No lights in this room", style = MaterialTheme.typography.bodySmall)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(entry.lights, key = { it.id }) { light ->
                                LightTile(
                                    light = light,
                                    label = displayName(light.id, names),
                                    onClick = { onToggleLight(light) },
                                    onLongClick = { onAssign(light) },
                                )
                            }
                        }
                    }
                }
            }
        }

        if (overview.unassigned.isNotEmpty()) {
            item(key = "unassigned") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Unassigned lights", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(overview.unassigned, key = { it.id }) { light ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LightTile(
                                    light = light,
                                    label = displayName(light.id, names),
                                    onClick = { onToggleLight(light) },
                                    onLongClick = { onAssign(light) },
                                )
                                OutlinedButton(onClick = { onAssign(light) }) { Text("Assign") }
                            }
                        }
                    }
                }
            }
        }
    }
}