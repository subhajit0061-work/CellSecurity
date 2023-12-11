package com.adretsoftwares.cellsecuritycare;

import android.util.Log;

import com.adretsoftwares.cellsecuritycare.helper.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String text = remoteMessage.getNotification().getBody();
            Log.d("notificatiofdggn", "tile is"+title +"text is "+text);
            //calling method to display notification
            NotificationHelper.displayNotification(getApplicationContext(), title, text);
        }
    }
}
