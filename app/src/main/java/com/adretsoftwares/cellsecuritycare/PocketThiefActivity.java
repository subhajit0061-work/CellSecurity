package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.adretsoftwares.cellsecuritycare.daos.DateDao;
import com.adretsoftwares.cellsecuritycare.database.CellSecurityDatabase;
import com.adretsoftwares.cellsecuritycare.entities.DateEntity;
import com.adretsoftwares.cellsecuritycare.util.LocationService;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class PocketThiefActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener, EasyPermissions.PermissionCallbacks {
    TextView textView2, textActive;
    private List<Notify> listSuperHeroes;
    private List<DateEntity> dateEntities;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private ActivityResultLauncher<String> backgroundLocation;
    private int requestCount = 1;
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
        initRecyclerView();
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
                } else {
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

    void initRecyclerView() {
        if (!preferences.contains("admin") && !preferences.getBoolean("admin", false)) {
            Toast.makeText(this, "Required admin privilege to use this feature", Toast.LENGTH_LONG).show();
            finish();
        }
        Intent service = new Intent(this, LocationService.class);
        Context context = this;
        Bundle b = getIntent().getExtras();
        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait ....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        checkEasyPermission();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSim);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView dateRecyclerView = findViewById(R.id.date_recyclerViewSim);
        dateRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateEntities = new ArrayList<>();
        listSuperHeroes = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        //Calling method to get data to fetch data
        getData();
        CellSecurityDatabase cellSecurityDatabase = CellSecurityDatabase.getInstance(context);
        DateDao dateDao = cellSecurityDatabase.dateDao();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // Perform your database operation here
                dateEntities.addAll(dateDao.getAllDates());
                System.out.println(dateEntities);

            }
        });

        backgroundLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {


                    if (isGranted) {
                        // Permission granted, proceed with location-related tasks
                        Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                });


        // Permission granted, proceed with location-related tasks
        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                        }
                    }


                    if (isGranted) {
                        // Permission granted, proceed with location-related tasks
                        Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                });
        //Adding an scroll change listener to recyclerview
        recyclerView.setOnScrollChangeListener(PocketThiefActivity.this);

        //initializing our adapter
        adapter = new CardAdapter(listSuperHeroes, this);
        RecyclerView.Adapter dateAdapter = new DateAdapter(dateEntities);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
        dateRecyclerView.setAdapter(dateAdapter);
    }

    private void checkEasyPermission() {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, 22, Manifest.permission.ACCESS_FINE_LOCATION)
                        .setRationale("location permission")

                        .build());
    }

    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(requestCount));
        //Incrementing the request counter
        requestCount++;
    }

    private JsonArrayRequest getDataFromServer(int requestCount) {
        //Initializing ProgressBar
        final ProgressBar progressBar = findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://cellsecuritycare.com/api/getNotifications.php?mobile=" + preferences.getString("mobile_number", ""),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Calling method parseData to parse the json response
                        parseData(response);
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        //If an error occurs that means end of the list has reached
                        Toast.makeText(PocketThiefActivity.this, "No More Items Available", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            //Creating the superhero object
            Notify notify = new Notify();
            JSONObject json = null;
            try {
                //Getting json

                json = array.getJSONObject(i);

                Log.d("thetime", "the sarra" + json);
                //Adding data to the superhero object
                notify.setImageUrl(json.getString("imageUrl"));
                notify.setImageUr2(json.getString("imageUrl2"));
                notify.setMessage(json.getString("message"));
                notify.setDate(json.getString("createat"));

                notify.setCoordinates(json.getString("coordinates"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Adding the superhero object to the list
            listSuperHeroes.add(notify);
        }

        //Notifying the adapter that data has been added or changed
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}