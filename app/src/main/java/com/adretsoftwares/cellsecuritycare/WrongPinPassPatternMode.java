package com.adretsoftwares.cellsecuritycare;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adretsoftwares.cellsecuritycare.daos.DateDao;
import com.adretsoftwares.cellsecuritycare.database.CellSecurityDatabase;
import com.adretsoftwares.cellsecuritycare.entities.DateEntity;
import com.adretsoftwares.cellsecuritycare.util.LocationService;
import com.adretsoftwares.cellsecuritycare.util.UtilityAndConstant;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class WrongPinPassPatternMode extends AppCompatActivity implements RecyclerView.OnScrollChangeListener , EasyPermissions.PermissionCallbacks {
    /**
     * Use will tick the adminEnabled check box, this will prompt for
     * enabling admin privilege (Monitor screen unlock attempts)
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static Intent service;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private ActivityResultLauncher<String> backgroundLocation;
    private SwitchMaterial adminEnabled,switch1,switch2,switch3;
    /**
     * DevicePolicyManager is interface used for managing policies enforced on device
     * This app uses "watch-login" policy
     */
    private DevicePolicyManager devicePolicyManager;
    /**
     * statusTV: displays OFF is admin privilege is not given, else ON
     * countTV: displays the number of incorrect PIN attempts
     * reset: resets the number of incorrect PIN attempts to 0
     */
 //   private TextView statusTV,countTV,reset;
    /**
     * emailET: The user will enter the destination email-address here
     */

    /**
     * tickIV button sets the email-address
     */
    private Button tickIV;
    /**
     * compName: A component name object which will identify loginWatch class
     */
    private ComponentName compName;
    private boolean isAdminActive;
    EditText numb1txt,numb2txt,numb3txt;
    SharedPreferences preferences;




    Context context;
    TextView textView14;
    ImageView imageView5;
    //Creating a List of superheroes
    private List<Notify> listSuperHeroes;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter dateAdapter;
    private RecyclerView dateRecyclerView;

    //Volley Request Queue
    private RequestQueue requestQueue;
    Button btnUpdate;
    //The request counter to send ?page=1, ?page=2  requests
    private int requestCount = 1;

    private List<DateEntity> dateEntities;
    ProgressDialog progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_pin_pass_pattern_mode);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        if (!preferences.contains("admin") && !preferences.getBoolean("admin", false)) {
            Toast.makeText(this, "Required admin privilege to use this feature", Toast.LENGTH_LONG).show();
            finish();
        }
        service = new Intent(this, LocationService.class);
        //   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Inflate objects from activity_main.xml
        context = this;
        textView14 = findViewById(R.id.textView14);
        Bundle b = getIntent().getExtras();
        imageView5 = findViewById(R.id.imageView5);
        String flag = b.getString("flag");
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait ....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        btnUpdate = findViewById(R.id.btnUpdate);
        adminEnabled = findViewById(R.id.num1_switch);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        // tickIV = findViewById(R.id.tv_tick);
        numb1txt = findViewById(R.id.numb1txt);
        numb2txt = findViewById(R.id.numb2txt);
        numb3txt = findViewById(R.id.numb3txt);
//        statusTV=findViewById(R.id.tv_status);
//        countTV = findViewById(R.id.tv_count);
//        reset = findViewById(R.id.tv_Reset);
        if (flag.trim().equals("on")) {
            imageView5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.thief));
            textView14.setText("Pick Pocket Protection \n Mode");
        }

        checKEasyPermissiom();
