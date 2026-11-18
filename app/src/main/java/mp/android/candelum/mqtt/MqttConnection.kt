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
import java.util.concurrent.atomic.AtomicBoolean

enum class ConnectionStatus { Disconnected, Connecting, Connected }

data class IncomingMessage(val topic: String, val payload: String)

class MqttConnection(private val settings: MqttSettings, private val subscribeFilters: List<String>) {
    private val tag = "MqttConnection"
    private val _status = MutableStateFlow(ConnectionStatus.Disconnected)
    val status: StateFlow<ConnectionStatus> = _status

    private val _messages = MutableSharedFlow<IncomingMessage>(extraBufferCapacity = 64)
    val messages: SharedFlow<IncomingMessage> = _messages

    private val stopReconnect = AtomicBoolean(true)

    private val client: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .identifier("candelum-app" + UUID.randomUUID())
        .serverHost(settings.host)
        .serverPort(settings.port)
        .simpleAuth()
            .username(settings.username)
            .password(settings.password.toByteArray())
            .applySimpleAuth()
        .automaticReconnectWithDefaultConfig()
        .addConnectedListener {
            _status.value = ConnectionStatus.Connected
            subscribeAll()
        }
        .addDisconnectedListener { ctx ->
            Log.w(tag, "Disconnected, source=${ctx.source}, cause=${ctx.cause}")
            if (stopReconnect.get()) {
                ctx.reconnector.reconnect(false)
                _status.value = ConnectionStatus.Disconnected
            } else {
                _status.value = ConnectionStatus.Connecting
            }
        }
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
        stopReconnect.set(false)
        _status.value = ConnectionStatus.Connecting
        client.connectWith()
            .keepAlive(30)
            .send()
            .whenComplete { ack, error ->
                if (error != null) {
                    Log.e(tag, "Connect failed: ${error.message}", error)
                    stopReconnect.set(true)
                }
                else Log.d(tag, "Connected, code: ${ack.returnCode}")
            }
    }

    fun disconnect() {
        stopReconnect.set(true)
        client.disconnect().whenComplete { _, e ->
            if (e != null) Log.w(tag, "Disconnect: ${e.message}")
            _status.value = ConnectionStatus.Disconnected
        }
    }

    fun publish(topic: String, payload: String, retain: Boolean = false) {
        if (_status.value != ConnectionStatus.Connected) {
            Log.w(tag, "Skipped publish on $topic - No connection")
            return
        }
        client.publishWith()
            .topic(topic)
            .payload(payload.toByteArray())
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(retain)
            .send()
            .whenComplete { _, error ->
                if (error != null) Log.e(tag, "Publish $topic unsuccessfully: ${error.message}", error)
                else Log.d(tag, "Sent $topic = $payload")
            }
    }

    private fun subscribeAll() {
        subscribeFilters.forEach { filter ->
            client.subscribeWith()
                .topicFilter(filter)
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete { _, error ->
                    if (error != null) Log.e(tag, "Subscribe $filter failed: ${error.message}", error)
                    else Log.d(tag, "Subscribed to $filter")
                }
        }
    }
}