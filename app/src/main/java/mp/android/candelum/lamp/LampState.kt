package mp.android.candelum.lamp

import mp.android.candelum.mqtt.ConnectionStatus

data class LampState(
    val broker: ConnectionStatus = ConnectionStatus.Disconnected,
    val lampOnline: Boolean? = null,
    val color: RgbColor? = null,
    val mode: LampMode? = null,
    val temperature: String? = null,
    val colorMessage: String? = null,
    val modeMessage: String? = null,
    val temperatureMessage: String? = null,
)