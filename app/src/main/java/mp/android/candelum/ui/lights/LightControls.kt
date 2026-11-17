package mp.android.candelum.ui.lights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.android.candelum.light.Light
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget

@Composable
fun LightControls(
    light: Light,
    onCommand: (LightTarget, LightCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    val target = LightTarget.Device(light.id)
    val send = { command: LightCommand -> onCommand(target, command) }
    val caps = light.capabilities

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PowerToggle(power = light.power, onPowerChange = { send(LightCommand.Power(it)) })

        if (caps == null) {
            Text("Waiting for device info...", style = MaterialTheme.typography.bodyMedium)
        } else {
            if (caps.color) {
                ColorWheelPicker(color = light.color, onColorSelected = { send(LightCommand.Color(it)) })
            }
            if (caps.brightness) {
                BrightnessSlider(
                    brightness = light.brightness,
                    onBrightnessChange = { send(LightCommand.Brightness(it)) },
                )
            }
            if (caps.modes.isNotEmpty()) {
                ModeSelector(
                    modes = caps.modes,
                    currentMode = light.mode,
                    onModeSelected = { send(LightCommand.Mode(it)) },
                )
            }
            if (caps.temperature) {
                TemperatureReadout(temperature = light.temperature)
            }
        }

        OutlinedButton(onClick = { send(LightCommand.Identify) }) { Text("Identify") }
    }
}