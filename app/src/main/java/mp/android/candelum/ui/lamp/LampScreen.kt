package mp.android.candelum.ui.lamp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.android.candelum.lamp.LampMode
import mp.android.candelum.lamp.LampState
import mp.android.candelum.lamp.LampViewModel
import mp.android.candelum.lamp.RgbColor

private val presets = listOf(
    "Red" to RgbColor(255, 0, 0),
    "Green" to RgbColor(0, 255, 0),
    "Blue" to RgbColor(0, 0, 255),
    "Warm" to RgbColor(255, 140, 40),
    "White" to RgbColor(255, 255, 255),
)

@Composable
fun LampScreen(
    state: LampState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onPower: (Boolean) -> Unit,
    onColor: (RgbColor) -> Unit,
    onMode: (LampMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Candelum - test lamp", style = MaterialTheme.typography.headlineSmall)

        Text("Broker: ${state.broker}")
        Text("Lamp: ${state.lampOnline?.let { if (it) "Online" else "Offline" } ?: "No data"}")
        Text("Color: ${state.color?.toPayload() ?: "-"}   Mode: ${state.mode ?: "-"}")
        Text("Temperature: ${state.temperature ?: "-"}")
        state.colorMessage?.let { Text("Info color: $it") }
        state.modeMessage?.let { Text("Info mode: $it") }
        state.temperatureMessage?.let { Text("Info temp.: $it") }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onConnect) { Text("Connect") }
            OutlinedButton(onClick = onDisconnect) { Text("Disconnect") }
        }

        Text("Power", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onPower(true) }) { Text("ON") }
            OutlinedButton(onClick = { onPower(false) }) { Text("OFF") }
        }

        Text("Color", style = MaterialTheme.typography.titleMedium)
        presets.forEach { (name, color) ->
            OutlinedButton(onClick = { onColor(color) }, modifier = Modifier.fillMaxWidth()) {
                Text("$name (${color.toPayload()})")
            }
        }

        Text("Mode", style = MaterialTheme.typography.titleMedium)
        LampMode.entries.forEach { mode ->
            OutlinedButton(onClick = { onMode(mode) }, modifier = Modifier.fillMaxWidth()) {
                Text(mode.name)
            }
        }
    }
}

@Preview
@Composable
fun lampPreview() {
    val vm: LampViewModel = viewModel()
    val state by vm.state.collectAsState()
        LampScreen(
            state = state,
            onConnect = vm::connect,
            onDisconnect = vm::disconnect,
            onPower = vm::setPower,
            onColor = vm::setColor,
            onMode = vm::setMode,
            modifier = Modifier
        )
}
