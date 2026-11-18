package mp.android.candelum.home

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
import java.util.UUID

class HomeRepository(
    private val mqtt: MqttConnection,
    scope: CoroutineScope,
) {
    private val _lights = MutableStateFlow<Map<String, Light>>(emptyMap())
    val lights: StateFlow<Map<String, Light>> = _lights

    private val _layout = MutableStateFlow(HomeLayout.EMPTY)
    val layout: StateFlow<HomeLayout> = _layout

    val overview: StateFlow<HomeOverview> =
        combine(_layout, _lights) { layout, lights -> buildOverview(layout, lights)}
            .stateIn(scope, SharingStarted.Eagerly, HomeOverview(emptyList(), emptyList()))

    val connectionStatus: StateFlow<ConnectionStatus> = mqtt.status

    init {
        scope.launch { mqtt.messages.collect { onMessage(it) } }
    }

    fun connect() = mqtt.connect()
    fun disconnect() = mqtt.disconnect()

    fun send(target: LightTarget, command: LightCommand) {
        val ids: List<String> = when (target) {
            is LightTarget.Device -> listOf(target.id)
            is LightTarget.Room -> _layout.value.devices
                .filter { it.roomId == target.roomId }
                .map { it.id }
            LightTarget.All -> _lights.value.keys.toList()
        }
        ids.forEach { id ->
            val message = CandelumProtocol.encode(LightTarget.Device(id), command)
            mqtt.publish(message.topic, message.payload)
        }
    }

    fun createRoom(name: String): String? {
        val id = UUID.randomUUID().toString().take(8)
        return if (editLayout { it.addRoom(Room(id, name.trim())) }) id else null
    }

    fun renameRoom(roomId: String, name: String) =
        editLayout { it.renameRoom(roomId, name.trim()) }

    fun deleteRoom(roomId: String) = editLayout { it.removeRoom(roomId) }

    fun assignLight(lightId: String, roomId: String?) =
        editLayout { it.assignDevice(lightId, roomId) }

    private fun editLayout(change: (HomeLayout) -> HomeLayout): Boolean {
        if (mqtt.status.value != ConnectionStatus.Connected) return false
        var next = HomeLayout.EMPTY
        _layout.update { current -> change(current).also { next = it } }
        mqtt.publish(LAYOUT_TOPIC, LayoutJson.toJson(next), retain = true)
        return true
    }

    private fun onMessage(message: IncomingMessage) {
        if (message.topic == LAYOUT_TOPIC) {
            onLayout(message.payload)
            return
        }
        val event = CandelumProtocol.decode(message.topic, message.payload) ?: return
        _lights.update { current ->
            val light = current[event.deviceId] ?: Light(id = event.deviceId)
            current + (event.deviceId to apply(light, event))
        }
    }

    private fun onLayout(payload: String) {
        val incoming = LayoutJson.parse(payload) ?: return
        _layout.update { current -> if (incoming.version >= current.version) incoming else current }
        Log.d("HomeRepository", "Layout: ${_layout.value}")
        Log.d("HomeRepository", "Overview: ${overview.value}")
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

    companion object{
        const val LAYOUT_TOPIC = "candelum/home/layout"
    }
}