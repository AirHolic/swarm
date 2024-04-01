package com.neuro_sama.swarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    static TextView desk_time_text, desk_name_text, desk_port_text;
    @SuppressLint("StaticFieldLeak")
    static View item_view;
    static LayoutInflater item_layout;
    @SuppressLint("StaticFieldLeak")
    static Button desk_del;


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

        LinearLayout swarm3_scroll_layout = view.findViewById(R.id.swarm3_scroll_layout);
        Button swarm3_add_task = view.findViewById(R.id.swarm3_add_task);

        swarm3_scroll_layout.removeAllViews();

        List<String> task_list;
        List<String> time_list;
        List<String> port_list;


        SharedPreferences task_list_sp = requireContext().getSharedPreferences("task_list", Context.MODE_PRIVATE);
        task_list = new ArrayList<>(task_list_sp.getStringSet("id_list", new HashSet<>()));
        time_list = new ArrayList<>(task_list_sp.getStringSet("time_list", new HashSet<>()));
        port_list = new ArrayList<>(task_list_sp.getStringSet("port_list", new HashSet<>()));
        // Add a new task
        List<String> finalTask_list = task_list;
        List<String> finalTime_list = time_list;
        List<String> finalPort_list = port_list;
        if(task_list_sp.contains("id_list")){
            for (int i = 0; i < task_list.size(); i++){
                item_layout =  LayoutInflater.from(getContext());
                item_view = item_layout.inflate(R.layout.task_item, null);
                desk_time_text = item_view.findViewById(R.id.desk_time_text);
                desk_name_text = item_view.findViewById(R.id.desk_name_text);
                desk_port_text = item_view.findViewById(R.id.desk_port_text);
                desk_del = item_view.findViewById(R.id.desk_del);
                desk_name_text.setText(task_list.get(i));
                desk_time_text.setText(time_list.get(i));
                desk_port_text.setText(port_list.get(i));
                int finalI = i;
                desk_del.setOnClickListener(v -> {
                    Message message = new Message();
                    message.what = 6;//删除任务
                    message.obj = "DT" + " " + finalTask_list.get(finalI);
                    mqtt_client.handler.sendMessage(message);

                    finalTask_list.remove(finalI);
                    finalTime_list.remove(finalI);
                    finalPort_list.remove(finalI);
                    SharedPreferences.Editor editor = task_list_sp.edit();
                    editor.putStringSet("id_list", new HashSet<>(finalTask_list));
                    editor.putStringSet("time_list", new HashSet<>(finalTime_list));
                    editor.putStringSet("port_list", new HashSet<>(finalPort_list));
                    editor.apply();
                    onViewCreated(view, savedInstanceState);
                });
                swarm3_scroll_layout.addView(item_view);
            }
        }





        swarm3_add_task.setOnClickListener(v -> {
            Random random = new Random();
            int task_id = random.nextInt(65535);
            String hex_task_id = Integer.toHexString(task_id);
            if (hex_task_id.length() < 4) {
                while (hex_task_id.length() < 4)
                    hex_task_id = "0" + hex_task_id;
            }

            LayoutInflater using_dialog_layout_xml = LayoutInflater.from(getContext());
            View dialog_view = using_dialog_layout_xml.inflate(R.layout.add_task, null);

            TextView add_task_name = dialog_view.findViewById(R.id.add_task_name);
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

            add_task_name.setText(hex_task_id);
            boolean[] checked = {false, false, false, false, false, false, false, false};
            @SuppressLint("SetTextI18n") AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setView(dialog_view)
                    .setPositiveButton("添加任务", (dialog, which) -> {
                        // Add the task
                        String task_name = add_task_name.getText().toString();
                        String task_time_hour = add_task_time_hour.getText().toString();
                        String task_time_minute = add_task_time_minute.getText().toString();
                        String task_time_second = add_task_time_second.getText().toString();
                        item_layout =  LayoutInflater.from(getContext());
                        item_view = item_layout.inflate(R.layout.task_item, null);
                        desk_time_text = item_view.findViewById(R.id.desk_time_text);
                        desk_name_text = item_view.findViewById(R.id.desk_name_text);
                        desk_port_text = item_view.findViewById(R.id.desk_port_text);
                        desk_del = item_view.findViewById(R.id.desk_del);

                        if(!task_time_hour.equals("") && !task_time_minute.equals("") && !task_time_second.equals("")) {
                            Log.d("Task", "Task Name: " + task_name + " Time: " +
                                    task_time_hour + ":" + task_time_minute + ":" + task_time_second);

                            Message msg = new Message();
                            msg.what = 6;//Device_Timer
                            msg.obj = 0;//msg
                            msg.arg1 = 0;//port

                            for (int i = 0; i < add_task_ports.length; i++) {
                                checked[i] = add_task_ports[i].isChecked();
                                if (checked[i]) {
                                    Log.d("Task", "Port " + i + " is checked");
                                    desk_port_text.setText(desk_port_text.getText() + " " + i);
                                    msg.arg1 |= (1 << i);
                                }
                            }

                            msg.obj = "AT " + task_name + " " + task_time_hour + task_time_minute + task_time_second + " " + msg.arg1;
                            mqtt_client.handler.sendMessage(msg);
                            // Save the task
                            desk_name_text.setText(task_name);
                            desk_time_text.setText(task_time_hour + ":" + task_time_minute + ":" + task_time_second);
                            desk_del.setOnClickListener(v1 -> {
                                Message message = new Message();
                                message.what = 6;//删除任务
                                message.obj = "DT" + " " + task_name;
                                mqtt_client.handler.sendMessage(message);

                                finalTask_list.remove(task_name);
                                finalTime_list.remove(task_time_hour + ":" + task_time_minute + ":" + task_time_second);
                                finalPort_list.remove(desk_port_text.getText().toString());
                                SharedPreferences.Editor task_list_editor = task_list_sp.edit();
                                task_list_editor.putStringSet("id_list", new HashSet<>(finalTask_list));
                                task_list_editor.putStringSet("time_list", new HashSet<>(finalTime_list));
                                task_list_editor.putStringSet("port_list", new HashSet<>(finalPort_list));
                                task_list_editor.apply();
                                SharedPreferences.Editor editor1 = task_list_sp.edit();
                                editor1.apply();
                                onViewCreated(view, savedInstanceState);
                            });
                            swarm3_scroll_layout.addView(item_view);
                            // Set the task item
                            finalTask_list.add(task_name);
                            finalTime_list.add(task_time_hour + ":" + task_time_minute + ":" + task_time_second);
                            finalPort_list.add(desk_port_text.getText().toString());
                            SharedPreferences.Editor task_list_editor = task_list_sp.edit();
                            task_list_editor.putStringSet("id_list", new HashSet<>(finalTask_list));
                            task_list_editor.putStringSet("time_list", new HashSet<>(finalTime_list));
                            task_list_editor.putStringSet("port_list", new HashSet<>(finalPort_list));
                            task_list_editor.apply();
                        }
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

    static Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };


}