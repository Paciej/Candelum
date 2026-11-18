package mp.android.candelum.home

import org.json.JSONObject
import org.json.JSONArray

object LayoutJson {
    private fun JSONArray?.objects(): List<JSONObject> =
        if (this == null) emptyList() else (0 until length()).map { getJSONObject(it) }

    fun parse(text: String): HomeLayout? = try {
        val root = JSONObject(text)
        val rooms = root.optJSONArray("rooms").objects().map {
            Room(id = it.getString("id"), name = it.optString("name", it.getString("id")))
        }
        val devices = root.optJSONArray("devices").objects().map {
            LayoutDevice(
                id = it.getString("id"),
                name = it.optString("name", it.getString("id")),
                roomId = if (it.isNull("room")) null else it.optString("room").ifBlank { null }
            )
        }
        HomeLayout(root.optInt("version", 0), rooms, devices)
    } catch (e: Exception) {
        null
    }

    fun toJson(layout: HomeLayout): String = JSONObject().apply {
        put("version", layout.version)
        put("rooms", JSONArray().apply {
            layout.rooms.forEach { put(JSONObject().put("id", it.id).put("name", it.name)) }
        })
        put("devices", JSONArray().apply {
            layout.devices.forEach {
                put(
                    JSONObject()
                        .put("id", it.id)
                        .put("name", it.name)
                        .put("room", it.roomId ?: JSONObject.NULL)
                )
            }
        })
    }.toString()
}