package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.adretsoftwares.cellsecuritycare.databinding.ActivitySplashBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences preferences;
    Context context;
    private static final String FIRST_LAUNCH_DATE_KEY = "firstLaunchDate";
    private static final int MAX_ALLOWED_DAYS = 5;
    private boolean flag = false;

    private ActivitySplashBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        context = this;
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(binding.getRoot());

        binding.imageView3.setOnClickListener(view -> {
            checkOverlayPermission();
        });

        String firstLaunchDateStr = preferences.getString(FIRST_LAUNCH_DATE_KEY, null);

        if (firstLaunchDateStr == null) {
            // First launch, store the current date
            String currentDate = getCurrentDate();
            preferences.edit().putString(FIRST_LAUNCH_DATE_KEY, currentDate).apply();
        } else {
            // App has been launched before, check the number of days
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date firstLaunchDate = dateFormat.parse(firstLaunchDateStr);
                Date currentDate = dateFormat.parse(getCurrentDate());

                long diffMillis = currentDate.getTime() - firstLaunchDate.getTime();
                long diffDays = diffMillis / (24 * 60 * 60 * 1000);

//                if (diffDays > MAX_ALLOWED_DAYS) {
//                    // More than 5 days have passed, restrict the app
//                    Toast.makeText(this, "Notifications service expired.", Toast.LENGTH_SHORT).show();
//                    finish(); // Close the app
//                }else {
                requestPermissions();
                //}
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
//        Dexter.withActivity(this)
//                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.FOREGROUND_SERVICE,
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.POST_NOTIFICATIONS,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                        Manifest.permission.SYSTEM_ALERT_WINDOW,
//                        Manifest.permission.READ_MEDIA_IMAGES
//                ).withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        if (report.areAllPermissionsGranted()) {
//                            flag = true;
//                            Toast.makeText(context, "All the permissions are granted...", Toast.LENGTH_LONG).show();
//                        }
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            flag = false;
//                            Toast.makeText(context, "All the permissions are Denied...", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                });

    }


    private void requestPermissions() {
        String[] permissions = null;
//      //  Toast.makeText(context,String.valueOf(Build.VERSION.SDK_INT),Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT > 30) {
            permissions = new String[]{Manifest.permission.CAMERA,
                    // below is the list of permissions
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.MODIFY_PHONE_STATE};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA,
                    // below is the list of permissions
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS};
        }

        // Check if permissions are granted
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            // All permissions are granted, proceed with your app
            flag = true;
            // Check for overlay permission
            checkOverlayPermission();
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Allow draw over the application permission", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            if (preferences.getBoolean("issLoggedIn", false)) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
            // Permissions are granted, proceed with your app
            return true;
        }
        return false;
    }


//    private void requestPermissions() {
//        String[] permissions = null;
//      //  Toast.makeText(context,String.valueOf(Build.VERSION.SDK_INT),Toast.LENGTH_LONG).show();
//                if(Build.VERSION.SDK_INT>30){
//                    permissions = new String[]{Manifest.permission.CAMERA,
//                            // below is the list of permissions
//                            Manifest.permission.POST_NOTIFICATIONS,
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.SEND_SMS,
//                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                            Manifest.permission.READ_MEDIA_IMAGES};
//                }else{
//                    permissions = new String[]{Manifest.permission.CAMERA,
//                            // below is the list of permissions
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.SEND_SMS,
//                            Manifest.permission.ACCESS_BACKGROUND_LOCATION};
//                }
//        // below line is use to request permission in the current activity.
//        // this method is use to handle error in runtime permissions
//        Dexter.withActivity(this)
//                // below line is use to request the number of permissions which are required in our app.
//                .withPermissions(permissions)
//                // after adding permissions we are calling an with listener method.
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                        // this method is called when all permissions are granted
//                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                            // do you work now
//                         //   Toast.makeText(SplashActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
//                            flag = true;
//                            if (!Settings.canDrawOverlays(SplashActivity.this)) {
//                                Toast.makeText(SplashActivity.this, "Allow draw over the application permission ", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                                        Uri.parse("package:" + getPackageName()));
//                                someActivityResultLauncher.launch(intent);
//                            }else{
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (flag) {
//                                            if (preferences.getBoolean("issLoggedIn", false)) {
//                                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                                                finish();
//                                            } else {
//                                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                                                finish();
//                                            }
//                                        }
//                                    }
//                                },1000);
//                            }
//                        }
//                        // check for permanent denial of any permission
//                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                            // permission is denied permanently, we will show user a dialog message.
//                            for(int i = 0;i<multiplePermissionsReport.getDeniedPermissionResponses().size();i++){
//                                Toast.makeText(context,multiplePermissionsReport.getDeniedPermissionResponses().get(i).getPermissionName(),Toast.LENGTH_LONG).show();
//                            }
//
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                        // this method is called when user grants some permission and denies some of them.
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).withErrorListener(error -> {
//                    // we are displaying a toast message for error message.
//                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
//                })
//                // below line is use to run the permissions on same thread and to check the permissions
//                .check();
//    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (flag) {
                            if (preferences.getBoolean("issLoggedIn", false)) {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }
                }
            });


    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to run without issue. Kindly grant all permissions from app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
            finish();
        });
        // below line is used to display our dialog
        builder.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(new Date());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Permissions granted, check overlay permission
                checkOverlayPermission();
            }
//            } else {
//                // Permissions denied, show a message or handle as needed
////                showPermissionDeniedDialog();
//            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Some permissions have been denied. Please grant all permissions in the app settings.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
}