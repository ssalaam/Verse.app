package com.veed.oduchantingapp;

import android.app.Application;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pixplicity.easyprefs.library.Prefs;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.Utils.MyFile;
import com.veed.oduchantingapp.Utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Saboor Salaam on 6/5/2015.
 */
public class MyApplication extends Application {


    public static Chant current_chant = new Chant();
    public static MyFile current_file = new MyFile();
    public static List<Chant> all_chants = new ArrayList<>();
    public static String add_line = "";
    public static boolean isFreeVersion = false;
    public static GoogleApiClient mGoogleApiClient = null;
    public static final String SHARED_PREFS = "ese_prefs";


    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();


        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean( (this.getResources().getString(R.string.is_first_run)) , false)) {

            Utils.setNotificationAlarm(getApplicationContext());

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(getResources().getString(R.string.is_first_run), false);
            editor.commit();
        }
        }

    }
