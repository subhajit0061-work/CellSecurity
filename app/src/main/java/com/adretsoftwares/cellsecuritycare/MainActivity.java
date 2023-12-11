package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.adretsoftwares.cellsecuritycare.services.AppForegroundService;
import com.adretsoftwares.cellsecuritycare.util.LocationService;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_DEVICE_ADMIN = 1;


    private DevicePolicyManager devicePolicyManager, devicePolicyManager1;
    private ComponentName deviceAdminComponent;
    ImageView pocketThiefMode, emergencyMode, powerOffMode, avatar;
    ImageView stopDataTransfer, simChangeAlert, wrongPinPass1, settings, referrals, trackMobile, referClick;
    ImageView logOut;
    TextView userName;
    LinearLayout emegnecySOS, batteryLayout, dontTouch;
    static SharedPreferences sharedPreferences;
    SwitchMaterial num1_switch;
    TextView textView2;

    // myy code start

    private static Intent service;


    // end


    CardView cardView, format, subcription, apphide, applock;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBatteryLowReceiver();
        startForegroundService();
        View decorView = getWindow().getDecorView();
        num1_switch = findViewById(R.id.num1_switch);
        textView2 = findViewById(R.id.textView2);
        askPermissions(this);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        cardView = findViewById(R.id.contact);
        emegnecySOS = findViewById(R.id.emegnecySOS);
        batteryLayout = findViewById(R.id.batteryLayout);
        dontTouch = findViewById(R.id.dontTouch);
        apphide = findViewById(R.id.apphide);
        applock = findViewById(R.id.applock);
        format = findViewById(R.id.format);
        subcription = findViewById(R.id.subcription);
        service = new Intent(this, LocationService.class);

        subcription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Subscription.class);
                startActivity(i);
            }
        });
        applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, HideApp.class);
//                startActivity(i);
            }
        });
        apphide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, HideApp.class);
//                startActivity(i);
            }
        });
        format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, FormatMobile.class);
                startActivity(i);
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ContactBackup.class);
                startActivity(i);
            }
        });
        emegnecySOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SOS.class);
                startActivity(i);
            }
        });
        dontTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PocketThiefActivity.class);
                startActivity(i);
            }
        });
        batteryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BatteryIndicator.class);
                startActivity(i);

                askPermissions(view.getContext());
            }
        });
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        logOut = findViewById(R.id.logOut);
        userName = findViewById(R.id.userName);
        referClick = findViewById(R.id.referClick);
        pocketThiefMode = findViewById(R.id.pocketThiefMode);
        trackMobile = findViewById(R.id.trackMobile);
        emergencyMode = findViewById(R.id.emergencyMode);
        powerOffMode = findViewById(R.id.powerOffMode);
        avatar = findViewById(R.id.avatar);
        stopDataTransfer = findViewById(R.id.stopDataTransfer);
        simChangeAlert = findViewById(R.id.simChangeAlert);
        wrongPinPass1 = findViewById(R.id.wrongPinPass1);
        settings = findViewById(R.id.settings);
        referrals = findViewById(R.id.referrals);


        byte[] imageData = loadImage(this);

        if (imageData != null) {
            // Convert the byte array to a Bitmap
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            // Set the Bitmap to the ImageView
            if (imageBitmap != null) {
                avatar.setImageBitmap(imageBitmap);
            }
        } else {
            // Handle the case where no image data is found in SharedPreferences
            // You can set a default image or display an error message.
        }
        if (!SecurityService.serviceRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(new Intent( MainActivity.this, SecurityService.class));
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription(CHANNEL_DESC);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
        userName.setText(sharedPreferences.getString("name", ""));
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Title")
                        .setMessage("Do you really want to logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear().apply();
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        devicePolicyManager1 = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponentName = new ComponentName(this, loginWatch.class);

        boolean isAdminActive = devicePolicyManager1.isAdminActive(adminComponentName);
        if (isAdminActive) {
            num1_switch.setChecked(true);
            textView2.setTextColor(Color.parseColor("#4CAF50"));
            textView2.setText("Admin Activated");
        } else {
            textView2.setTextColor(Color.parseColor("#000000"));
            textView2.setText("Activate Admin");
            num1_switch.setChecked(false);
        }
        referClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Reference.class);
                startActivity(i);
            }
        });

        pocketThiefMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationServices()) {
//                startActivity(new Intent(MainActivity.this, PocketThiefActivity.class));
                    Intent i = new Intent(MainActivity.this, WrongPinPassPatternMode.class);
                    i.putExtra("flag", "on");
                    startActivity(i);
                } else {
                    showLocationServicesDialog();
                }

