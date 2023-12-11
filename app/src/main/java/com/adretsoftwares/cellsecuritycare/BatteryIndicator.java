package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class BatteryIndicator extends AppCompatActivity {
    TextView textView21,textActive;
    SwitchMaterial num1_switch;
    SharedPreferences preferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        textView21 = findViewById(R.id.num1_switch);
        textActive = findViewById(R.id.textView2);
        num1_switch = findViewById(R.id.num1_switch);
        if(preferences.contains("battery")&&preferences.getBoolean("battery",false)){
            textActive.setTextColor(Color.parseColor("#4CAF50"));
            textActive.setText("Activated");
            num1_switch.setChecked(true);
        }
//        textActive.setVisibility(View.GONE);
//        textView21.setVisibility(View.GONE);
        num1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("boolean ", String.valueOf(b));
                if(b){
                    textActive.setTextColor(Color.parseColor("#4CAF50"));
                    textActive.setText("Activated");
                    editor.putBoolean("battery",true).commit();
                }else{
                    textActive.setTextColor(Color.parseColor("#000000"));
                    textActive.setText("Activate");
                    editor.putBoolean("battery",false).commit();
                }

            }
        });
    }
}
