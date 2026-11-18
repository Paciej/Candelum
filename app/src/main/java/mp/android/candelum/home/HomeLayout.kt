package mp.android.candelum.home

import android.R

data class Room(val id: String, val name: String)
data class LayoutDevice(val id: String, val name: String, val roomId: String?)

data class HomeLayout(
    val version: Int,
    val rooms: List<Room>,
    val devices: List<LayoutDevice>
) {
    private fun bumped(rooms: List<Room> = this.rooms, devices: List<LayoutDevice> = this.devices) =
        HomeLayout(version + 1, rooms, devices)

    fun addRoom(room: Room) = bumped(rooms = rooms + room)

    fun renameRoom(id: String, name: String) =
        bumped(rooms = rooms.map { if (it.id == id) it.copy(name = name) else it })

    fun removeRoom(id: String) = bumped(
        rooms = rooms.filterNot { it.id == id },
        devices = devices.map { if (it.roomId == id) it.copy(roomId = null) else it }
    )

    fun assignDevice(deviceId: String, roomId: String?, defaultName: String = deviceId): HomeLayout {
        val exists = devices.any { it.id == deviceId }
        val newDevices = if (exists) {
            devices.map { if (it.id == deviceId) it.copy(roomId = roomId) else it }
        } else {
            devices + LayoutDevice(deviceId, defaultName, roomId)
        }
        return bumped(devices = newDevices)
    }

    fun renameDevice(deviceId: String, name: String) =
        bumped(devices = devices.map { if (it.id == deviceId) it.copy(name = name) else it })

    companion object {
        val EMPTY = HomeLayout(version = 0, rooms = emptyList(), devices = emptyList())
    }
}
