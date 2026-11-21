package mp.android.candelum.ui.lights

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mp.android.candelum.light.Light

fun displayName(id: String, names: Map<String, String>): String =
    names[id]?.takeIf { it != id } ?: id.takeLast(4)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LightTile(
    light: Light,
    label: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glow = if (light.online == true && light.power == true) {
        Color(light.color?.r ?: 0x0000, light.color?.g ?: 0x0000, light.color?.b ?: 0x0000)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val status = when {
        light.online == true -> "Offline"
        light.power == true -> "On"
        else -> "Off"
    }
    Card(
        modifier = modifier
            .width(120.dp)
            .alpha(if (light.online == true) 1f else 0.5f)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.size(36.dp).background(glow, CircleShape))
            Text(label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            Text(status, style = MaterialTheme.typography.bodySmall)
        }
    }
}