package mp.android.candelum.ui.lights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun BrightnessSlider(
    brightness: Int?,
    onBrightnessChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Local value while dragging; reset whenever the light reports a new brightness.
    var slider by remember(brightness) { mutableFloatStateOf((brightness ?: 0).toFloat()) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Brightness: ${slider.toInt()} / 255",
            style = MaterialTheme.typography.titleMedium,
        )
        Slider(
            value = slider,
            onValueChange = { slider = it },
            onValueChangeFinished = { onBrightnessChange(slider.toInt().coerceIn(0, 255)) },
            valueRange = 0f..255f,
        )
    }
}