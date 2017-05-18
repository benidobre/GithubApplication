package com.example.beni.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by beni on 5/9/2017.
 */

public class AirplaneModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                if(!intent.getBooleanExtra("state",false)){
                    Intent view = new Intent(context, ProfileActivity.class);
                    PendingIntent pendingView = PendingIntent.getActivity(context,0,view,PendingIntent.FLAG_UPDATE_CURRENT);
//                   Notification notification = new NotificationCompat.Builder(context)
                }

        }
    }
}
