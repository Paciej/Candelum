package mp.android.candelum.ui.lights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PowerToggle(
    power: Boolean?,
    onPowerChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Power: " + when (power) { true -> "on"; false -> "off"; null -> "unknown" },
            style = MaterialTheme.typography.titleMedium,
        )
        Switch(checked = power == true, onCheckedChange = onPowerChange)
    }
}