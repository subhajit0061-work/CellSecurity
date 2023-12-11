package com.adretsoftwares.cellsecuritycare.helper;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.CHANNEL_ID;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.adretsoftwares.cellsecuritycare.MainActivity;
import com.adretsoftwares.cellsecuritycare.R;

public class NotificationHelper {
    public static void displayNotification(Context context, String title, String text) {
        Log.d("notificationdetauls","title is :"+title +"text" +text);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.applogo);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationCompat.notify(1, mBuilder.build());
    }
}
