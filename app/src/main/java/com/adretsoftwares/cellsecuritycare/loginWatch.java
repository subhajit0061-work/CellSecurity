package com.adretsoftwares.cellsecuritycare;

/**
 * @author Sanat
 * loginWatch class, handle enabling and disabling of security Policies
 * by the user
 */
import static android.content.Context.MODE_PRIVATE;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class loginWatch extends DeviceAdminReceiver {
    SharedPreferences sharedPreferences;
    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("admin").commit();
    }

    @Override
    public void onEnabled(Context ctxt, Intent intent) {
        ComponentName cn = new ComponentName(ctxt, loginWatch.class);
        DevicePolicyManager mgr =
                (DevicePolicyManager) ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        sharedPreferences = ctxt.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("admin",true).commit();
//        mgr.setPasswordQuality(cn,
//                DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);

        onPasswordChanged(ctxt, intent);
    }

    @Override
    public void onPasswordChanged(Context ctxt, Intent intent) {
        DevicePolicyManager mgr =
                (DevicePolicyManager) ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        String  msgId;
        SharedPreferences preferences = ctxt.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
//        if (mgr.isActivePasswordSufficient()) {
//            msgId = "Your password is compliant!";
//        } else {
//            msgId = "Your password is not compliant! Submit to my authority!";
//        }

    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {
        Toast.makeText(ctxt, R.string.password_failed, Toast.LENGTH_LONG)
                .show();
        SharedPreferences preferences = ctxt.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String message =  "A Wrong PIN Was Entered On Your Phone";
        String mobile = preferences.getString("mobile_number","");

        Intent front_translucent = new Intent(ctxt
                .getApplicationContext(), CameraService.class);
        front_translucent.putExtra("Front_Request", true);
        front_translucent.putExtra("message", "A Wrong PIN Was Entered On Your Phone");
        front_translucent.putExtra("mobile", preferences.getString("mobile_number",""));
        front_translucent.putExtra("Quality_Mode",
                20);
        ctxt.getApplicationContext().startService(
                front_translucent);


//        if(preferences.getString("emergencyMobile1","")!=null&&preferences.getBoolean("is1Enable",false)){
//            NotifyandSave.Companion.LoginRegister(ctxt,mobile,message);
//            NotifyandSave.Companion.LoginRegister(ctxt,preferences.getString("emergencyMobile1",""),message);
//        }
//
//        if(preferences.getString("emergencyMobile2","")!=null&&preferences.getBoolean("is2Enable",false)){
//            NotifyandSave.Companion.LoginRegister(ctxt,preferences.getString("emergencyMobile2",""),message);
//        }
//
//        if(preferences.getString("emergencyMobile3","")!=null&&preferences.getBoolean("is3Enable",false)){
//            NotifyandSave.Companion.LoginRegister(ctxt,preferences.getString("emergencyMobile3",""),message);
//        }
//        Intent i = new Intent(ctxt,PushService.class);
//        i.putExtra("number",mobile);
//        i.putExtra("message",message);
//        i.putExtra("coordinates","389.09");
//        ctxt.startService(i);
    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        Toast.makeText(ctxt, R.string.password_success, Toast.LENGTH_LONG)
                .show();

       // Toast.makeText(ctxt, msgId, Toast.LENGTH_LONG).show();
    }
}
