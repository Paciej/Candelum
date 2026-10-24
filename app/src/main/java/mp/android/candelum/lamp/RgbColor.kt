package mp.android.candelum.lamp

data class RgbColor(val r: Int, val g: Int, val b: Int) {
    init {
        require(r in 0..255 && g in 0..255 && b in 0..255) {
            "RGB is outside range 0..255: $r,$g,$b"
        }
    }

    fun toPayload(): String = "$r,$g,$b"

    companion object {
        fun parse(text: String): RgbColor? {
            val parts = text.trim().split(",")
            if (parts.size != 3) return null
            val values = parts.map { it.trim().toIntOrNull() ?: return null }
            if (values.any { it !in 0..255 }) return null
            return RgbColor(values[0], values[1], values[2])
        }
    }
}