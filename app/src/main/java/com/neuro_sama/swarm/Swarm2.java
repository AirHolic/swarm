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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Swarm2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Swarm2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @SuppressLint("StaticFieldLeak")
    static LinearLayout light_switch, light_regulator, port_status;
    @SuppressLint("StaticFieldLeak")
    static SeekBar light_rank;
    @SuppressLint("StaticFieldLeak")
    static TextView light_rank_value;
    @SuppressLint({"UseSwitchCompatOrMaterialCode", "StaticFieldLeak"})
    static Switch pwm_light_switch,pwm_light_auto_mode;
    @SuppressLint("StaticFieldLeak")
    static Button refresh_ports_status;
    static CheckBox[] port_status_checkboxes = new CheckBox[8];
    static Toast toast;

    public Swarm2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Swarm2.
     */
    // TODO: Rename and change types and number of parameters
    public static Swarm2 newInstance(String param1, String param2) {
        Swarm2 fragment = new Swarm2();
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
        return inflater.inflate(R.layout.fragment_swarm2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        light_switch = view.findViewById(R.id.light_switch);
        light_regulator = view.findViewById(R.id.light_regulator);
        port_status = view.findViewById(R.id.port_status);

        toast = Toast.makeText(MainActivity.context, "操作成功", Toast.LENGTH_SHORT);

        light_switch.setOnClickListener(v -> {

            String[] light_switches = {"light1", "light2", "light3", "light4", "light5", "light6", "light7", "light8"};
            boolean[] checked = {false, false, false, false, false, false, false, false};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setMultiChoiceItems(light_switches, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                    .setPositiveButton("确定", (dialog, which) -> {
                        Message msg = new Message();
                        msg.what=3;//Device_Port
                        msg.arg1=0;//Device_Port
                        for (int i = 0; i < checked.length; i++)
                        {
                            if(checked[i])
                                msg.arg1 |= (1 << i);
                        }
                        msg.arg1 = 255 - msg.arg1;
                        msg.obj = "SET " + msg.arg1;
                        mqtt_client.handler.sendMessage(msg);
                        Log.d("light_switch", msg.obj.toString());

                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        // do something
                    })
                    .setTitle("灯光开关");
            builder.create().show();
        });

        light_regulator.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                LayoutInflater using_dialog_layout_xml = LayoutInflater.from(getContext());
                View light_regulator_view = using_dialog_layout_xml.inflate(R.layout.light_regulator, null);
                light_rank = light_regulator_view.findViewById(R.id.seekBar2);
                light_rank_value = light_regulator_view.findViewById(R.id.light_rank_value);
                pwm_light_switch = light_regulator_view.findViewById(R.id.pwm_light_switch);
                pwm_light_auto_mode = light_regulator_view.findViewById(R.id.pwm_light_auto_mode);
                pwm_light_auto_mode.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setView(light_regulator_view)
                        .setPositiveButton("确定", (dialog, which) -> {
                            Log.d("light_regulator", light_rank_value.getText().toString());
                            Message msg = new Message();
                            msg.what=4;
                            if(pwm_light_switch.isChecked()) {
                                if(pwm_light_auto_mode.isChecked()) {
                                    msg.obj="AUTO";
                                }
                                else {
                                    msg.obj = light_rank_value.getText().toString();
                                }
                            } else {
                                msg.obj="OFF";
                            }
                            mqtt_client.handler.sendMessage(msg);
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                            // do something
                        })
                        .setTitle("灯光调节");
                light_rank.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        light_rank_value.setText(String.valueOf(progress));
                        if(progress>0) {
                            pwm_light_switch.setChecked(true);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // do something
                    }
                });
                pwm_light_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(!isChecked) {
                        pwm_light_auto_mode.setChecked(false);
                        pwm_light_auto_mode.setEnabled(false);
                        light_rank.setProgress(0);
                        light_rank_value.setText("0");
                    }
                    else {
                        pwm_light_auto_mode.setEnabled(true);
                        light_rank.setProgress(24);
                        light_rank_value.setText("24");
                    }
                });
                pwm_light_auto_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    light_rank.setEnabled(!isChecked);
                });
                builder.create().show();
            }
        });

        port_status.setOnClickListener(v -> {
            String[] port_status = {"port0", "port1", "port2", "port3", "port4", "port5", "port6", "port7"};
            boolean[] checked = {false, false, false, false, false, false, false, false};

            LayoutInflater using_dialog_layout_xml = LayoutInflater.from(getContext());
            View port_status_view = using_dialog_layout_xml.inflate(R.layout.port_status_dialog, null);
            refresh_ports_status = port_status_view.findViewById(R.id.refresh_ports_status);
            port_status_checkboxes[0] = port_status_view.findViewById(R.id.port1);
            port_status_checkboxes[1] = port_status_view.findViewById(R.id.port2);
            port_status_checkboxes[2] = port_status_view.findViewById(R.id.port3);
            port_status_checkboxes[3] = port_status_view.findViewById(R.id.port4);
            port_status_checkboxes[4] = port_status_view.findViewById(R.id.port5);
            port_status_checkboxes[5] = port_status_view.findViewById(R.id.port6);
            port_status_checkboxes[6] = port_status_view.findViewById(R.id.port7);
            port_status_checkboxes[7] = port_status_view.findViewById(R.id.port8);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setView(port_status_view)
                    .setPositiveButton("确定", (dialog, which) -> {

                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        // do something
                    })
                    .setTitle("其他开关");
            builder.create().show();
            refresh_ports_status.setOnClickListener(v1 -> {
                Message msg = new Message();
                msg.what=3;//Device_Port
                msg.obj="RD";//Device_Type
                mqtt_client.handler.sendMessage(msg);
            });

        });
    }

    static Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 3)
            {
                if(msg.obj.toString().equals("OK"))
                    toast.show();
                else if (Integer.parseInt(msg.obj.toString()) >= 0 && Integer.parseInt(msg.obj.toString()) <= 255)
                    for(int i = 0; i < 8; i ++)
                    {
                        port_status_checkboxes[i].setChecked((Integer.parseInt(msg.obj.toString()) &
                                (1 << i)) != 0);
                    }
            }
        }
    };

}