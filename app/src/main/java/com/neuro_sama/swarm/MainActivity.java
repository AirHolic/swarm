package com.neuro_sama.swarm;

import static com.neuro_sama.swarm.mqtt_client.pubmsg;
import static com.neuro_sama.swarm.mqtt_interface.Control_Timer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hivemq.client.mqtt.datatypes.MqttQos;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragments_list;
    static ViewPager2 viewPager2;
    static Toast toast;
    @SuppressLint("StaticFieldLeak")
    static Context context;
    @SuppressLint("StaticFieldLeak")
    static NotificationCompat.Builder builder;
    @SuppressLint("StaticFieldLeak")
    static NotificationManagerCompat notificationManager;

    //Swarm1 swarm1 = Swarm1.newInstance("", "");
    //Bundle bundle = new Bundle();

    mqtt_client mqtt_client = new mqtt_client();
    Thread mqtt_thread = new Thread(mqtt_client);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        boolean areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if(!areNotificationsEnabled){

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("通知权限")
                    .setMessage("告警模式需开启通知权限")
                    .setPositiveButton("确认", (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                    });
            builder.show();
        }



        mqtt_thread.start();
        toast = Toast.makeText(MainActivity.this, "MQTT Connected", Toast.LENGTH_LONG);

        viewPager2 = findViewById(R.id.viewPager2);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        fragments_list = new ArrayList<>();
        fragments_list.add(new Swarm1());
        fragments_list.add(new Swarm2());
        fragments_list.add(new Swarm3());




        /* viewpager2 adapter */
        FragmentStateAdapter adapter = new FragmentStateAdapter(MainActivity.this) {
            @Override
            public int getItemCount() {
                return fragments_list.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments_list.get(position);
            }
        };

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    //tab.setCustomView(R.layout.iconfont_view);
                    tab.setText("系统状态");
                    break;
                case 1:
                    tab.setText("设备控制");
                    break;
                case 2:
                    tab.setText("定时任务");
                    break;
            }
            //设置tab样式
        }).attach();//将tablayout与viewpager2绑定


        NotificationChannel channel = new NotificationChannel("warning", "warning", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        channel.enableVibration(true);//震动
        channel.enableLights(true);//闪光

        builder = new NotificationCompat.Builder(this, "warning")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("警告")
                .setContentText("传感器数据异常，请检查！")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        notificationManager = NotificationManagerCompat.from(this);

    }

    //接收消息
    static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                toast.show();
                long timestamp = System.currentTimeMillis()/1000;
                pubmsg(Control_Timer, "TS "+ timestamp, MqttQos.EXACTLY_ONCE);
            }
            else if (msg.what == 2) {
                notificationManager.notify(1, builder.build());
            }

        }
    };
}