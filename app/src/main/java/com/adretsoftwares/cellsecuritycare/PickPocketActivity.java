package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adretsoftwares.cellsecuritycare.daos.DateDao;
import com.adretsoftwares.cellsecuritycare.database.CellSecurityDatabase;
import com.adretsoftwares.cellsecuritycare.databinding.ActivityPickPocketBinding;
import com.adretsoftwares.cellsecuritycare.entities.DateEntity;
import com.adretsoftwares.cellsecuritycare.services.PickPocketService;
import com.adretsoftwares.cellsecuritycare.util.LocationService;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class PickPocketActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener, EasyPermissions.PermissionCallbacks {

    private ActivityPickPocketBinding binding;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private AlertDialog alertDialog;
    private List<Notify> listSuperHeroes;
    private List<DateEntity> dateEntities;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private ActivityResultLauncher<String> backgroundLocation;
    private int requestCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickPocketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        alertDialog = new AlertDialog.Builder(this).create();
        if (preferences.getBoolean("pocketThief", false)) {
            binding.switchActivate.setChecked(true);
        }
        initRecyclerView();

        binding.switchActivate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!binding.txtPin.getText().toString().trim().isEmpty()) {
                if (b) {
                    SharedPreferences.Editor editor1 = preferences.edit();
                    editor1.putString("passwordKey", binding.txtPin.getText().toString().trim()).apply();
                    editor.putBoolean("pocketThief", true).apply();
                    editor.putBoolean("motion", true).apply();
                    alertDialog.setTitle("Will Be Activated In 10 Seconds,Keep your phone in pocket");
                    alertDialog.setMessage("00:10");
                    new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            alertDialog.hide();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(new Intent(PickPocketActivity.this, PickPocketService.class));
                            } else {
                                startService(new Intent(PickPocketActivity.this, PickPocketService.class));
                            }
                        }
                    }.start();
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                } else {
                    editor.putBoolean("pocketThief", false).apply();
                    Toast.makeText(PickPocketActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("motion", false).apply();
                    stopService(new Intent(PickPocketActivity.this, PickPocketService.class));
                }
            } else {
                binding.switchActivate.setChecked(false);
                Toast.makeText(PickPocketActivity.this, "Enter pin first", Toast.LENGTH_SHORT).show();
            }
        });

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
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
        recyclerView.setOnScrollChangeListener(PickPocketActivity.this);

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
                        Toast.makeText(PickPocketActivity.this, "No More Items Available", Toast.LENGTH_SHORT).show();
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