package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.PocketThiefActivity.flag;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adretsoftwares.cellsecuritycare.common.CapturePhoto;
import com.adretsoftwares.cellsecuritycare.services.PickPocketService;

public class EnterPin extends AppCompatActivity {
    EditText etEnterPin;
    SharedPreferences sharedpreferences;
    Button deactivate;
    View view;


    //Disable Back Key
    @Override
    public void onBackPressed() {
        //return nothing
        return;
    }

    //Disable Tasks Key
    @Override
    protected void onPause() {
        super.onPause();


    }

    //Disable Volume Key
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Do your thing

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        //intertiate

        /*final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000};
        vb.vibrate(pattern, 0);*/
        deactivate = (Button) findViewById(R.id.deactivate);
        sharedpreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String password = sharedpreferences.getString("passwordKey", "");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        final MediaPlayer mPlayer = MediaPlayer.create(EnterPin.this, R.raw.countdownsound);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        mPlayer.start();
        mPlayer.setLooping(true);
        etEnterPin = (EditText) findViewById(R.id.etEnterPin);
        deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = etEnterPin.getText().toString();
                if (pin.equals(password)) {
                    mPlayer.stop();
                    //vb.cancel();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("pocketThief", false).apply();
                    flag = false;
                    stopService(new Intent(EnterPin.this, PocketService.class));
                    stopService(new Intent(EnterPin.this, PickPocketService.class));
                    startActivity(new Intent(EnterPin.this, MainActivity.class));
                    finish();
                } else {
                    etEnterPin.getText().clear();
                    etEnterPin.setError("Wrong Pin!");
                    etEnterPin.requestFocus();
                    new CapturePhoto().click(EnterPin.this, "Wrong PIN entered");
                }
            }
        });
        etEnterPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {


                }
                return handled;
            }
        });

    }
}