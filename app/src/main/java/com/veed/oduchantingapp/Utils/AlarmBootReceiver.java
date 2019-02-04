package com.veed.oduchantingapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.veed.oduchantingapp.R;


/**
 * Created by Saboor Salaam on 10/31/2015.
 */
public class AlarmBootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.receive_notifications_switch), true)) {
                    Utils.setNotificationAlarm(context);
                }
            }
        }
    }
