package mp.android.candelum.ui.lights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mp.android.candelum.CandelumApp
import mp.android.candelum.home.HomeRepository
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget

class LightsViewModel(private val home: HomeRepository) : ViewModel() {

    val connectionStatus = home.connectionStatus

    val lights = home.lights
        .map { map -> map.values.sortedBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun connect() = home.connect()
    fun disconnect() = home.disconnect()
    fun send(target: LightTarget, command: LightCommand) = home.send(target, command)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CandelumApp
                LightsViewModel(app.container.home)
            }
        }
    }
}