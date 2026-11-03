package mp.android.candelum.lamp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import mp.android.candelum.mqtt.MqttConnection
import mp.android.candelum.mqtt.MqttSettings

class LampViewModel : ViewModel() {

    private val connection = MqttConnection(
        settings = MqttSettings.fromBuildConfig(),
        subscribeFilter = LampTopics.STATUS_ALL,
    )
    private val controller = LampController(connection, viewModelScope)

    val state = controller.state

    fun connect() = controller.connect()
    fun disconnect() = controller.disconnect()
    fun setPower(on: Boolean) = controller.setPower(on)
    fun setColor(color: RgbColor) = controller.setColor(color)
    fun setMode(mode: LampMode) = controller.setMode(mode)

    override fun onCleared() {
        controller.disconnect()
    }
}