package mp.android.candelum.home

import mp.android.candelum.light.Light

data class RoomWithLights(val room: Room, val lights: List<Light>)

data class HomeOverview(
    val rooms: List<RoomWithLights>,
    val unassigned: List<Light>,
)

fun buildOverview(layout: HomeLayout, lights: Map<String, Light>): HomeOverview {
    val roomOf = layout.devices.associate { it.id to it.roomId }
    val knownRoomIds = layout.rooms.map { it.id }.toSet()

    val rooms = layout.rooms.map { room ->
        RoomWithLights(
            room,
            lights.values.filter { roomOf[it.id] == room.id }.sortedBy { it.id },
        )
    }
    // No entry, null room, or a room that no longer exists -> unassigned.
    val unassigned = lights.values
        .filter { roomOf[it.id] !in knownRoomIds }
        .sortedBy { it.id }

    return HomeOverview(rooms, unassigned)
}