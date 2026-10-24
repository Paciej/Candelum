package mp.android.candelum.mqtt

class LampTopics(private val base: String = "lamp") {

    val cmdPower = "$base/cmd/power"
    val cmdColor = "$base/cmd/color"
    val cmdMode = "$base/cmd/mode"

    val statusConnection = "$base/status/connection"
    val statusColor = "$base/status/color"
    val statusMode = "$base/status/mode"
    val statusTemperature = "$base/status/temperature"

    val messageColor = "$base/status/color/message"
    val messageMode = "$base/status/mode/message"
    val messageTemperature = "$base/status/temperature/message"

    val subscribeAllStatus = "$base/status/#"
}