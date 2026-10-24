package mp.android.candelum.mqtt

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

enum class ConnectionStatus { Disconnected, Connecting, Connected }

data class IncomingMessage(val topic: String, val payload: String)

class MqttConnection(private val settings: MqttSettings, private val subscribeFilter: String) {
    private val tag = "MqttConnection"
    private val _status = MutableStateFlow(ConnectionStatus.Disconnected)
    val status: StateFlow<ConnectionStatus> = _status

    private val _messages = MutableSharedFlow<IncomingMessage>(extraBufferCapacity = 64)
    val messages: SharedFlow<IncomingMessage> = _messages

    private val client: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .identifier("candelum-app" + UUID.randomUUID())
        .serverHost(settings.host)
        .serverPort(settings.port)
        .automaticReconnectWithDefaultConfig()
        .addConnectedListener {
            _status.value = ConnectionStatus.Connected
            subscribeToStatus()
        }
        .addDisconnectedListener { _status.value = ConnectionStatus.Disconnected }
        .buildAsync()

    init {
        client.publishes(MqttGlobalPublishFilter.ALL) { publish ->
            val text = String(publish.payloadAsBytes, Charsets.UTF_8)
            Log.d(tag, "RX ${publish.topic} = '$text' (retain=${publish.isRetain})")
            _messages.tryEmit(IncomingMessage(publish.topic.toString(), text))
        }
    }

    fun connect() {
        if (_status.value != ConnectionStatus.Disconnected) return
        _status.value = ConnectionStatus.Connecting
        client.connectWith()
            .simpleAuth()
            .username(settings.username)
            .password(settings.password.toByteArray())
            .applySimpleAuth()
            .keepAlive(30)
            .send()
            .whenComplete { ack, error ->
                if (error != null) Log.e(tag, "Subscription failed: ${error.message}", error)
                else Log.d(tag, "Subscribed $subscribeFilter, code: ${ack.returnCode }")
            }
    }

    fun disconnect() {
        client.disconnect()
        _status.value = ConnectionStatus.Disconnected
    }

    fun publish(topic: String, payload: String) {
        if (_status.value != ConnectionStatus.Connected) {
            Log.w(tag, "Skipped publish on $topic - No connection")
            return
        }
        client.publishWith()
            .topic(topic)
            .payload(payload.toByteArray())
            .qos(MqttQos.AT_LEAST_ONCE)
            .send()
            .whenComplete { _, error ->
                if (error != null) Log.e(tag, "Publish $topic unsuccessfully: ${error.message}", error)
                else Log.d(tag, "Sent $topic = $payload")
            }
    }

    private fun subscribeToStatus() {
        client.subscribeWith()
            .topicFilter(subscribeFilter)
            .qos(MqttQos.AT_LEAST_ONCE)
            .send()
            .whenComplete { _, error ->
                if (error != null) Log.e(tag, "Subscription unavailable: ${error.message}", error)
                else Log.d(tag, "Subscribed $subscribeFilter")
            }
    }
}