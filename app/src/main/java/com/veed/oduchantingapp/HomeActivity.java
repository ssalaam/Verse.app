package com.veed.oduchantingapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.marshalchen.ultimaterecyclerview.animators.adapters.AlphaInAnimationAdapter;
import com.marshalchen.ultimaterecyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.veed.oduchantingapp.Adapters.HomeRecyclerViewAdapter;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.DragSelectRecyclerView.DragSelectRecyclerView;
import com.veed.oduchantingapp.DragSelectRecyclerView.DragSelectRecyclerViewAdapter;
import com.veed.oduchantingapp.Utils.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.doorbell.android.Doorbell;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class HomeActivity extends AppCompatActivity   implements
        HomeRecyclerViewAdapter.ClickListener, DragSelectRecyclerViewAdapter.SelectionListener, MaterialCab.Callback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private static final int MY_PERMISSIONS_RECORD_AUDIO= 55;
    private static final int MY_PERMISSIONS_RECEIVE_BOOT_COMPLETED= 85;

    FloatingActionButton floatingActionButton;
    int counter = 0;
    String listmode = "";
    boolean on = false;
    Intent intent;
    Toolbar toolbar;
    HomeRecyclerViewAdapter homeRecyclerViewAdapter;
    DragSelectRecyclerView dragSelectRecyclerView;
    GridLayoutManager gridLayoutManager;
    boolean selectionMode = false;
    Drawer main_drawer;
    Integer[] selected = {};
    Boolean use_dark_theme;

    ProgressBar progressBar;

    SlideInBottomAnimationAdapter slideInBottomAnimationAdapter;
    AlphaInAnimationAdapter alphaInAnimationAdapter;

    final int FEEDBACK = 1;
    final int SUPPORT = 45;
    final int SETTINGS = 2;
    final int ABOUT = 3;

    private GoogleApiClient mGoogleApiClient;

    private MaterialCab mCab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        Utils.notifyUserOfNewContent(this, 0);

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.auto_theme_change_setting), false)) {
            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

            Log.d("TimeOfDay", "In 24 hour format it is " + timeOfDay + " oclock");

            if (!(timeOfDay >= 7 && timeOfDay < 17)) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(getResources().getString(R.string.theme_setting), true);
                editor.commit();
                use_dark_theme = true;
                Snackbar.make(findViewById(R.id.base), "Auto Theme Switching Enabled: Entering NIGHT MODE", Snackbar.LENGTH_LONG).show();
            } else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(getResources().getString(R.string.theme_setting), false);
                editor.commit();
                use_dark_theme = false;
                Snackbar.make(findViewById(R.id.base), "Auto Theme Switching Enabled: Entering DAY MODE", Snackbar.LENGTH_LONG).show();

            }
        }else{
            use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);
        }

        if (!use_dark_theme) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());




        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar);


        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if(MyApplication.isFreeVersion) {
            getSupportActionBar().setTitle("Verse.app (Free)");
        }else{
            getSupportActionBar().setTitle("Verse.app (Pro)");
        }

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View header = inflater.inflate(R.layout.my_material_drawer_header, null, false);
        ImageView back = (ImageView) header.findViewById(R.id.material_drawer_account_header_background);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        header.setPadding(0, getStatusBarHeight(), 0, 0);



        main_drawer = new DrawerBuilder(HomeActivity.this)
                //this layout have to contain child layouts
                .withRootView(R.id.drawer_container)
                .withToolbar(toolbar)
                //.withTranslucentStatusBar(true)
                .withSelectedItem(0)
                //.withStickyHeader(R.layout.drawer_header)
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .withCloseOnClick(true)
                //.withHeader(header)
                .build();

        main_drawer.addItem(new PrimaryDrawerItem().withName("Support").withIcon(MaterialDesignIconic.Icon.gmi_favorite).withIdentifier(SUPPORT));
        main_drawer.addItem(new PrimaryDrawerItem().withName("Feedback").withIcon(MaterialDesignIconic.Icon.gmi_comment_alert).withIdentifier(FEEDBACK));
        main_drawer.addItem(new PrimaryDrawerItem().withName("Settings").withIcon(MaterialDesignIconic.Icon.gmi_settings).withIdentifier(SETTINGS));
        main_drawer.addItem(new PrimaryDrawerItem().withName("About this project").withIcon(MaterialDesignIconic.Icon.gmi_info_outline).withIdentifier(ABOUT));




        main_drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {

                switch ((int) iDrawerItem.getIdentifier()) {
                    case FEEDBACK:
                        new Doorbell(HomeActivity.this, 4710, "Hr6MPeUgSPAqVEgGfi3EPzovIWko2w31CarlfTDILG1wIXyieTEm40j1bMY64KRO").show();
                        main_drawer.deselect();
                        break;
                    case SETTINGS:

                        intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        getWindow().setExitTransition(new Fade());
                        HomeActivity.this.startActivityForResult(intent, 0, ActivityOptionsCompat.
                                makeSceneTransitionAnimation(HomeActivity.this).toBundle());

                        main_drawer.deselect();

                        break;
                    case ABOUT:

                        intent = new Intent(HomeActivity.this, AboutActivity.class);
                        getWindow().setExitTransition(new Fade());
                        HomeActivity.this.startActivityForResult(intent, 0, ActivityOptionsCompat.
                                makeSceneTransitionAnimation(HomeActivity.this).toBundle());

                        main_drawer.deselect();
                        break;
                    case SUPPORT:

                        String url = "https://www.gofundme.com/the-alade-project";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);

                        main_drawer.deselect();
                        break;
                }
                return false;
            }
        });





        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(getAssets(), "fonts/Montserrat-Bold.ttf");
                if(tv.getText().equals(toolbar.getTitle())){
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            new MaterialDialog.Builder(this)
                    .positiveText("OK")
                    .negativeText("CANCEL")
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    })
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {

                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.RECEIVE_BOOT_COMPLETED},
                                    MY_PERMISSIONS_RECORD_AUDIO);

                        }
                    })
                    .content("You need to enable RECORD AUDIO and RECEIVE_BOOT_COMPLETED permission for this app to work properly")
                    .backgroundColor((Color.WHITE))
                    .titleGravity(GravityEnum.START)
                    .titleColor((Color.BLACK))
                    .negativeColor(Color.BLACK)
                    .contentColor((Color.BLACK))
                    .positiveColor((Color.BLACK))
                    .show();


        }else {

        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MyApplication.isFreeVersion) {

                    if(MyApplication.all_chants.size() > 2){

                        new MaterialDialog.Builder(HomeActivity.this)
                                .positiveText("UPGRADE")
                                .negativeText("NO THANKS")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Utils.launchGooglePlay(HomeActivity.this, 1234);
                                    }
                                })
                                .title("Upgrade to Verse.app Pro")
                                .content("Whoops. Looks like you have the free version. If you would like to add more verses you can purchase the Pro version.")
                                .backgroundColor((Color.WHITE))
                                .titleGravity(GravityEnum.START)
                                .titleColor((Color.BLACK))
                                .negativeColor(Color.BLACK)
                                .contentColor((Color.BLACK))
                                .positiveColor((Color.BLACK))
                                .show();

                    }else{

                        Intent intent = new Intent(HomeActivity.this, NewChantActivity.class);
                        getWindow().setExitTransition(new Fade());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(HomeActivity.this);
                        HomeActivity.this.startActivityForResult(intent, 0, options.toBundle());

                    }
                }else{

                    Intent intent = new Intent(HomeActivity.this, NewChantActivity.class);
                    getWindow().setExitTransition(new Fade());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(HomeActivity.this);
                    HomeActivity.this.startActivityForResult(intent, 0, options.toBundle());

                }



                homeRecyclerViewAdapter.clearSelected();
            }
        });




        dragSelectRecyclerView = (DragSelectRecyclerView) findViewById(R.id.scroll);



        homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(dragSelectRecyclerView , DatabaseHandler.getInstance(this).getAllChants(), this, this);

        homeRecyclerViewAdapter.setSelectionListener(this);

        homeRecyclerViewAdapter.restoreInstanceState(savedInstanceState);

        dragSelectRecyclerView.setAdapter(homeRecyclerViewAdapter);


        dragSelectRecyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.list_divider));


        gridLayoutManager = new GridLayoutManager(this, 1);
        /*
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(homeRecyclerViewAdapter.getItemViewType(position)){
                    case HomeRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return gridLayoutManager.getSpanCount();
                    case HomeRecyclerViewAdapter.CHANT_TYPE:
                        return position % 3 == 0 ? 1 : 1;
                    default:
                        return -1;
                }
            }
        });
        */


        listmode = PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.list_mode_setting), Utils.STAGGERED_GRID_MODE);

        dragSelectRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        if(listmode.equals(Utils.LARGE_CARD_MODE)){
          homeRecyclerViewAdapter.notifyDataSetChanged();
            dragSelectRecyclerView.setLayoutManager(gridLayoutManager);
        }else{
            homeRecyclerViewAdapter.notifyDataSetChanged();
            dragSelectRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }





        mCab = MaterialCab.restoreState(savedInstanceState, this, this);

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean( (this.getResources().getString(R.string.show_home_tap_target)) , true)
                || PreferenceManager.getDefaultSharedPreferences(this).getBoolean( this.getResources().getString(R.string.help_setting), false)
                ) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(floatingActionButton)
                    .setPrimaryText("Get Started")
                    .setBackgroundColour((use_dark_theme ? getResources().getColor(R.color.dark_primary) : getResources().getColor(R.color.light_primary)))
                    .setSecondaryText("Tap the plus to start saving verses")
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            //Do something such as storing a value so that this prompt is never shown again
                        }

                        @Override
                        public void onHidePromptComplete() {

                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(getResources().getString(R.string.show_home_tap_target), !PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).getBoolean(getResources().getString(R.string.show_home_tap_target), false));
                            editor.commit();

                        }
                    })
                    .show();
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {



        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECEIVE_BOOT_COMPLETED, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION


                if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED
                       ){
                    // All Permissions Granted


                } else {
                    // Permission Denied
                    new MaterialDialog.Builder(this)
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    finish();
                                }
                            })
                            .dismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {

                                    ActivityCompat.requestPermissions(HomeActivity.this,
                                            new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.RECEIVE_BOOT_COMPLETED},
                                            MY_PERMISSIONS_RECORD_AUDIO);

                                }
                            })
                            .content("You need to enable RECORD AUDIO permission for this app to work properly")
                            .backgroundColor((use_dark_theme ? Color.DKGRAY : Color.WHITE))
                            .titleGravity(GravityEnum.START)
                            .titleColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                            .contentColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                            .positiveColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                            .negativeColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                            .show();

                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        menu.findItem(R.id.action_search).setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_search)
                .color(Color.WHITE)
                .actionBar());

        menu.findItem(R.id.action_switch_theme).setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_invert_colors)
                .color(Color.WHITE)
                .actionBar());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id == android.R.id.home)
        {
            super.onBackPressed();
            return true;
        }

        if (id == R.id.action_search) {

            if(MyApplication.isFreeVersion){
                new MaterialDialog.Builder(this)
                        .positiveText("UPGRADE")
                        .negativeText("NO THANKS")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Utils.launchGooglePlay(HomeActivity.this, 1234);
                            }
                        })
                        .title("Upgrade to Verse.app Pro")
                        .content("Whoops. Looks like you have the free version. If you would like to use this feature you can purchase the Pro version.")
                        .backgroundColor((Color.WHITE))
                        .titleGravity(GravityEnum.START)
                        .titleColor((Color.BLACK))
                        .negativeColor(Color.BLACK)
                        .contentColor((Color.BLACK))
                        .positiveColor((Color.BLACK))
                        .show();
            }else {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivityForResult(intent, 0);
            }
            return true;
        }


        if (id == R.id.action_switch_theme) {

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(getResources().getString(R.string.theme_setting), !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.theme_setting), false));
            editor.commit();

            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }else {
            MyApplication.all_chants = DatabaseHandler.getInstance(this).getAllChants();
            homeRecyclerViewAdapter.chants = MyApplication.all_chants;
            homeRecyclerViewAdapter.notifyDataSetChanged();
        }



       listmode = PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.list_mode_setting), Utils.STAGGERED_GRID_MODE);


        /*
        if(resultCode == RESULT_OK && data.getBooleanExtra("should_refresh", false)) {
            if (listmode.equals(Utils.LARGE_CARD_MODE)) {
                homeRecyclerViewAdapter.notifyDataSetChanged();
                dragSelectRecyclerView.setLayoutManager(gridLayoutManager);
            } else {
                homeRecyclerViewAdapter.notifyDataSetChanged();
                dragSelectRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            }
        }
        */
    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }
    */

    /**
     * Handles resolution callbacks.
     */


    static String TAG = "GOogle Drive Api";
    static int REQUEST_CODE_RESOLUTION = 99;

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "GoogleApiClient connected");



        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean( (this.getResources().getString(R.string.is_app_folder_created)) , false) == false) {

            Log.d("onConnected", "is_app_folder_created = false;");

            ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
                    ResultCallback<DriveFolder.DriveFolderResult>() {
                        @Override
                        public void onResult(DriveFolder.DriveFolderResult result) {
                            if (!result.getStatus().isSuccess()) {
                                showMessage("Error while trying to create the folder");

                                //Drive.DriveApi.fetchDriveId(getGoogleApiClient(), Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId().encodeToString())
                                //.setResultCallback(idCallback);

                                return;

                            }else {


                                Log.d("onConnected", "folder created");

                                showMessage("Created a folder: " + result.getDriveFolder().getDriveId());

                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean(getResources().getString(R.string.is_app_folder_created), true);

                                editor.putString(getResources().getString(R.string.app_folder_drive_id), result.getDriveFolder().getDriveId().encodeToString());

                                editor.commit();
                            }
                        }
                    };


            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle("Versus Export Data created at " + System.currentTimeMillis()).build();
            Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                    getGoogleApiClient(), changeSet).setResultCallback(folderCreatedCallback);
        }

    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // Save selected indices to be restored after recreation
        homeRecyclerViewAdapter.saveInstanceState(outState);
        if (mCab != null) mCab.saveState(outState);
    }

    @Override
    public void onClick(int index, View v) {


        if(selectionMode) {
            homeRecyclerViewAdapter.toggleSelected(index);
        }else {
            MyApplication.current_chant = homeRecyclerViewAdapter.chants.get(index - 1);
            Intent intent = new Intent(this, ChantCoverActivity.class);
            getWindow().setExitTransition(new Fade());
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this);
            startActivityForResult(intent, 0 );
        }

        /*
        if(mCab != null && mCab.isActive()) {

        }else{

        }
        */

    }


    @Override
    public void onLongClick(int index) {

        dragSelectRecyclerView.setDragSelectActive(true, index);

        selectionMode = true;

        Log.d("dragSelectRecyclerView", "onLongClick");

    }

    @Override
    public void onDragSelectionChanged(int count) {

        Log.d("Material Cab", "onDragSelectionChanged");

        if (count > 0) {
            if (mCab == null) {

                TypedValue popupTheme = new TypedValue();
                getTheme().resolveAttribute(R.attr.mytoolbarPopUpStyle, popupTheme, true);

                TypedValue backgroundColor = new TypedValue();
                getTheme().resolveAttribute(R.attr.myPrimaryColor, backgroundColor, true);

                Log.d("Material Cab", "start");


                mCab = new MaterialCab(this, R.id.cab_stub)
                        .setMenu(R.menu.cab)
                        .setPopupMenuTheme(popupTheme.resourceId)
                        .setCloseDrawableRes(R.drawable.ic_close)
                        .setBackgroundColorRes(backgroundColor.resourceId)
                        .start(this);

                mCab.getMenu().findItem(R.id.trash).setIcon(new IconicsDrawable(this)
                        .icon(MaterialDesignIconic.Icon.gmi_delete)
                        .color(Color.WHITE)
                        .actionBar());

            }
            mCab.setTitle(count + "");
        } else if (mCab != null && mCab.isActive()) {
            mCab.reset().finish();
            selectionMode = false;
            Log.d("Material Cab", "finish");
            mCab = null;
        }
    }

    // Material CAB Callbacks

    @Override
    public boolean onCabCreated(MaterialCab cab, Menu menu) {
        return true;
    }

    @Override
    public boolean onCabItemClicked(MenuItem item) {
        if (item.getItemId() == R.id.trash) {


            selected = homeRecyclerViewAdapter.getSelectedIndices();

            StringBuilder sb = new StringBuilder();
            int traverse = 0;
            for (Integer index : homeRecyclerViewAdapter.getSelectedIndices()) {
                if (traverse > 0) sb.append(", ");
                sb.append(homeRecyclerViewAdapter.chants.get(index - 1).name);
                traverse++;
            }

            Toast.makeText(this,
                    String.format("Selected letters (%d): %s", homeRecyclerViewAdapter.getSelectedCount(), sb.toString()),
                    Toast.LENGTH_LONG);

            new MaterialDialog.Builder(this)
                    .positiveText("DELETE")
                    .negativeText("CANCEL")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // TODO
                            Log.d("Deleting", "Dialog dismissed " + selected.length + " selected");

                            progressBar.setVisibility(View.VISIBLE);
                            dragSelectRecyclerView.setVisibility(View.GONE);

                            for (int i = 0; i < selected.length; i++) {
                                Log.d("Deleting", homeRecyclerViewAdapter.chants.get(selected[i]-1).name);
                                DatabaseHandler.getInstance(HomeActivity.this).deleteChant(homeRecyclerViewAdapter.chants.get(selected[i] - 1));
                                MyApplication.all_chants = DatabaseHandler.getInstance(HomeActivity.this).getAllChants();
                                homeRecyclerViewAdapter.chants = MyApplication.all_chants = DatabaseHandler.getInstance(HomeActivity.this).getAllChants();
                                homeRecyclerViewAdapter.notifyDataSetChanged();
                                //homeRecyclerViewAdapter.notifyItemRemoved(selected[i]-2);

                            }

                            if(homeRecyclerViewAdapter.chants.size() < 1) {
                                dragSelectRecyclerView.showEmptyView();
                            }else{
                                dragSelectRecyclerView.hideEmptyView();
                            }

                            Snackbar.make(findViewById(R.id.base), "Deleting...", Snackbar.LENGTH_LONG);
                            homeRecyclerViewAdapter.clearSelected();

                            final Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    dragSelectRecyclerView.setVisibility(View.VISIBLE);
                                }
                            }, 2000);
                        }
                    })
                    .content("Are you sure you want to delete these items?")
                    .backgroundColor((Color.WHITE))
                    .titleGravity(GravityEnum.START)
                    .titleColor((Color.BLACK))
                    .negativeColor(Color.BLACK)
                    .contentColor((Color.BLACK))
                    .positiveColor((Color.BLACK))
                    .show();




        }
        return true;
    }



    @Override
    public void onBackPressed() {
        if (homeRecyclerViewAdapter.getSelectedCount() > 0)
            homeRecyclerViewAdapter.clearSelected();
        else super.onBackPressed();
    }

    @Override
    public boolean onCabFinished(MaterialCab cab) {
        homeRecyclerViewAdapter.clearSelected();
        return true;
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }



    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupWindowAnimations() {
        if(supportsViewElevation()) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
        }
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

}
