package mp.android.candelum.light

sealed interface LightTarget {
    data class Device(val id: String) : LightTarget
    data class Room(val roomId: String) : LightTarget
    data object All : LightTarget
}