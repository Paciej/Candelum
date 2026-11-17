package mp.android.candelum.ui.lights

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import mp.android.candelum.light.RgbColor
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

private const val SEND_INTERVAL_MS = 100L

@Composable
fun ColorWheelPicker(
    color: RgbColor?,
    onColorSelected: (RgbColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var lastSent by remember { mutableStateOf(0L) }

    // Follow the color reported by the light, unless the user is dragging.
    LaunchedEffect(color, dragging) {
        if (!dragging && color != null) {
            val hsv = FloatArray(3)
            AndroidColor.RGBToHSV(color.r, color.g, color.b, hsv)
            hue = hsv[0]
            saturation = hsv[1]
        }
    }

    fun currentRgb(): RgbColor {
        val argb = AndroidColor.HSVToColor(floatArrayOf(hue, saturation, 1f))
        return RgbColor(AndroidColor.red(argb), AndroidColor.green(argb), AndroidColor.blue(argb))
    }

    fun updateFrom(position: Offset) {
        val radius = min(size.width, size.height) / 2f
        if (radius <= 0f) return
        val dx = position.x - size.width / 2f
        val dy = position.y - size.height / 2f
        saturation = (hypot(dx, dy) / radius).coerceIn(0f, 1f)
        hue = ((Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()) + 360f) % 360f
    }

    fun sendThrottled() {
        val now = System.currentTimeMillis()
        if (now - lastSent >= SEND_INTERVAL_MS) {
            lastSent = now
            onColorSelected(currentRgb())
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Color", style = MaterialTheme.typography.titleMedium)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .aspectRatio(1f)
                .onSizeChanged { size = it }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        updateFrom(offset)
                        onColorSelected(currentRgb())
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> dragging = true; updateFrom(offset) },
                        onDrag = { change, _ -> updateFrom(change.position); sendThrottled() },
                        onDragEnd = { dragging = false; onColorSelected(currentRgb()) },
                        onDragCancel = { dragging = false },
                    )
                },
        ) {
            val radius = min(this.size.width, this.size.height) / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            val hues = (0..360 step 30).map { Color.hsv(it.toFloat().coerceAtMost(359.9f), 1f, 1f) }
            drawCircle(brush = Brush.sweepGradient(hues, center), radius = radius, center = center)
            drawCircle(
                brush = Brush.radialGradient(listOf(Color.White, Color.Transparent), center, radius),
                radius = radius,
                center = center,
            )

            // Selection marker.
            val angle = Math.toRadians(hue.toDouble())
            val marker = Offset(
                center.x + (kotlin.math.cos(angle) * saturation * radius).toFloat(),
                center.y + (kotlin.math.sin(angle) * saturation * radius).toFloat(),
            )
            drawCircle(Color.White, radius = 14.dp.toPx(), center = marker, style = Stroke(3.dp.toPx()))
            drawCircle(Color.Black, radius = 16.dp.toPx(), center = marker, style = Stroke(1.dp.toPx()))
        }
    }
}