package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class StopUsbConnectionActivity extends AppCompatActivity {
    TextView textActive;
    EditText editText;
    SwitchMaterial num1_switch;
    SharedPreferences preferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_usb_connection);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editText = (EditText) findViewById(R.id.editText);
        num1_switch = findViewById(R.id.num1_switch);
        textActive = findViewById(R.id.textView2);
        if(preferences.contains("usbblock")&&preferences.getBoolean("usbblock",false)){
            textActive.setTextColor(Color.parseColor("#4CAF50"));
            textActive.setText("Activated");
            num1_switch.setChecked(true);
        }
        num1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    textActive.setTextColor(Color.parseColor("#4CAF50"));
                    textActive.setText("Activated");
                    editor.putString("usbPin",editText.getText().toString()).commit();
                    editor.putBoolean("usbblock",true).commit();

                }else{
                    textActive.setTextColor(Color.parseColor("#000000"));
                    textActive.setText("Activate");
                    editor.putBoolean("usbblock",false).commit();

                }
            }
        });
    }
}