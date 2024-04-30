package com.neuro_sama.swarm;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Swarm3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Swarm3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @SuppressLint("StaticFieldLeak")
    static TextView task_time_text, task_name_text, task_port_text;
    @SuppressLint("StaticFieldLeak")
    static View item_view;
    static LayoutInflater item_layout;
    @SuppressLint("StaticFieldLeak")
    static Button task_del;
    @SuppressLint("StaticFieldLeak")
    static Context context;

    @SuppressLint("StaticFieldLeak")
    static LinearLayout swarm3_scroll_layout;
    static SharedPreferences task_list_sp;

    static List<String> task_list = new ArrayList<>();
    static List<String> time_list = new ArrayList<>();
    static List<String> port_list = new ArrayList<>();
    static List<String> finalTask_list = new ArrayList<>();
    static List<String> finalTime_list = new ArrayList<>();
    static List<String> finalPort_list = new ArrayList<>();

    static String task_name, task_time, task_port,task_time_hour,task_time_minute,task_time_second;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Swarm3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Swarm3.
     */
    // TODO: Rename and change types and number of parameters
    public static Swarm3 newInstance(String param1, String param2) {
        Swarm3 fragment = new Swarm3();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swarm3, container, false);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swarm3_scroll_layout = view.findViewById(R.id.swarm3_scroll_layout);
        Button swarm3_add_task = view.findViewById(R.id.swarm3_add_task);
        context = getContext();
        //swarm3_scroll_layout.removeAllViews();


        task_list_sp = requireContext().getSharedPreferences("task_list", Context.MODE_PRIVATE);
        task_list.addAll(new ArrayList<>(task_list_sp.getStringSet("id_list", new HashSet<>())));
        time_list.addAll(new ArrayList<>(task_list_sp.getStringSet("time_list", new HashSet<>())));
        port_list.addAll(new ArrayList<>(task_list_sp.getStringSet("port_list", new HashSet<>())));
        // Add a new task



        refreshview();

        finalPort_list.addAll(port_list);
        finalTask_list.addAll(task_list);
        finalTime_list.addAll(time_list);




        swarm3_add_task.setOnClickListener(v -> {
            Random random = new Random();
            int task_id = random.nextInt(65535);
            StringBuilder hex_task_id = new StringBuilder(Integer.toHexString(task_id));
            if (hex_task_id.length() < 4) {
                while (hex_task_id.length() < 4)
                    hex_task_id.insert(0, "0");
            }


            LayoutInflater using_dialog_layout_xml = LayoutInflater.from(getContext());
            View dialog_view = using_dialog_layout_xml.inflate(R.layout.add_task, null);

            EditText add_task_name = dialog_view.findViewById(R.id.add_task_name);
            EditText add_task_time_hour = dialog_view.findViewById(R.id.add_task_time_hour);
            EditText add_task_time_minute = dialog_view.findViewById(R.id.add_task_time_minute);
            EditText add_task_time_second = dialog_view.findViewById(R.id.add_task_time_second);
            CheckedTextView add_task_port1 = dialog_view.findViewById(R.id.add_task_port1);
            CheckedTextView add_task_port2 = dialog_view.findViewById(R.id.add_task_port2);
            CheckedTextView add_task_port3 = dialog_view.findViewById(R.id.add_task_port3);
            CheckedTextView add_task_port4 = dialog_view.findViewById(R.id.add_task_port4);
            CheckedTextView add_task_port5 = dialog_view.findViewById(R.id.add_task_port5);
            CheckedTextView add_task_port6 = dialog_view.findViewById(R.id.add_task_port6);
            CheckedTextView add_task_port7 = dialog_view.findViewById(R.id.add_task_port7);
            CheckedTextView add_task_port8 = dialog_view.findViewById(R.id.add_task_port8);
            CheckedTextView[] add_task_ports = {add_task_port1, add_task_port2, add_task_port3,
                    add_task_port4, add_task_port5, add_task_port6, add_task_port7, add_task_port8};

            add_task_name.setText(hex_task_id.toString().toUpperCase());
            boolean[] checked = {false, false, false, false, false, false, false, false};
            @SuppressLint("SetTextI18n") AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setView(dialog_view)
                    .setPositiveButton("添加任务", (dialog, which) -> {
                        // Add the task
                        task_name = add_task_name.getText().toString();
                        task_time_hour = add_task_time_hour.getText().toString();
                        task_time_minute = add_task_time_minute.getText().toString();
                        task_time_second = add_task_time_second.getText().toString();

                        if(task_time_hour.equals("") || task_time_minute.equals("") ||
                                task_time_second.equals("")){
                            Toast.makeText(getContext(), "请输入完整的时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(Integer.parseInt(task_time_hour) > 23 || Integer.parseInt(task_time_minute) > 59
                                || Integer.parseInt(task_time_second) > 59 || Integer.parseInt(task_time_hour) < 0
                                || Integer.parseInt(task_time_minute) < 0 || Integer.parseInt(task_time_second) < 0){
                            Toast.makeText(getContext(), "请输入正确的时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(task_name.length() < 4){
                            Toast.makeText(getContext(), "任务名长度不足", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String hexChars = "0123456789ABCDEF"; // 16进制字符集
                        for(int i = 0; i < task_name.length(); i++){
                            if(hexChars.indexOf(task_name.charAt(i)) == -1){
                                Toast.makeText(getContext(), "任务名只能包含大写16进制字符", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if(Integer.parseInt(task_time_hour) < 10 && task_time_hour.length() < 2)
                            task_time_hour = "0" + task_time_hour;
                        if(Integer.parseInt(task_time_minute) < 10 && task_time_minute.length() < 2)
                            task_time_minute = "0" + task_time_minute;
                        if(Integer.parseInt(task_time_second) < 10 && task_time_second.length() < 2)
                            task_time_second = "0" + task_time_second;

                        item_layout =  LayoutInflater.from(getContext());
                        item_view = item_layout.inflate(R.layout.task_item, null);
                        task_time_text = item_view.findViewById(R.id.task_time_text);
                        task_name_text = item_view.findViewById(R.id.task_name_text);
                        task_port_text = item_view.findViewById(R.id.task_port_text);
                        task_del = item_view.findViewById(R.id.task_del);

                        Log.d("Task", "Task Name: " + task_name + " Time: " +
                                task_time_hour + ":" + task_time_minute + ":" + task_time_second);

                        Message msg = new Message();
                        msg.what = 6;//Device_Timer
                        msg.obj = 0;//msg
                        msg.arg1 = 0;//port

                        task_port = "Null";
                        StringBuilder task_port_str = new StringBuilder();

                        for (int i = 0; i < add_task_ports.length; i++) {
                            checked[i] = add_task_ports[i].isChecked();
                            if (checked[i]) {
                                if(task_port.equals("Null")) {
                                    task_port = i + "";
                                    task_port_str.append(i);
                                }
                                else
                                {
                                    task_port_str.append(",").append(i);
                                }
                                //Log.d("Task", "Port " + i + " is checked "+task_port);
                                msg.arg1 |= (1 << i);
                            }
                        }
                        task_port = task_port_str.toString();
                        task_port_text.setText(task_port);
                        msg.arg1 = 255 - msg.arg1;
                        msg.obj = "AT " + task_name + " " + task_time_hour + task_time_minute + task_time_second + " " + msg.arg1;
                        mqtt_client.handler.sendMessage(msg);
                        // Save the task
                        task_name_text.setText(task_name);
                        task_time = task_time_hour + ":" + task_time_minute + ":" + task_time_second;
                        task_time_text.setText(task_time);
//                        String finalTask_time_hour = task_time_hour;
//                        String finalTask_time_minute = task_time_minute;
//                        String finalTask_time_second = task_time_second;
                        task_del.setOnClickListener(v1 -> {
                            Message message = new Message();
                            message.what = 6;//删除任务
                            message.obj = "DT" + " " + task_name;
                            mqtt_client.handler.sendMessage(message);

//                            task_list.remove(task_name);
//                            time_list.remove(task_time_hour + ":" + task_time_minute + ":" + task_time_second);
//                            port_list.remove(task_port);
//                            SharedPreferences.Editor task_list_editor = task_list_sp.edit();
//                            task_list_editor.putStringSet("id_list", new HashSet<>(task_list));
//                            task_list_editor.putStringSet("time_list", new HashSet<>(time_list));
//                            task_list_editor.putStringSet("port_list", new HashSet<>(port_list));
//                            task_list_editor.apply();
//                            SharedPreferences.Editor editor1 = task_list_sp.edit();
//                            editor1.apply();
//                            refreshview();
                        });
//                        swarm3_scroll_layout.addView(item_view);
//                        // Set the task item
                        finalTask_list.add(task_name);
                        finalTime_list.add(task_time);
                        finalPort_list.add(task_port);
//                        SharedPreferences.Editor task_list_editor = task_list_sp.edit();
//                        task_list_editor.putStringSet("id_list", new HashSet<>(task_list));
//                        task_list_editor.putStringSet("time_list", new HashSet<>(time_list));
//                        task_list_editor.putStringSet("port_list", new HashSet<>(port_list));
//                        task_list_editor.apply();
                    })
                    .setNegativeButton("取消", (dialog, which) -> {

                    })
                    .setTitle("Add Task");
            builder.create().show();

            add_task_port1.setOnClickListener(v1 -> add_task_port1.toggle());
            add_task_port2.setOnClickListener(v1 -> add_task_port2.toggle());
            add_task_port3.setOnClickListener(v1 -> add_task_port3.toggle());
            add_task_port4.setOnClickListener(v1 -> add_task_port4.toggle());
            add_task_port5.setOnClickListener(v1 -> add_task_port5.toggle());
            add_task_port6.setOnClickListener(v1 -> add_task_port6.toggle());
            add_task_port7.setOnClickListener(v1 -> add_task_port7.toggle());
            add_task_port8.setOnClickListener(v1 -> add_task_port8.toggle());
        });
    }

    @SuppressLint("InflateParams")
    static void refreshview()
    {
        swarm3_scroll_layout.removeAllViews();
        if(task_list_sp.contains("id_list")){
            if(!task_list.isEmpty())
                for (int i = 0; i < task_list.size(); i++){
                item_layout =  LayoutInflater.from(Swarm3.context);
                item_view = item_layout.inflate(R.layout.task_item, null);
                task_time_text = item_view.findViewById(R.id.task_time_text);
                task_name_text = item_view.findViewById(R.id.task_name_text);
                task_port_text = item_view.findViewById(R.id.task_port_text);
                task_del = item_view.findViewById(R.id.task_del);
                task_name = task_list.get(i);
                task_time = time_list.get(i);
                task_port = port_list.get(i);
                task_name_text.setText(task_name);
                task_time_text.setText(task_time);
                task_port_text.setText(task_port);
                Log.d("init", "i = "+ i +"Task Name: " + task_list.get(i) + " Time: " + time_list.get(i) + " Port: " + port_list.get(i));
                int finalI = i;
                task_del.setOnClickListener(v -> {
                    Message message = new Message();
                    message.what = 6;//删除任务
                    message.obj = "DT" + " " + task_list.get(finalI);
                    mqtt_client.handler.sendMessage(message);

//                    task_list.remove(finalI);
//                    time_list.remove(finalI);
//                    port_list.remove(finalI);
//                    SharedPreferences.Editor editor = task_list_sp.edit();
//                    editor.putStringSet("id_list", new HashSet<>(task_list));
//                    editor.putStringSet("time_list", new HashSet<>(time_list));
//                    editor.putStringSet("port_list", new HashSet<>(port_list));
//                    editor.apply();
//                    refreshview();
                });
                swarm3_scroll_layout.addView(item_view);
            }
        }
    }

    static Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String message = msg.obj.toString();
            Log.d("MQTT", "Message: " + message);
            Toast.makeText(MainActivity.context, message, Toast.LENGTH_LONG).show();
            if(message.contains("ADD")){
                String sub_message = message.substring(4,8);
                int i = finalTask_list.indexOf(sub_message);
                    if(i != -1) {
                        task_list_sp = Swarm3.context.getSharedPreferences("task_list", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = task_list_sp.edit().clear();

                        task_name = sub_message;
                        task_time = finalTime_list.get(i);
                        task_port = finalPort_list.get(i);

                        task_list.add(task_name);
                        time_list.add(task_time);
                        port_list.add(task_port);

                        Log.d("init1", "Task Name: " + task_name + " Time: " + task_time + " Port: " + task_port);

                        editor.putStringSet("id_list", new HashSet<>(task_list));
                        editor.putStringSet("time_list", new HashSet<>(time_list));
                        editor.putStringSet("port_list", new HashSet<>(port_list));
                        editor.apply();
                        refreshview();
                    }

            }
            else if(message.contains("DEL"))
            {
                String sub_message = message.substring(4,8);
                for(int i = 0; i < task_list.size(); i++){
                    if(sub_message.equals(task_list.get(i))) {
                        task_list.remove(i);
                        time_list.remove(i);
                        port_list.remove(i);
                        task_list_sp = Swarm3.context.getSharedPreferences("task_list", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = task_list_sp.edit().clear();
                        editor.putStringSet("id_list", new HashSet<>(task_list));
                        editor.putStringSet("time_list", new HashSet<>(time_list));
                        editor.putStringSet("port_list", new HashSet<>(port_list));
                        editor.apply();
                        refreshview();
                        return;
                    }
                }
            }
        }
    };


}