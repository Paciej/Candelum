package mp.android.candelum.home

import mp.android.candelum.light.LightTarget

sealed interface Scope {
    data object Home : Scope
    data class OfRoom(val roomId: String) : Scope
}

fun Scope.toTarget(): LightTarget = when (this) {
    Scope.Home -> LightTarget.All
    is Scope.OfRoom -> LightTarget.Room(roomId)
}