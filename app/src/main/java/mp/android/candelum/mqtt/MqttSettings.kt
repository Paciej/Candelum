package mp.android.candelum.mqtt

import mp.android.candelum.BuildConfig

data class MqttSettings(
    val host: String,
    val port: Int,
    val username: String,
    val password: String
) {
    companion object {
        fun fromBuildConfig() = MqttSettings(
            host = BuildConfig.MQTT_HOST,
            port = BuildConfig.MQTT_PORT,
            username = BuildConfig.MQTT_USER,
            password = BuildConfig.MQTT_PASSWORD
        )
    }
}