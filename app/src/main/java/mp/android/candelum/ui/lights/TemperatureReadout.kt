package mp.android.candelum.ui.lights

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TemperatureReadout(temperature: String?, modifier: Modifier = Modifier) {
    Text(
        text = "Temperature: ${temperature?.let { "$it °C" } ?: "-"}",
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}