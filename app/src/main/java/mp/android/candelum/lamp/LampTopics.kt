package mp.android.candelum.lamp

object LampTopics {
    const val CMD_POWER = "lamp/cmd/power"
    const val CMD_COLOR = "lamp/cmd/color"
    const val CMD_MODE = "lamp/cmd/mode"

    const val STATUS_ALL = "lamp/status/#"
    const val STATUS_CONNECTION = "lamp/status/connection"
    const val STATUS_COLOR = "lamp/status/color"
    const val STATUS_MODE = "lamp/status/mode"
    const val STATUS_TEMPERATURE = "lamp/status/temperature"
    const val MSG_COLOR = "lamp/status/color/message"
    const val MSG_MODE = "lamp/status/mode/message"
    const val MSG_TEMPERATURE = "lamp/status/temperature/message"
}