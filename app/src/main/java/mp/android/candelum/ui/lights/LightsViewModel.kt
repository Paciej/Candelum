package mp.android.candelum.ui.lights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mp.android.candelum.home.HomeRepository
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget
import mp.android.candelum.mqtt.MqttConnection
import mp.android.candelum.mqtt.MqttSettings
import mp.android.candelum.mqtt.CandelumTopics

class LightsViewModel : ViewModel() {

    private val connection = MqttConnection(
        settings = MqttSettings.fromBuildConfig(),
        subscribeFilters = CandelumTopics.SUBSCRIPTIONS,
    )
    private val home = HomeRepository(connection, viewModelScope)

    val connectionStatus = home.connectionStatus

    val lights = home.lights
        .map { map -> map.values.sortedBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun connect() = home.connect()
    fun disconnect() = home.disconnect()
    fun send(target: LightTarget, command: LightCommand) = home.send(target, command)

    override fun onCleared() {
        home.disconnect()
    }
}