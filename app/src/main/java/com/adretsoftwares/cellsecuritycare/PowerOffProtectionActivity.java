package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class PowerOffProtectionActivity extends AppCompatActivity {
    TextView textView21,textActive;
//    SwitchMaterial num1_switch;
    SharedPreferences preferences;
    EditText editPin;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off_protection);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editPin = (EditText)findViewById(R.id.editPin);
        textView21 = findViewById(R.id.num1_switch);
        textActive = findViewById(R.id.textView2);
//        num1_switch = findViewById(R.id.num1_switch);
        if(preferences.contains("battery")&&preferences.getBoolean("battery",false)){
            textActive.setTextColor(Color.parseColor("#4CAF50"));
            textActive.setText("Activated");
//            num1_switch.setChecked(true);
        }
//        num1_switch.setEnabled(false);
        editPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();

                if (newText.length() > 0) {
//                    num1_switch.setEnabled(true);
                    // The user has entered a non-empty value.
                    // You can take action here.
                } else {
                    // The EditText is empty.
                    // You can handle this case as well.
                }
            }
        });
//        textActive.setVisibility(View.GONE);
//        textView21.setVisibility(View.GONE);
//        num1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.d("boolean ", String.valueOf(b));
//                if(b){
//                    editor.putString("poweroffpass",editPin.getText().toString()).commit();
//                    textActive.setTextColor(Color.parseColor("#4CAF50"));
//                    textActive.setText("Activated");
//                    editor.putBoolean("poweroff",true).commit();
//                }else{
//                    textActive.setTextColor(Color.parseColor("#000000"));
//                    textActive.setText("Activate");
//                    editor.putBoolean("poweroff",false).commit();
//                }
//
//            }
//        });
    }
}