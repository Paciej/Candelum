package mp.android.candelum.mqtt

import mp.android.candelum.light.LightCapabilities
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget
import org.json.JSONException
import org.json.JSONObject

data class OutgoingMessage(val topic: String, val payload: String)

sealed interface DeviceEvent {
    val deviceId: String

    data class Info(
        override val deviceId: String,
        val firmware: String?,
        val capabilities: LightCapabilities,
    ) : DeviceEvent

    data class Status(override val deviceId: String, val field: String, val value: String) : DeviceEvent

    data class StatusMessage(override val deviceId: String, val field: String, val text: String) : DeviceEvent
}

object CandelumProtocol {

    fun encode(target: LightTarget, command: LightCommand): OutgoingMessage {
        val (suffix, payload) = when (command) {
            is LightCommand.Power -> "power" to if (command.on) "ON" else "OFF"
            is LightCommand.Color -> "color" to command.color.toPayload()
            is LightCommand.Brightness -> "brightness" to command.value.toString()
            is LightCommand.Mode -> "mode" to command.name
            LightCommand.Identify -> "identify" to "ON"
        }
        return OutgoingMessage(CandelumTopics.command(target, suffix), payload)
    }

    // Returns null when the message is unknown or malformed, so bad data never crashes the app.
    fun decode(topic: String, payload: String): DeviceEvent? {
        val text = payload.trim()
        return when (val parsed = CandelumTopics.parse(topic)) {
            null -> null
            is ParsedTopic.Info -> parseInfo(parsed.deviceId, text)
            is ParsedTopic.Status ->
                if (parsed.isMessage) DeviceEvent.StatusMessage(parsed.deviceId, parsed.field, text)
                else DeviceEvent.Status(parsed.deviceId, parsed.field, text)
        }
    }

    private fun parseInfo(deviceId: String, payload: String): DeviceEvent.Info? = try {
        val json = JSONObject(payload)
        val caps = json.optJSONObject("capabilities")
        val modesArray = caps?.optJSONArray("modes")
        val modes = if (modesArray == null) emptyList()
        else List(modesArray.length()) { modesArray.getString(it) }

        DeviceEvent.Info(
            deviceId = deviceId,
            firmware = json.optString("fw").ifEmpty { null },
            capabilities = LightCapabilities(
                color = caps?.optBoolean("color", false) ?: false,
                brightness = caps?.optBoolean("brightness", false) ?: false,
                modes = modes,
                temperature = caps?.optBoolean("temperature", false) ?: false,
            ),
        )
    } catch (e: JSONException) {
        null
    }
}