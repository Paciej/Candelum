package mp.android.candelum.light

data class Light(
    val id: String,
    val firmware: String? = null,
    val capabilities: LightCapabilities? = null,
    val online: Boolean? = null,
    val power: Boolean? = null,
    val color: RgbColor? = null,
    val brightness: Int? = null,
    val mode: String? = null,
    val temperature: String? = null,
    val message: Map<String, String> = emptyMap()
)