//                emergencyMode.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        PushNotification pushNotification = new PushNotification();
//                        pushNotification.push("7304828262","hello",view.getContext());
//                        //startActivity(new Intent(MainActivity.this,EmergencyMode.class));
//                    }
//                });
            }
        });


        trackMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationServices()) {
                    startActivity(new Intent(MainActivity.this, MobileTracking.class));
                } else {
                    showLocationServicesDialog();
                }
            }
        });
        powerOffMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PowerOffProtectionActivity.class));
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        stopDataTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StopUsbConnectionActivity.class));
            }
        });

        simChangeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SimChangeActivity.class));
            }
        });

        wrongPinPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationServices()) {
                    Intent i = new Intent(MainActivity.this, WrongPinPassPatternMode.class);
                    i.putExtra("flag", "off");
                    startActivity(i);
                } else {
                    showLocationServicesDialog();
                }

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
        referrals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReferralActivity.class));
            }
        });

        num1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    deviceAdminComponent = new ComponentName(MainActivity.this, loginWatch.class);

                    if (!devicePolicyManager.isAdminActive(deviceAdminComponent)) {
                        // Request device admin activation
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponent);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin for app");
                        startActivityForResult(intent, REQUEST_ENABLE_DEVICE_ADMIN);
                        num1_switch.setChecked(true);
                        textView2.setTextColor(Color.parseColor("#4CAF50"));
                        textView2.setText("Admin Activated");
                    }
                } else {
                    devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    deviceAdminComponent = new ComponentName(MainActivity.this, loginWatch.class);

                    if (devicePolicyManager.isAdminActive(adminComponentName)) {
                        devicePolicyManager.removeActiveAdmin(adminComponentName);
                    }
                    textView2.setTextColor(Color.parseColor("#000000"));
                    textView2.setText("Activate Admin");
                    num1_switch.setChecked(false);
                    sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("admin").commit();
                }
            }
        });
    }

    public static byte[] loadImage(Context context) {

        String encodedImage = sharedPreferences.getString("IMAGE_KEY", null);

        if (encodedImage != null) {
            // Convert Base64-encoded string back to byte array
            return Base64.decode(encodedImage, Base64.DEFAULT);
        }

        return null;
    }

    private boolean checkLocationServices() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            // Location services are not enabled, show a dialog to enable them
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        byte[] imageData = loadImage(this);

        if (imageData != null) {
            // Convert the byte array to a Bitmap
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            // Set the Bitmap to the ImageView
            if (imageBitmap != null) {
                avatar.setImageBitmap(imageBitmap);
            }
        } else {
            // Handle the case where no image data is found in SharedPreferences
            // You can set a default image or display an error message.
        }


    }


    // Handle permission request result in onRequestPermissionsResult() method

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

    private void askPermissions(Context context) {
        final int STORAGE_PERMISSION_REQUEST_CODE = 1;

        int permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // we already asked for permisson & Permission granted, call camera intent
        if (permissionCheckStorage == PackageManager.PERMISSION_GRANTED) {

            //do what you want
            Toast.makeText(this, "Already granted", Toast.LENGTH_LONG).show();

        } else {


            // Show permission request popup for the first time
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ANSWER_PHONE_CALLS},
                    STORAGE_PERMISSION_REQUEST_CODE);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // check whether storage permission granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Already granted", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_DEVICE_ADMIN) {
            if (resultCode == RESULT_OK) {

                // Device admin activation successful
                // You can perform device admin tasks here
            } else {
                // Device admin activation failed
            }
        }
    }

    private void registerBatteryLowReceiver() {
        registerReceiver(new BatteryReceiver(), new IntentFilter(Intent.ACTION_BATTERY_LOW));
    }

    private void startForegroundService() {
        ContextCompat.startForegroundService(this, new Intent(this, AppForegroundService.class));
    }
}