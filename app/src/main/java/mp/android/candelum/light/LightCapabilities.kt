package mp.android.candelum.light

data class LightCapabilities(
    val color: Boolean = false,
    val brightness: Boolean = false,
    val modes: List<String> = emptyList(),
    val temperature: Boolean = false
)
