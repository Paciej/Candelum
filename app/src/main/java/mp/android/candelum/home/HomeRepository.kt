package mp.android.candelum.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mp.android.candelum.light.Light
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget
import mp.android.candelum.light.RgbColor
import mp.android.candelum.mqtt.ConnectionStatus
import mp.android.candelum.mqtt.IncomingMessage
import mp.android.candelum.mqtt.MqttConnection
import mp.android.candelum.mqtt.CandelumProtocol
import mp.android.candelum.mqtt.DeviceEvent

class HomeRepository(
    private val mqtt: MqttConnection,
    scope: CoroutineScope,
) {
    private val _lights = MutableStateFlow<Map<String, Light>>(emptyMap())
    val lights: StateFlow<Map<String, Light>> = _lights

    val connectionStatus: StateFlow<ConnectionStatus> = mqtt.status

    init {
        scope.launch { mqtt.messages.collect { onMessage(it) } }
    }

    fun connect() = mqtt.connect()
    fun disconnect() = mqtt.disconnect()

    fun send(target: LightTarget, command: LightCommand) {
        val message = CandelumProtocol.encode(target, command)
        mqtt.publish(message.topic, message.payload)
    }

    private fun onMessage(message: IncomingMessage) {
        val event = CandelumProtocol.decode(message.topic, message.payload) ?: return
        _lights.update { current ->
            // Unknown device id means a newly discovered light.
            val light = current[event.deviceId] ?: Light(id = event.deviceId)
            current + (event.deviceId to apply(light, event))
        }
    }

    private fun apply(light: Light, event: DeviceEvent): Light = when (event) {
        is DeviceEvent.Info -> light.copy(firmware = event.firmware, capabilities = event.capabilities)
        is DeviceEvent.StatusMessage -> light.copy(messages = light.messages + (event.field to event.text))
        is DeviceEvent.Status -> applyStatus(light, event.field, event.value)
    }

    private fun applyStatus(light: Light, field: String, value: String): Light = when (field) {
        "connection" -> light.copy(online = value == "online")
        "power" -> when (value) {
            "ON" -> light.copy(power = true)
            "OFF" -> light.copy(power = false)
            else -> light
        }
        "color" -> RgbColor.parse(value)?.let { light.copy(color = it) } ?: light
        "brightness" -> value.toIntOrNull()?.takeIf { it in 0..255 }?.let { light.copy(brightness = it) } ?: light
        "mode" -> light.copy(mode = value)
        "temperature" -> light.copy(temperature = value)
        else -> light
    }
}