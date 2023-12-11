package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class PocketThiefActivity extends AppCompatActivity{
    TextView textView2, textActive;
    SwitchMaterial num1_switch;
    EditText textView10;
    SharedPreferences preferences;
    AlertDialog alertDialog;
    private SensorManager sensorMan;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Notification _notification;
    private static final int SENSOR_SENSITIVITY = 4;
    public static boolean flag = false;
    @SuppressLint({"MissingInflatedId", "InvalidWakeLockTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_pocket_thief);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        textView10 = findViewById(R.id.textView10);
        alertDialog = new AlertDialog.Builder(this).create();
        textActive = findViewById(R.id.textView2);
        num1_switch = findViewById(R.id.num1_switch);
        if (preferences.getBoolean("pocketThief", false)) {
            num1_switch.setChecked(true);
        }
//        textActive.setVisibility(View.GONE);
//        textView2.setVisibility(View.GONE);
        //num1_switch.setEnabled(false);
        textView10.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(PocketThiefActivity.this, textView10.getText().toString(), Toast.LENGTH_LONG).show();
                boolean handled = false;
                if (textView10.getText().toString().trim().isEmpty()) {
                    num1_switch.setEnabled(false);
                    handled = true;
                } else {
                    handled = false;
                    num1_switch.setEnabled(true);
                }
                return handled;
            }
        });
        num1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!textView10.getText().toString().trim().isEmpty()) {
                    if (b) {
                        SharedPreferences.Editor editor1 = preferences.edit();
                        editor1.putString("passwordKey", textView10.getText().toString().trim()).commit();
                        Log.d("boolean ", String.valueOf(b));
                        editor.putBoolean("pocketThief", true).commit();
                        textActive.setTextColor(Color.parseColor("#4CAF50"));
                        textActive.setText("Activated");
                        editor.putBoolean("motion", true).commit();
                        alertDialog.setTitle("Will Be Activated In 10 Seconds,Keep your phone in pocket");
                        alertDialog.setMessage("00:10");
                        //Toast.makeText(MainActivity.this, "Motion Switch On", Toast.LENGTH_SHORT).show();
                        CountDownTimer cdt = new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                //info.setVisibility(View.GONE);
                                // mSwitchSet = 1;
                                alertDialog.hide();
//                                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//                                accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
////                            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
////                            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//                                //        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
//                                //        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//
//                                //        sensorMan.registerListener(this, accelerometer,
//                                //                SensorManager.SENSOR_DELAY_UI);
//                                if (accelerometer != null) {
//                                    mSensorManager.registerListener(PocketThiefActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//                                } else {
//                                    Toast.makeText(PocketThiefActivity.this, "Accelerometer not available on this device.", Toast.LENGTH_LONG).show();
//                                    finish();
//                                    // Log.e("MotionActivity", "Accelerometer not available on this device.");
//                                }
////                                                mSensorManager.registerListener(PocketThiefActivity.this, mSensor,
//                                                        SensorManager.SENSOR_DELAY_NORMAL);
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(new Intent(PocketThiefActivity.this, PocketService.class));
                            } else {
                                startService(new Intent(PocketThiefActivity.this, PocketService.class));
                            }
//                                Toast.makeText(PocketThiefActivity.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();

                            }
                        }.start();
                        alertDialog.show();
                        alertDialog.setCancelable(false);


                    } else {
                        textActive.setTextColor(Color.parseColor("#000000"));
                        textActive.setText("Activate");
                        editor.putBoolean("pocketThief", false).commit();
                        Toast.makeText(PocketThiefActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                        textActive.setTextColor(Color.parseColor("#000000"));
                        textActive.setText("Activate");
                        editor.putBoolean("motion", false).commit();
                        stopService(new Intent(PocketThiefActivity.this, PocketService.class));
                    }
                }else{
                    num1_switch.setChecked(false);
                    Toast.makeText(PocketThiefActivity.this, "Enter pin first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !flag) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            // Calculate the magnitude of acceleration
//            double acceleration = Math.sqrt(x * x + y * y + z * z);
//
//            // You can set a threshold for motion detection
//            if (acceleration > 20) {
//                Log.d("MotionActivity", "Motion detected!");
//                Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                //To play audio on clap
//                //playAudio();
//                flag = true;
//                ComponentName cn = new ComponentName(this, EnterPin.class);
//                intent.setComponent(cn);
//                getApplicationContext().startActivity(intent);
//            }
//        }
////        if (event.sensor.getType()== Sensor.TYPE_PROXIMITY){
////            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
////                if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
////                    //near
//////                    Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
////
////                } else{
////
////                    //  Toast.makeText(this, "sensor run Start successfully", Toast.LENGTH_SHORT).show();
////                    //finish();
////                    //far
//////                    Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
////
////                }
////            }
////        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(sensorMan!=null) {
//          //  sensorMan.unregisterListener(this);
//        }if(mSensorManager!=null) {
//          //  mSensorManager.unregisterListener(this);
//        }
    }
}