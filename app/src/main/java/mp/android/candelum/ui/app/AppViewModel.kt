package mp.android.candelum.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mp.android.candelum.CandelumApp
import mp.android.candelum.home.HomeRepository
import mp.android.candelum.home.Room
import mp.android.candelum.home.Scope
import mp.android.candelum.home.toTarget
import mp.android.candelum.light.Light
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget

class AppViewModel(private val home: HomeRepository) : ViewModel() {
    private val requested = MutableStateFlow<Scope>(Scope.Home)

    val connection = home.connectionStatus

    val rooms: StateFlow<List<Room>> = home.layout
        .map { it.rooms }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selected: StateFlow<Scope> = combine(requested, home.layout) { scope, layout ->
        if (scope is Scope.OfRoom && layout.rooms.none { it.id == scope.roomId }) Scope.Home else scope
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Scope.Home)

    val title: StateFlow<String> = combine(requested, home.layout) { scope, layout ->
        when (scope) {
            Scope.Home -> "Home"
            is Scope.OfRoom -> layout.rooms.firstOrNull {it.id == scope.roomId }?.name ?: "Home"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Home")

    val visibleLights: StateFlow<List<Light>> = combine(selected, home.overview) { scope, overview ->
        when (scope) {
            Scope.Home -> (overview.rooms.flatMap { it.lights } + overview.unassigned).sortedBy { it.id }
            is Scope.OfRoom -> overview.rooms.firstOrNull { it.room.id == scope.roomId }?.lights.orEmpty()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun select(scope: Scope) { requested.value = scope }

    fun connect() = home.connect()
    fun disconnect() = home.disconnect()
    fun send(target: LightTarget, command: LightCommand) = home.send(target, command)
    fun sendToScope(command: LightCommand) = home.send(selected.value.toTarget(), command)

    fun createRoom(name: String): Boolean = home.createRoom(name) != null
    fun renameRoom(id: String, name: String): Boolean = home.renameRoom(id, name)
    fun deleteRoom(id: String): Boolean = home.deleteRoom(id)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CandelumApp
                AppViewModel(app.container.home)
            }
        }
    }
}