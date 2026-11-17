package mp.android.candelum.mqtt

import mp.android.candelum.light.LightTarget

object CandelumTopics {
    private const val ROOT = "candelum"
    private const val GROUP_ALL = "all"

    const val FILTER_INFO = "$ROOT/+/info"
    const val FILTER_STATUS = "$ROOT/+/status/#"

    val SUBSCRIPTIONS = listOf(FILTER_INFO, FILTER_STATUS)

    private val deviceIdRegex = Regex("^[0-9a-f]{12}$")

    fun command(target: LightTarget, suffix: String): String = when (target) {
        is LightTarget.Device -> "$ROOT/${target.id}/cmd/$suffix"
        is LightTarget.Room -> "$ROOT/group/${target.roomId}/cmd/$suffix"
        LightTarget.All -> "$ROOT/group/$GROUP_ALL/cmd/$suffix"
    }

    fun parse(topic: String): ParsedTopic? {
        val parts = topic.split("/")
        if (parts.size < 3 || parts[0] != ROOT) return null
        val deviceId = parts[1]
        if (!deviceIdRegex.matches(deviceId)) return null

        return when {
            parts.size == 3 && parts[2] == "info" -> ParsedTopic.Info(deviceId)
            parts.size == 4 && parts[2] == "status" ->
                ParsedTopic.Status(deviceId, field = parts[3], isMessage = false)
            parts.size == 5 && parts[2] == "status" && parts[4] == "message" ->
                ParsedTopic.Status(deviceId, field = parts[3], isMessage = true)
            else -> null
        }
    }
}
sealed interface ParsedTopic {
    data class Info(val deviceId: String) : ParsedTopic
    data class Status(val deviceId: String, val field: String, val isMessage: Boolean) : ParsedTopic
}