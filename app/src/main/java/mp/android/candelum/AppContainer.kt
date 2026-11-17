package mp.android.candelum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mp.android.candelum.home.HomeRepository
import mp.android.candelum.mqtt.CandelumTopics
import mp.android.candelum.mqtt.MqttConnection
import mp.android.candelum.mqtt.MqttSettings

class AppContainer {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val connection = MqttConnection(
        settings = MqttSettings.fromBuildConfig(),
        subscribeFilters = CandelumTopics.SUBSCRIPTIONS
    )

    val home = HomeRepository(connection, appScope)
}