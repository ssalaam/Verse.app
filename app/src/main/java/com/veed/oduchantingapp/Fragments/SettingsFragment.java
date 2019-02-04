package com.veed.oduchantingapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.veed.oduchantingapp.AboutActivity;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.HomeActivity;
import com.veed.oduchantingapp.LibrariesActivity;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.SettingsActivity;
import com.veed.oduchantingapp.Utils.Utils;

import io.doorbell.android.Doorbell;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Saboor Salaam on 10/26/2015.
 */
public class SettingsFragment extends PreferenceFragment implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    Activity context;
    GoogleApiClient mGoogleApiClient;
    static int REQUEST_CODE_RESOLUTION = 99;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = getActivity();

        addPreferencesFromResource(R.xml.preferences);


        findPreference(getResources().getString(R.string.google_drive)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new MaterialDialog.Builder(context)
                        .positiveText("SWITCH")
                        .negativeText("CANCEL")
                        .icon(context.getDrawable(
                                R.drawable.drive_icon
                        ))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                /*
                                if (mGoogleApiClient == null) {
                                    mGoogleApiClient = new GoogleApiClient.Builder(context)
                                            .addApi(Drive.API)
                                            .addScope(Drive.SCOPE_FILE)
                                            .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                                            .addConnectionCallbacks(SettingsFragment.this)
                                            .addOnConnectionFailedListener(SettingsFragment.this)
                                            .build();
                                }
                                mGoogleApiClient.connect();

                                mGoogleApiClient.clearDefaultAccountAndReconnect();
                                */


                            }
                        })
                        .limitIconToDefaultSize()
                        .title("Switch Google Drive Accounts")
                        .content("Sign into a new Google Drive account and begin backing your files up in the cloud")
                        .backgroundColor((Color.WHITE))
                        .titleGravity(GravityEnum.START)
                        .titleColor((Color.BLACK))
                        .negativeColor(Color.BLACK)
                        .contentColor((Color.BLACK))
                        .positiveColor((Color.RED));
                        //.show();


                return false;
            }
        });

        findPreference(getResources().getString(R.string.theme_setting)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean use_dark_theme = !PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getResources().getString(R.string.theme_setting), false); //the setting gets changed when we click on it
                new MaterialDialog.Builder(getActivity())
                        .title("Restart required")
                        .positiveText("RESTART")
                        .negativeText("CANCEL")
                        .content("Restarting the app is required to  apply some settings, do you want to do it now?")
                        .backgroundColor((use_dark_theme ? Color.DKGRAY : Color.WHITE))
                        .titleGravity(GravityEnum.START)
                        .titleColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .contentColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .positiveColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .negativeColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                super.onPositive(dialog);
                            }
                        })
                        .show();

                return false;
            }
        });

        findPreference(getResources().getString(R.string.list_mode_setting)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), "clicked list mode", Toast.LENGTH_SHORT).show();
                ((SettingsActivity) getActivity()).should_refresh_home_activity = true;
                boolean use_dark_theme = !PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getResources().getString(R.string.theme_setting), false); //the setting gets changed when we click on it

                new MaterialDialog.Builder(getActivity())
                        .title("Restart required")
                        .positiveText("OK")
                        .content("These settings will take effect once the app restarts.")
                        .backgroundColor((use_dark_theme ? Color.DKGRAY : Color.WHITE))
                        .titleGravity(GravityEnum.START)
                        .titleColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .contentColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .positiveColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .negativeColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .show();

                return false;
            }
        });


        findPreference(getResources().getString(R.string.open_source_libraries)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(context, LibrariesActivity.class);
                context.getWindow().setExitTransition(new Fade());
                context.startActivityForResult(intent, 0, ActivityOptionsCompat.
                        makeSceneTransitionAnimation(context).toBundle());

                return false;
            }
        });


/*

        findPreference(getResources().getString(R.string.receive_notifications_switch)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (((SwitchPreference) preference).isChecked()) {
                    Utils.stopNotificationAlarm(context);
                    Utils.setNotificationAlarm(getActivity());
                    Toast.makeText(getActivity(),
                            "You will now receive notifications from Veed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Utils.stopNotificationAlarm(context);
                    Toast.makeText(getActivity(),
                            "You will no longer receive notifications from Veed",
                            Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        findPreference(getResources().getString(R.string.feedback)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Doorbell doorbellDialog = new Doorbell(context, Utils.DOORBELL_APP_ID, Utils.DOORBELL_API_KEY); // Create the Doorbell object
                // Callback for when the dialog is shown
                doorbellDialog.setPoweredByVisibility(View.GONE);
                doorbellDialog.setMessageHint("What would you like to send?");
                doorbellDialog.show();
                return false;
            }
        });

        findPreference(getResources().getString(R.string.list_mode_setting)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), "clicked list mode", Toast.LENGTH_SHORT).show();
                ((SettingsActivity) getActivity()).should_refresh_home_activity = true;
                return false;
            }
        });

        findPreference(getResources().getString(R.string.theme_setting)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean use_dark_theme = !PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getResources().getString(R.string.theme_setting), false); //the setting gets changed when we click on it
                new MaterialDialog.Builder(getActivity())
                        .title("Restart required")
                        .positiveText("RESTART")
                        .negativeText("CANCEL")
                        .content("Restarting the app is required to  apply some settings, do you want to do it now?")
                        .backgroundColor((use_dark_theme ? Color.DKGRAY : Color.WHITE))
                        .titleGravity(GravityEnum.START)
                        .titleColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .contentColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .positiveColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .negativeColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                        .typeface(Typeface.createFromAsset(getActivity().getAssets(),
                                "fonts/Lato-Semibold.ttf"), Typeface.createFromAsset(getActivity().getAssets(),
                                "fonts/Lato-Regular.ttf"))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                super.onPositive(dialog);
                            }
                        })
                        .show();

                return false;
            }
        });
        */

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        //Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(context, connectionResult.getErrorCode(), 0).show();
            return;
        }
        try {
            connectionResult.startResolutionForResult(context, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            //Log.e(TAG, "Exception while starting resolution activity", e);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }else {

            Snackbar.make(getView(), "Authorizing...", Snackbar.LENGTH_LONG).show();
        }

    }
}
