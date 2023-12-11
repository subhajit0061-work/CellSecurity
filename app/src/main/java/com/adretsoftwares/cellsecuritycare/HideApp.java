package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HideApp extends AppCompatActivity {
    private boolean isAdminActive =false;
    private ComponentName compName;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private ListView appList;
    private List<String> appNames;
    SharedPreferences preferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_app);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        compName = new ComponentName(this, loginWatch.class);
        if(!preferences.contains("admin")&&!preferences.getBoolean("admin",false)){
            Toast.makeText(this,"Required admin privilege to use this feature",Toast.LENGTH_LONG).show();
            finish();
        }
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, HideApp.class);
        appList = findViewById(R.id.appList);
        appNames = new ArrayList<>();

        // Populate the appNames list with installed app names
        populateAppList();

        // Set up the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appNames);
        appList.setAdapter(adapter);

        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected app package name
                String packageName = getPackageName(position);
                Log.d("package ",packageName);
                if (packageName != null) {
                    // Check if the app is currently hidden
                    boolean isHidden = devicePolicyManager.isApplicationHidden(componentName, packageName);

                    // Toggle the hidden status of the app
                    devicePolicyManager.setApplicationHidden(componentName, packageName, !isHidden);

                    // Refresh the app list
                    populateAppList();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    private String getPackageName(int position) {
        if (position >= 0 && position < appNames.size()) {
            return appNames.get(position);
        }
        return null;
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    private void populateAppList() {
        appNames.clear();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // Filter out system apps
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appNames.add(packageInfo.packageName);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}