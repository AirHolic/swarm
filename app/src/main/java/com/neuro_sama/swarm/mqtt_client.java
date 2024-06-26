package com.neuro_sama.swarm;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
        Log.d("mqtt", "mqtt_init: connected");
    }

    public static void pubmsg(String topic, String msg,MqttQos qos) {
        /*
          Publish "Hello" to the topic "my/test/topic" with qos = 0.
         */
        client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(msg))
                .qos(qos)
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
        Log.d("mqtt", "subscribe: " + topic);
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mqtt_init();
        subscribe("Device/#");//订阅所有设备的消息
        Message msg = new Message();
        msg.what=1;
        MainActivity.handler.sendMessage(msg);

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
            if(topic_index(topic)!=3 && topic_index(topic)!=6)
                Swarm1.handler.sendMessage(message);
            else if(topic_index(topic)==3)
                Swarm2.handler.sendMessage(message);
            else if(topic_index(topic)==6)
                Swarm3.handler.sendMessage(message);
        });

    }

   static Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d("mqtt", "PubMQTTMsg: " + msg.obj);
            String topic = get_topic(msg.what);
            String pub_msg = (String) msg.obj;
            if(msg.what==6 || msg.what==3 || msg.what==7)
                //如果是定时器或者端口控制或者警告信息，qos设置为2
                pubmsg(topic, pub_msg,MqttQos.EXACTLY_ONCE);
            else pubmsg(topic, pub_msg,MqttQos.AT_MOST_ONCE);
                //如果是其他设备，qos设置为0

        }
    };

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
        } else if (topic.toString().equals(Device_Timer)) {
            return 6;
        } else if (topic.toString().equals(Device_Warning)) {
            return 7;
        }
        return 0;
    }
    public void message_init(Message message, int what, String msg) {
        message.obj = msg;
        message.what = what;
    }

    public static String get_topic(int index) {
        switch (index) {
            case 1:
                return Control_AHT10;
            case 2:
                return Control_LoRa;
            case 3:
                return Control_Port;
            case 4:
                return Control_BH1750;
            case 5:
                return Control_MQ135;
            case 6:
                return Control_Timer;
            case 7:
                return Control_Warning;
            default:
                return "";
        }
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
    String Control_Timer = "Control/Others/Timer";
    String Control_Warning = "Control/Others/Warning";

    String Device_AHT10 = "Device/Environment/AHT10";
    String Device_LoRa = "Device/Meter/LoRa";
    String Device_Port = "Device/Others/Port";
    String Device_BH1750 = "Device/Environment/BH1750";
    String Device_MQ135 = "Device/Environment/MQ135";
    String Device_Timer = "Device/Others/Timer";
    String Device_Warning = "Device/Others/Warning";

    /*
     Building the client with ssl.
     */
     Mqtt5BlockingClient client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8884)
            .sslWithDefaultConfig()
            .webSocketConfig()
            .serverPath("mqtt")
            .applyWebSocketConfig()
            .buildBlocking();

}
