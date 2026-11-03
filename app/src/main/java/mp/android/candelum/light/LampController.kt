package mp.android.candelum.lamp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mp.android.candelum.mqtt.IncomingMessage
import mp.android.candelum.mqtt.MqttConnection

class LampController(
    private val mqtt: MqttConnection,
    scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(LampState())
    val state: StateFlow<LampState> = _state

    init {
        scope.launch { mqtt.status.collect { s -> _state.update { it.copy(broker = s) } } }
        scope.launch { mqtt.messages.collect { handle(it) } }
    }

    fun connect() = mqtt.connect()
    fun disconnect() = mqtt.disconnect()

    fun setPower(on: Boolean) = mqtt.publish(LampTopics.CMD_POWER, if (on) "ON" else "OFF")
    fun setColor(color: RgbColor) = mqtt.publish(LampTopics.CMD_COLOR, color.toPayload())
    fun setMode(mode: LampMode) = mqtt.publish(LampTopics.CMD_MODE, mode.name)

    private fun handle(msg: IncomingMessage) {
        val text = msg.payload.trim()
        _state.update { current ->
            when (msg.topic) {
                LampTopics.STATUS_CONNECTION -> current.copy(lampOnline = text == "Online")
                LampTopics.STATUS_COLOR -> current.copy(color = RgbColor.parse(text) ?: current.color)
                LampTopics.STATUS_MODE -> current.copy(mode = LampMode.parse(text) ?: current.mode)
                LampTopics.STATUS_TEMPERATURE -> current.copy(temperature = text)
                LampTopics.MSG_COLOR -> current.copy(colorMessage = text)
                LampTopics.MSG_MODE -> current.copy(modeMessage = text)
                LampTopics.MSG_TEMPERATURE -> current.copy(temperatureMessage = text)
                else -> current
            }
        }
    }
}