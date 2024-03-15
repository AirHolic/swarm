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
        subscribe("Device/#");//订阅所有设备的消息



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
            Message message = new Message();
            message_init(message, topic_index(topic), recv_msg);
            Swarm1.handler.sendMessage(message);

        });
    }

//    Handler handler = new Handler(Looper.myLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                Swarm1.textView.setText((String) msg.obj);
//            }
//        }
//    };

    public int topic_index(MqttTopic topic) {
        if (topic.toString().equals(Device_AHT10)) {
            return 1;
        } else if (topic.toString().equals(Device_LoRa)) {
            return 2;
        } else if (topic.toString().equals(Device_Port)) {
            return 3;
        } else if (topic.toString().equals(Device_BH1750)) {
            return 4;
        } else if (topic.toString().equals(Device_MQ135)) {
            return 5;
        }
        return 0;
    }
    public void message_init(Message message, int what, String msg) {
        message.obj = msg;
        message.what = what;
    }
}



interface mqtt_interface {

    String host = "bb9c9a0834f741db86abbba451ec955f.s1.eu.hivemq.cloud";
    String username = "esp8266";
    String password = "Esp8266test";

    String Control_AHT10 = "Control/Environment/AHT10";
    String Control_LoRa = "Control/Meter/LoRa";
    String Control_Port = "Control/Others/Port";
    String Control_BH1750 = "Control/Environment/BH1750";
    String Control_MQ135 = "Control/Environment/MQ135";

    String Device_AHT10 = "Device/Environment/AHT10";
    String Device_LoRa = "Device/Meter/LoRa";
    String Device_Port = "Device/Others/Port";
    String Device_BH1750 = "Device/Environment/BH1750";
    String Device_MQ135 = "Device/Environment/MQ135";

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