//        requestLocationPermissions(this);
//
//        if (!checkLocationServices()){
//            showLocationServicesDialog();
//        }


        SharedPreferences.Editor editor = preferences.edit();
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, loginWatch.class);

        numb1txt.setText(preferences.getString("emergencyMobile1", null));
        numb2txt.setText(preferences.getString("emergencyMobile2", null));
        numb3txt.setText(preferences.getString("emergencyMobile3", null));
        if (preferences.getBoolean("is1Enable", false)) {
            switch1.toggle();
        }
        if (preferences.getBoolean("is2Enable", false)) {
            switch2.toggle();
        }
        if (preferences.getBoolean("is3Enable", false)) {
            switch3.toggle();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRegister(preferences.getString("mobile_number", ""), preferences.getString("email", ""), preferences.getString("name", ""), numb1txt.getText().toString().trim(), numb2txt.getText().toString().trim()
                        , numb3txt.getText().toString().trim());
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dateRecyclerView = findViewById(R.id.date_recyclerView);
        dateRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateEntities = new ArrayList<>();
        //Initializing our superheroes list
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
                dateEntities.addAll(dateDao.getAllDates()) ;
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


        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                        }
                    }


                    if (isGranted) {
                        // Permission granted, proceed with location-related tasks
                        Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                });
        //Adding an scroll change listener to recyclerview
        recyclerView.setOnScrollChangeListener(this);

        //initializing our adapter
        adapter = new CardAdapter(listSuperHeroes, this);
        dateAdapter = new DateAdapter(dateEntities);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
        dateRecyclerView.setAdapter(dateAdapter);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("is1Enable", b).commit();
                if (b && numb1txt.getText().toString().trim() != null) {
                    Toast.makeText(context, "Emergency mobile 1 enabled", Toast.LENGTH_LONG).show();
                } else if (!b) {
                    Toast.makeText(context, "Emergency mobile 1 disabled", Toast.LENGTH_LONG).show();
                }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("is2Enable", b).commit();
                if (b && numb2txt.getText().toString().trim() != null) {
                    Toast.makeText(context, "Emergency mobile 2 enabled", Toast.LENGTH_LONG).show();
                } else if (!b) {
                    Toast.makeText(context, "Emergency mobile 2 disabled", Toast.LENGTH_LONG).show();
                }
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("is3Enable", b).commit();
                if (b && numb3txt.getText().toString().trim() != null) {
                    Toast.makeText(context, "Emergency mobile 3 enabled", Toast.LENGTH_LONG).show();
                } else if (!b) {
                    Toast.makeText(context, "Emergency mobile 3 disabled", Toast.LENGTH_LONG).show();
                }
            }
        });
        /**
         * If security service is not running, then start it
         * Foreground service will keep on running, even if the app stops
         */

        /**
         * Clicking on tickIV button will set the email-address
         * The text-view hint shall also be set same
         */
//        tickIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SecurityService.senderEmail = emailET.getText().toString();
//                emailET.setHint(SecurityService.senderEmail);
//                Toast.makeText(WrongPinPassPatternMode.this, "Email Set:" + SecurityService.senderEmail, Toast.LENGTH_SHORT).show();
//            }
//        });

        /**
         * Clicking on the reset view will reset the incorrect PIN cout
         */
//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SecurityService.failedPasswordCount = 0;
//                countTV.setText(String.valueOf(0));
//            }
//        });

        /**
         *If device admin is not enabled, then checking the box will open a menu
         * the user will be asked to enable admin privilege
         */
//        adminEnabled.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isAdminActive) {
//                    //ACTION_ADD_DEVICE_ADMIN: Prompt user to give admin privilege to this app
//                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                    //EXTRA_DEVICE_ADMIN: The ComponentName identifier object of the admin component
//                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
//                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin Access Needed To Check If Incorrect PIN Entered");
//                    startActivity(intent);  //The request code will be used while fetching result
//                    isAdminActive = devicePolicyManager.isAdminActive(compName);   //Check if the admin is enabled
//                } else {
//                    /***
//                     * If admin privilege was already granted (checkbox ticked), then disable admin privelege
//                     * set isAdminActive to false
//                     * set status to OFF (also change color to red)
//                     * Show a toast to alert the user
//                     */
//                    devicePolicyManager.removeActiveAdmin(compName);
//                    isAdminActive = false;
//                    // statusTV.setText("OFF");
//                    // statusTV.setTextColor(Color.RED);
//                    Toast.makeText(WrongPinPassPatternMode.this, "Admin Privilege Revoked", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        requestLocationPermissions(this);

        isAdminActive = devicePolicyManager.isAdminActive(compName);
        if(SecurityService.senderEmail!=null)
        //    emailET.setHint(SecurityService.senderEmail);
        if(isAdminActive){
          //  statusTV.setText("ON");
          //  statusTV.setTextColor(Color.GREEN);
        }
        else{
           // statusTV.setText("OFF");
            //statusTV.setTextColor(Color.RED);
        }
     //   countTV.setText(String.valueOf(SecurityService.failedPasswordCount));
        //First Set the CheckBox
