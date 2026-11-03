package mp.android.candelum.light

sealed interface LightCommand {
    data class Power(val on: Boolean) : LightCommand
    data class Color(val color: RgbColor) : LightCommand
    data class Brightness(val value: Int) : LightCommand {
        init { require(value in 0..255) { "Brightness must be 0..255" } }
    }
    data class Mode(val name: String) : LightCommand
    data object Identify : LightCommand
}