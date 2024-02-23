package com.neuro_sama.swarm;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.os.Message;
import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.datatypes.MqttTopic;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class mqtt_client implements mqtt_interface, Runnable {

    public void mqtt_init() {
        /*
          Connect securely with username, password.
         */
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .keepAlive(60)
                .send();
    }

    public void pubmsg(String topic, String msg) {
        /*
          Publish "Hello" to the topic "my/test/topic" with qos = 0.
         */
        client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(msg))
                .qos(MqttQos.AT_MOST_ONCE)
                .send();
    }

    public void subscribe(String topic) {
        /*
          Subscribe to the topic "my/test/topic" with qos = 0 and print the received message.
         */
        client.subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_MOST_ONCE)
                .send();
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mqtt_init();
        Message message = new Message();
        /*
          Set a callback that is called when a message is received (using the async API style).
          Then disconnect the client after a message was received.
         */
        client.toAsync().publishes(ALL, publish -> {
            //System.out.println("Received message: " + publish.getTopic() + " -> " + UTF_8.decode(publish.getPayload().orElse(null)));
            MqttTopic topic = publish.getTopic();
            String recv_msg = String.valueOf(UTF_8.decode(publish.getPayload().orElse(null)));
            //在ui线程中更新ui
            Log.d("mqtt", "Received message: " + topic + " -> " + recv_msg);
            message.obj = recv_msg;
            message.what = 1;
            Swarm1.handler.sendMessage(message);
        });


    }
}

interface mqtt_interface {
    final String host = "bb9c9a0834f741db86abbba451ec955f.s1.eu.hivemq.cloud";
    final String username = "esp8266";
    final String password = "Esp8266test";
    /*
     Building the client with ssl.
     */
    final Mqtt5BlockingClient client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8884)
            .sslWithDefaultConfig()
            .webSocketConfig()
            .serverPath("mqtt")
            .applyWebSocketConfig()
            .buildBlocking();

}
