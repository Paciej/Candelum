package mp.android.candelum.lamp

enum class LampMode(val payload: String) {
    None("None"),
    Wave("Wave"),
    Candle("Candle"),
    Pulse("Pulse");

    companion object {
        fun parse(text: String): LampMode? = entries.firstOrNull { it.name == text.trim() }
    }
}