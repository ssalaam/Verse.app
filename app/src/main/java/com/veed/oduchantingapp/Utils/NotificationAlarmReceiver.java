package com.veed.oduchantingapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Saboor Salaam on 10/31/2015.
 */
public class NotificationAlarmReceiver extends  BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NotificationReceiver", "Alarm Received!!!");
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if((timeOfDay >= 10  && timeOfDay < 23)) {
            Log.d("TimeOfDay", "Right time of day for notification");
            Utils.notifyUserOfNewContent(context, 0);
        }else{
            Log.d("TimeOfDay", "Wrong time of day for notification");
        }
    }
}
