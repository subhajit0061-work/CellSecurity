package com.adretsoftwares.cellsecuritycare;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 */

public class PhoneUnlockedReceiver extends BroadcastReceiver {

    interface AfterUnlocked {
        void stopAlarmHere();
    }

    AfterUnlocked afterUnlocked;

    public void actionAfterUnlock(AfterUnlocked afterUnlocked){
        this.afterUnlocked = afterUnlocked;
        afterUnlocked.stopAlarmHere();
    }

    public static final String TAG = "phoneUnlockedReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Log.d(TAG, "Keyguard unlocked ");
            actionAfterUnlock(afterUnlocked);
        }
    }


}
