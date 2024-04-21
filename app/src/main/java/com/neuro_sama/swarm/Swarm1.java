package com.neuro_sama.swarm;

import static android.content.Context.MODE_PRIVATE;
import static com.neuro_sama.swarm.mqtt_client.pubmsg;
import static com.neuro_sama.swarm.mqtt_interface.Control_Timer;
import static com.neuro_sama.swarm.mqtt_interface.Control_Warning;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.hivemq.client.mqtt.datatypes.MqttQos;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Swarm1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Swarm1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    @SuppressLint("StaticFieldLeak")
    static TextView temperature_text, humidity_text,
            light_text, aqi_text, water_text, electric_text;
    static Button refresh_button,warning_button;
    static Toast toast;


    public Swarm1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Swarm1.
     */
    // TODO: Rename and change types and number of parameters
    public static Swarm1 newInstance(String param1, String param2) {
        Swarm1 fragment = new Swarm1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swarm1, container, false);
    }

    //只能在此处理fragment的控件
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        temperature_text = view.findViewById(R.id.temperature_text);
        humidity_text = view.findViewById(R.id.humidity_text);
        light_text = view.findViewById(R.id.light_text);
        aqi_text = view.findViewById(R.id.aqi_text);
        water_text = view.findViewById(R.id.water_text);
        electric_text = view.findViewById(R.id.electric_text);
        refresh_button = view.findViewById(R.id.refresh_time);
        warning_button = view.findViewById(R.id.warning_mode);

        toast = Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        AtomicBoolean warning_mode = new AtomicBoolean(sharedPreferences.getBoolean("warning_mode", false));


        refresh_button.setOnClickListener(v -> {
            long timestamp = System.currentTimeMillis()/1000;
            pubmsg(Control_Timer, "TS "+ timestamp, MqttQos.EXACTLY_ONCE);
        });

        warning_button.setOnClickListener(v -> {
            if(!warning_mode.get())
            {
                pubmsg(Control_Warning, "ON", MqttQos.EXACTLY_ONCE);
                warning_button.setText("关闭主动告警");
                warning_mode.set(true);
            }
            else
            {
                pubmsg(Control_Warning, "OFF", MqttQos.EXACTLY_ONCE);
                warning_button.setText("启动主动告警");
                warning_mode.set(false);
            }
        });

    }


    static Handler handler = new Handler(Looper.myLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String str = msg.obj.toString();
            switch (msg.what)
            {
                case 1:
                    String[] str1 = str.split(" ");
                    temperature_text.setText(ui_unit(str1[0], "°C"));
                    humidity_text.setText(ui_unit(str1[1], "%"));
                    break;
                case 2:
                    String[] str2 = str.split(" ");
                    water_text.setText(ui_unit(str2[0], "L"));
                    electric_text.setText(ui_unit(str2[1], "kWh"));
                    break;
                case 4:
                    if(str.equals("OK")) {
                        toast.setText("刷新成功");
                        toast.show();
                    }
                    else
                        light_text.setText(ui_unit(str, "lux"));
                    break;
                case 5:
                    aqi_text.setText(ui_unit(str, "ppm"));
                    break;
                case 7:
                    toast.setText(str);
                    toast.show();
                    Message message = new Message();
                    message.what = 2;
                    message.obj = str;
                    MainActivity.handler.sendMessage(message);
                    break;
                default:
                    Log.d("handler", "handleMessage: error");
            }
        }
    };

    public static String ui_unit(String str, String unit)
    {
        return str + " " + unit;
    }

}