package com.example.prittercare.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prittercare.R;
import com.example.prittercare.controller.StyleManager;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.MQTTHelper;
import com.google.android.material.tabs.TabLayout;


public class TemperatureAndHumidtyFragment extends Fragment {

    private MQTTHelper mqttHelper;

    // 사용자 및 장치 정보
    private String userid = DataManager.getInstance().getUserName(); // 사용자 ID
    private String serialnumber = DataManager.getInstance().getCurrentCageSerialNumber();
    private String animalType;

    // MQTT 토픽
    private String TEMPERATURE_TOPIC;
    private String HUMIDITY_TOPIC;

    private StyleManager styleManager;

    public static TemperatureAndHumidtyFragment newInstance(MQTTHelper mqttHelper) {
        TemperatureAndHumidtyFragment fragment = new TemperatureAndHumidtyFragment();
        fragment.mqttHelper = mqttHelper;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_temperature_and_humidty, container, false);

        TEMPERATURE_TOPIC = "${userid}/${serialnumber}/temperature"
                .replace("${userid}", userid)
                .replace("${serialnumber}", serialnumber);
        HUMIDITY_TOPIC = "${userid}/${serialnumber}/humidity"
                .replace("${userid}", userid)
                .replace("${serialnumber}", serialnumber);

        setupButtonListeners(rootView);
        applyAnimalStyle(rootView);

        return rootView;
    }

    private void setupButtonListeners(View rootView) {
        rootView.findViewById(R.id.btn_set_temperature).setOnClickListener(view -> {
            String temperature = ((EditText) rootView.findViewById(R.id.et_set_temperature)).getText().toString();
            sendCommand(TEMPERATURE_TOPIC, temperature, "온도 설정: " + temperature);
        });

        rootView.findViewById(R.id.btn_set_humidity).setOnClickListener(view -> {
            String humidity = ((EditText) rootView.findViewById(R.id.et_set_humidity)).getText().toString();
            sendCommand(HUMIDITY_TOPIC, humidity, "습도 설정: " + humidity);
        });
    }

    private void applyAnimalStyle(View rootView) {
        animalType = DataManager.getInstance().getCurrentAnimalType();
        styleManager = new StyleManager(getContext(), animalType);

        // TextView 스타일 변경
        TextView temperatureGetTextView = rootView.findViewById(R.id.tv_get_temperature);
        TextView humidityGetTextView = rootView.findViewById(R.id.tv_get_humidity);
        temperatureGetTextView.setTextColor(styleManager.getBasicColor02Id());
        humidityGetTextView.setTextColor(styleManager.getBasicColor03Id());

        // 버튼 스타일 변경
        AppCompatButton temperatureButton = rootView.findViewById(R.id.btn_set_temperature);
        AppCompatButton humidityButton = rootView.findViewById(R.id.btn_set_humidity);
        temperatureButton.setBackgroundResource(styleManager.getButton02ShapeId());
        temperatureButton.setTextColor(styleManager.getBasicColor02Id());
        humidityButton.setBackgroundResource(styleManager.getButton02ShapeId());
        humidityButton.setTextColor(styleManager.getBasicColor02Id());
    }

    private void sendCommand(String topic, String message, String successToast) {
        if(mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish(topic, message, 1);
            Toast.makeText(requireContext(), successToast, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(requireContext(), "MQTT 연결 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
