package com.veed.oduchantingapp;

import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.view.MotionEvent;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.BounceAnimation;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Objects.CircleButton;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class AddNewLineActivity extends IcePickActivity {

    private Handler mHandler = new Handler();

    private Runnable mUpdateTaskup = new Runnable() {
        public void run() {
            counter+= 0.1;
            status_text_view.setText(String.format("%.1f", counter));
            Log.i("repeatBtn", "repeat click");
            mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
        }//end run
    };// end runnable

    float counter = 0;

    CircleButton record_button;
    TextView status_text_view, lines_text_view;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    boolean overwriting_record = false;
    boolean can_record = true;
    boolean can_play = false;

    boolean can_done = false;
    boolean isRecording = false;
    boolean saved = false;
    final MediaPlayer mediaPlayer = MyMediaPlayer.getInstance(this);

    GestureDetector gs;

    MenuItem done_button;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        boolean use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);

        if (!use_dark_theme) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_line);


        record_button = (CircleButton) findViewById(R.id.record_button);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add");

        status_text_view = (TextView) findViewById(R.id.status);
        gs = new GestureDetector(AddNewLineActivity.this, new GestureListener());


        record_button.setImageDrawable(getResources().getDrawable(R.drawable.mic_svg));

        outputFile = getFilesDir() + "/" +  MyApplication.current_chant.id + (System.currentTimeMillis()) + ".3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);


        record_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionevent) {

                if (can_record) {
                    int action = motionevent.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {

                        record_button.setHoldingDownButton();

                        mHandler.removeCallbacks(mUpdateTaskup);
                        mHandler.postAtTime(mUpdateTaskup,
                                SystemClock.uptimeMillis() + 50);

                        if (overwriting_record) {
                            myAudioRecorder = new MediaRecorder();
                            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            myAudioRecorder.setOutputFile(outputFile);
                        }
                        try {

                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                            isRecording = true;

                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        //Toast.makeText(getApplicationContext(), "Recording started", //Toast.LENGTH_LONG).show();

                        overwriting_record = true;
                        setCan_done(false);
                        can_play = false;

                    } else if (action == MotionEvent.ACTION_UP) {
                        if (isRecording) {
                            isRecording = false;
                            record_button.setHoldingOffButton();
                            Log.i("repeatBtn", "MotionEvent.ACTION_UP");
                            mHandler.removeCallbacks(mUpdateTaskup);
                            record_button.setHoldingOffButton();
                            counter = 0;


                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    myAudioRecorder.stop();
                                    myAudioRecorder.release();
                                    myAudioRecorder = null;
                                }
                            }, 200);



                            status_text_view.setText("Play");
                            can_play = true;
                            setCan_done(true);
                            can_record = false;

                            record_button.setImageDrawable(new IconicsDrawable(AddNewLineActivity.this)
                                    .icon(MaterialDesignIconic.Icon.gmi_check)
                                    .color(Color.parseColor("#00ccd6"))
                                    .sizeDp(100));

                        }
                    }//end else
                } else {
                    gs.onTouchEvent(motionevent);
                }
                return false;
            }
        });


        status_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                if(can_play){
                    status_text_view.setText("Playing..");
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();

                    can_play = false;
                    can_record = false;
                    setCan_done(false);

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            can_play = true;
                            can_record = false;
                            setCan_done(true);
                            status_text_view.setText("Play");
                        }
                    });
                    //Toast.makeText(getApplicationContext(), "Playing audio", //Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
            if (!saved) {
                    File file = new File(outputFile);
                    file.delete();
                }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_activity, menu);
        done_button = menu.getItem(0);
        setCan_done(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done ) {
            Log.d("Saving", "Saving to chant " + MyApplication.current_chant.name);
            DatabaseHandler.getInstance(AddNewLineActivity.this).addNewLine(MyApplication.current_chant.id ,outputFile,((EditText)findViewById(R.id.lineedittext)).getText().toString());
            saved = true;
            setResult(RESULT_OK);
            finish();
            return true;
        }

        if(id == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public void onLongPress(MotionEvent e) {

            if (can_done) {
                new BounceAnimation(record_button).setListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {

                        status_text_view.setText("Hold to record again");
                        record_button.setImageDrawable(getResources().getDrawable(R.drawable.mic_svg));

                        can_record = true;
                        can_play = false;
                        setCan_done(false);
                        overwriting_record = true;
                    }
                }).animate();

                super.onLongPress(e);
            }
        }
    }

    public void setCan_done(boolean can_done) {

        if(can_done){
            done_button.setEnabled(true);

        }else{
            done_button.setEnabled(false);
        }

        this.can_done = can_done;


    }

}
