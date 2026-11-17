package mp.android.candelum.ui.lights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.android.candelum.light.Light
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget
import mp.android.candelum.mqtt.ConnectionStatus
import mp.android.candelum.ui.lights.LightControls

@Composable
fun LightsScreen(
    connection: ConnectionStatus,
    lights: List<Light>,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onCommand: (LightTarget, LightCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Candelum", style = MaterialTheme.typography.headlineSmall)
        Text("Broker: $connection")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onConnect) { Text("Connect") }
            OutlinedButton(onClick = onDisconnect) { Text("Disconnect") }
        }
        Text("Discovered lights: ${lights.size}", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lights, key = { it.id }) { light ->
                LightCard(light, onCommand)
            }
        }
    }
}

@Composable
private fun LightCard(light: Light, onCommand: (LightTarget, LightCommand) -> Unit) {
    val target = LightTarget.Device(light.id)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(light.id, style = MaterialTheme.typography.titleSmall)
            Text("Firmware: ${light.firmware ?: "-"}")
            LightControls(light = light, onCommand = onCommand)
        }
    }
}