//        if(!isAdminActive && adminEnabled.isChecked()){
//            adminEnabled.toggle();
//        }
//        else if(isAdminActive && !adminEnabled.isChecked()){
//            adminEnabled.toggle();
//        }
//        else if(!isAdminActive)
//            Toast.makeText(WrongPinPassPatternMode.this,"Admin Privilege Needed",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 3) {
            if (resultCode == RESULT_OK)
                Toast.makeText(WrongPinPassPatternMode.this, "Admin Privilege Granted", Toast.LENGTH_SHORT);
            else
                Toast.makeText(WrongPinPassPatternMode.this, "Failed to Enable Admin Privilege", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(requestCount));
        //Incrementing the request counter
        requestCount++;
    }

    //This method will parse json data
    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            //Creating the superhero object
            Notify notify = new Notify();
            JSONObject json = null;
            try {
                //Getting json

                json = array.getJSONObject(i);

                Log.d("thetime","the sarra"+json);
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

    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    //Overriden method to detect scrolling
    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //Ifscrolled at last then
        if (isLastItemDisplaying(recyclerView)) {
            //Calling the method getdata again
         //   getData();
        }
    }

    private JsonArrayRequest getDataFromServer(int requestCount) {
        //Initializing ProgressBar
        final ProgressBar progressBar = findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://cellsecuritycare.com/api/getNotifications.php?mobile="+preferences.getString("mobile_number",""),
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
                        Toast.makeText(WrongPinPassPatternMode.this, "No More Items Available", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    private void LoginRegister(final String mobile_number, final String email, final String name, final String emergencyMobile1,
                               final String emergencyMobile2,final String emergencyMobile3) {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.hide();
                try {
                    SharedPreferences.Editor editor = preferences.edit();
                    //converting response to json object
                    Log.e("Error ",s);
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (obj.getBoolean("error")) {
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        editor.putString("emergencyMobile1", emergencyMobile1);
                        editor.putString("emergencyMobile2", emergencyMobile2);
                    } else {
                        JSONObject user = obj.getJSONObject("user");

                        editor.putString("emailId", user.getString("email"));
                        editor.putString("name", user.getString("name"));
                        editor.putString("emergencyMobile1", user.getString("emergencyMobile1"));
                        editor.putString("emergencyMobile2", user.getString("emergencyMobile2"));
                        editor.putString("emergencyMobile3", emergencyMobile3);

                        //editor.putString("mobile_number", user.getString("mobile_number"));
                        editor.apply();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    }


                    editor.putString("emergencyMobile3", emergencyMobile3);


                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                Log.e("mobile_number",mobile_number);
                Log.e("emergencyMobile1",emergencyMobile1);
                Log.e("emergencyMobile2",emergencyMobile2);
                Log.e("emergencyMobile3",emergencyMobile3);

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_number", mobile_number);
                params.put("name", name);
                params.put("emailId", email);
                params.put("emergencyMobile1", emergencyMobile1);
                params.put("emergencyMobile2", emergencyMobile2);
                params.put("emergencyMobile3", emergencyMobile3);
                // params.put("token", token);

                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=update", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }

    private void checKEasyPermissiom(){
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, 22, Manifest.permission.ACCESS_FINE_LOCATION)
                        .setRationale("location permission")

                        .build());
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Log.d("permissiongra","Permissin granted");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!foregroundServiceRunning()){
                startForegroundService(service);
            }

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        checKEasyPermissiom();
    }

    public void requestLocationPermissions(Activity activity) {
        // Check if the app has both fine and background location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int fineLocationPermission = ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION);
            int backgroundLocationPermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);



            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            ) {
//                requestPermissionLauncher.launch(
//                                android.Manifest.permission.ACCESS_FINE_LOCATION
//
//                );

//                requestPermissionLauncher.launch(
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION
//
//                );

                ActivityCompat.requestPermissions(WrongPinPassPatternMode.this,
                        new String[]{ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
                Log.d("permission","permsiion screen is called");

            }




             else {
                // Location permissions are already granted
                // You can proceed with your location-related tasks here
              cheGpsIonOrOff();
                Toast.makeText(activity, "Location Permissions Granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            // For devices below Android Q, only fine location permission is needed
            if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                // If fine location permission is not granted, request it
                ActivityCompat.requestPermissions(activity,
                        new String[]{ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Fine location permission is already granted
                // You can proceed with your location-related tasks here
                cheGpsIonOrOff();
                Toast.makeText(activity, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cheGpsIonOrOff() {


        if (checkLocationServices()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            }
        }else {
            showLocationServicesDialog();
        }
    }



    private void showLocationServicesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Required");
        builder.setMessage("This service requires location services to function properly. Please enable GPS or network-based location services.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open location settings to enable location services
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        builder.setCancelable(true); // Prevent the user from dismissing the dialog without taking action
        builder.show();
    }
    private boolean checkLocationServices() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            // Location services are not enabled, show a dialog to enable them
            return false;
        }else {
            return true;
        }
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilityAndConstant.saveString("wrongPin","correctPin");
    }
}