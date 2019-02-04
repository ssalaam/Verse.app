package com.veed.oduchantingapp;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.processbutton.ProcessButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.Utils.MyFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.veed.oduchantingapp.HomeActivity.REQUEST_CODE_RESOLUTION;

public class UploadActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    Chant mChant = new Chant();
    GoogleApiClient mGoogleApiClient = null;
    static String TAG = "GOogle Drive Api";
    boolean isConnected = false;
    Toolbar toolbar;
    MaterialEditText title;
    int files_uploaded = 0;
    List<FileUploadWrapper> fileUploadWrappers = new ArrayList<>();
    boolean isUploading = false;

    FloatingActionButton floatingActionButton;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        boolean use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);

        if (!use_dark_theme) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_upload);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (MaterialEditText) findViewById(R.id.chantTitle);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Push to Google Drive");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        title.setText(MyApplication.current_chant.name);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingActionButton.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_cloud_upload)
                .color(Color.WHITE)
                .actionBar());

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean( (this.getResources().getString(R.string.show_cloud_tap_target)) , true)
                || PreferenceManager.getDefaultSharedPreferences(this).getBoolean( this.getResources().getString(R.string.help_setting), false)
                ) {


            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(floatingActionButton)
                    .setPrimaryText("Upload To Google Drive")
                    .setBackgroundColour((use_dark_theme ? getResources().getColor(R.color.dark_primary) : getResources().getColor(R.color.light_primary)))
                    .setSecondaryText("Tap the upload this verse to your Google Drive account")
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            //Do something such as storing a value so that this prompt is never shown again
                        }

                        @Override
                        public void onHidePromptComplete() {
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(UploadActivity.this);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(getResources().getString(R.string.show_cloud_tap_target), !PreferenceManager.getDefaultSharedPreferences(UploadActivity.this).getBoolean(getResources().getString(R.string.show_cloud_tap_target), false));
                            editor.commit();
                        }
                    })
                    .show();
        }



        for( int i = 0; i < MyApplication.current_chant.files.size(); i++ ){
            fileUploadWrappers.add(new FileUploadWrapper( MyApplication.current_chant.files.get(i), false));
        }




        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isUploading){
                    return;
                }

                Log.d("Uploading", "is connected = " + isConnected);

                if(!isUploading && isConnected && title.getText().toString().length() > 2 && title.getText().toString().length() < 30) {

                    isUploading = true;

                    floatingActionButton.setIndeterminate(false);
                    floatingActionButton.setProgress(5, true);

                    title.setEnabled(false);
                    //floatingActionButton.setEnabled(false);

                    final Snackbar upload_status_snack = Snackbar.make(findViewById(R.id.base), "Uploaded file " + files_uploaded + " of " +  MyApplication.current_chant.files.size(), Snackbar.LENGTH_INDEFINITE);
                    upload_status_snack.show();

                    //New folder has been created
                    ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
                            ResultCallback<DriveFolder.DriveFolderResult>() {
                                @Override
                                public void onResult(final DriveFolder.DriveFolderResult folderResult) {

                                    if (!folderResult.getStatus().isSuccess()) {
                                        showMessage("Error while trying to create the folder");

                                        return;

                                    } else {

                                        floatingActionButton.setProgress(10, true);

                                        //Each time a file is uploaded this gets called
                                        final  ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
                                                ResultCallback<DriveFolder.DriveFileResult>() {
                                                    @Override
                                                    public void onResult(DriveFolder.DriveFileResult result) {
                                                        if (!result.getStatus().isSuccess()) {
                                                            showMessage("Error while trying to create the file");
                                                            return;
                                                        }
                                                        Log.d("fileCallback", "Created a file with content: " + result.getDriveFile().getDriveId());
                                                        showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
                                                        files_uploaded++;
                                                        floatingActionButton.setProgress(floatingActionButton.getProgress() + 100/MyApplication.current_chant.files.size(), true);
                                                        Log.d("fileCallback", files_uploaded + " files completed");

                                                        upload_status_snack.setText("Uploaded file " + files_uploaded + " of " +  MyApplication.current_chant.files.size());

                                                        if(files_uploaded >= MyApplication.current_chant.files.size()){
                                                            showMessage("FINISHED UPLOADING ALL FILES!!!");
                                                            floatingActionButton.setProgress(100, true);
                                                            floatingActionButton.setImageDrawable(new IconicsDrawable(UploadActivity.this)
                                                                    .icon(MaterialDesignIconic.Icon.gmi_cloud_done)
                                                                    .color(Color.WHITE)
                                                                    .actionBar());
                                                            upload_status_snack.setText("Upload Complete");
                                                            upload_status_snack.dismiss();
                                                        }
                                                    }
                                                };
                                        //******


                                    for( int i = 0; i < MyApplication.current_chant.files.size(); i++) {

                                        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                                .setResultCallback(new
                                                                           ResultCallback<DriveApi.DriveContentsResult>() {
                                                                               @Override
                                                                               public void onResult(DriveApi.DriveContentsResult result) {

                                                                                   if (!result.getStatus().isSuccess()) {
                                                                                       showMessage("Error while trying to create new file contents");
                                                                                       return;
                                                                                   }

                                                                                   final DriveContents driveContents = result.getDriveContents();

                                                                                   // Perform I/O off the UI thread.
                                                                                   new Thread() {
                                                                                       @Override
                                                                                       public void run() {


                                                                                           // write content to DriveContents
                                                                                           OutputStream outputStream = driveContents.getOutputStream();

                                                                                           int choosenIndex = 0;

                                                                                            for(int x = 0; x < fileUploadWrappers.size(); x++){
                                                                                                Log.d("choosenIndex", fileUploadWrappers.get(x).myFile.filename + " / uploading: " + fileUploadWrappers.get(x).isUploading );

                                                                                                if(!fileUploadWrappers.get(x).isUploading){
                                                                                                    choosenIndex = x;
                                                                                                    fileUploadWrappers.get(x).isUploading = true;
                                                                                                    break;
                                                                                                }

                                                                                            }

                                                                                           File file = new File(MyApplication.current_chant.files.get(choosenIndex).filename);

                                                                                           try {
                                                                                               InputStream dbInputStream = new FileInputStream(file);

                                                                                               byte[] buffer = new byte[1024];
                                                                                               int length;
                                                                                               int counter = 0;
                                                                                               while ((length = dbInputStream.read(buffer)) > 0) {
                                                                                                   ++counter;
                                                                                                   outputStream.write(buffer, 0, length);
                                                                                               }

                                                                                               dbInputStream.close();
                                                                                               outputStream.flush();
                                                                                               outputStream.close();

                                                                                           } catch (IOException e) {
                                                                                               e.printStackTrace();
                                                                                               //finish();
                                                                                           }

                                                                                           MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                                                                   .setTitle(title.getText().toString() + choosenIndex + ".3gp")
                                                                                                   .setMimeType("audio/3gp")
                                                                                                   .setStarred(true).build();


                                                                                           folderResult.getDriveFolder()
                                                                                                   .createFile(mGoogleApiClient, changeSet, driveContents)
                                                                                                   .setResultCallback(fileCallback);


                                                                                       }
                                                                                   }.start();
                                                                               }
                                                                           });

                                                            }


                                    }
                                }
                            };


                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(title.getText().toString() + " v" + (System.currentTimeMillis()/100)).build();



                    if(!PreferenceManager.getDefaultSharedPreferences(UploadActivity.this).getString( (UploadActivity.this.getResources().getString(R.string.app_folder_drive_id)) , "ROOT").equals("ROOT")){

                        DriveId.decodeFromString(PreferenceManager.getDefaultSharedPreferences(UploadActivity.this).getString( (UploadActivity.this.getResources().getString(R.string.app_folder_drive_id)) , "root"))
                                .asDriveFolder().createFolder(
                                mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);

                    }else{

                        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
                                mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);

                    }

                }else if(!(title.getText().toString().length() > 3 && title.getText().toString().length() < 31)){
                    Snackbar.make(findViewById(R.id.base), "Please enter a correct title", Snackbar.LENGTH_LONG);
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(UploadActivity.this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();

    }


    private DriveId mFolderDriveId;


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

        isConnected = true;

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

                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(UploadActivity.this);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean(getResources().getString(R.string.is_app_folder_created), true);

                                editor.putString(getResources().getString(R.string.app_folder_drive_id), result.getDriveFolder().getDriveId().encodeToString());

                                editor.commit();
                            }
                        }
                    };


            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle("Verse.App Data Folder created at " + System.currentTimeMillis()).build();
            Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
                    mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
        }

    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */



    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "GoogleApiClient connection suspended");

        isConnected = false;


    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
        }

        isConnected = false;
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
       // Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public class FileUploadWrapper{
        public MyFile myFile = new MyFile();
        public boolean isUploading = false;
        public FileUploadWrapper(){

        }

        public FileUploadWrapper(MyFile myFile, boolean isUploading) {
            this.myFile = myFile;
            this.isUploading = isUploading;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
