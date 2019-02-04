package com.veed.oduchantingapp;

import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.Environment;

import android.view.MotionEvent;
import android.view.View;

import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.easyandroidanimations.library.Animation;

import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.BounceAnimation;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.Objects.CircleButton;
import com.veed.oduchantingapp.Utils.MyFile;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class RecordActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    private Runnable mUpdateTaskup = new Runnable() {
        public void run() {
            counter+= 0.1;

            if( counter >= 60 )
            {
                Log.d("mUpdateTaskup", "MAX DURATION REACHED");
                showMessage("MAX DURATION REACHED");

                if (isRecording) {
                    isRecording = false;
                    record_button.setHoldingOffButton();
                    counter = 0;
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;

                    status_text_view.setText("Play");
                    gesture.setImageDrawable(getResources().getDrawable(R.drawable.success_press));
                    can_play = true;
                    setCan_done(true);
                    can_record = false;

                    record_button.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                            .icon(MaterialDesignIconic.Icon.gmi_check)
                            .color(Color.parseColor("#00ccd6"))
                            .sizeDp(100));

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            can_next = true;
                        }
                    }, 300);

                    mHandler.removeCallbacks(mUpdateTaskup);


                }

            } else {
                status_text_view.setText(String.format("%.1f", counter));
                Log.i("repeatBtn", "repeat click");
                mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
            }
        }//end run
    };// end runnable

    float counter = 0;


    Chant new_chant = new Chant();
    CircleButton record_button;
    TextView status_text_view, lines_text_view;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    int record_count = 0;
    boolean overwriting_record = false;
    boolean can_record = true;
    boolean can_play = false;
    boolean can_next = false;

    boolean can_done = false;
    boolean isRecording = false;
    boolean saved = false;
    final MediaPlayer mediaPlayer = MyMediaPlayer.getInstance(this);
    Toolbar toolbar;
    ImageView gesture;
    ProgressBar player_progress;
    IconicsImageView play_icon;
    View player;

    GestureDetector gs;

    MenuItem done_button;
    boolean use_dark_theme = true;

    //SET TIME LIMIT ON RECORDINGS

    @Override
    public void onBackPressed() {

        new MaterialDialog.Builder(RecordActivity.this)
                .positiveText("Yes")
                .negativeText("Cancel")
                .title("Are you sure you want to exit?")
                .content("Your work will not be saved ")
                .backgroundColor((use_dark_theme ? Color.DKGRAY : Color.WHITE))
                .titleGravity(GravityEnum.START)
                .titleColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                .contentColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                .positiveColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                .negativeColor((use_dark_theme ? Color.WHITE : Color.BLACK))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        finish();
                    }

                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);

        if (!use_dark_theme) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_record);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            new_chant.name = extras.getString("chant_name");
            new_chant.id = extras.getString("chant_id");
            setTitle(new_chant.name);
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        record_button = (CircleButton) findViewById(R.id.record_button);
        gesture = (ImageView) findViewById(R.id.gesture);

        player = findViewById(R.id.player);
        player_progress = (ProgressBar) findViewById(R.id.player_progressBar);
        play_icon = (IconicsImageView) findViewById(R.id.play_icon);


        play_icon.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                .icon(MaterialDesignIconic.Icon.gmi_play)
                .color(Color.parseColor("#9E9E9E"))
                .sizeDp(24));

        player.setVisibility(View.GONE);

        android.view.animation.Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        //gesture.startAnimation(myFadeInAnimation);

        status_text_view = (TextView) findViewById(R.id.status);
        lines_text_view = (TextView) findViewById(R.id.lines);
        gs = new GestureDetector(RecordActivity.this, new GestureListener());

        lines_text_view.setText(record_count + " Lines");

        record_button.setImageDrawable(getResources().getDrawable(R.drawable.mic_svg));

                /*record_button.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                        .icon(MaterialDesignIconic.Icon.gmi_mic)
                        .color(getResources().getColor(R.color.light_primary))
                        .sizeDp(40));
                        */

        outputFile = getFilesDir() + "/" +  new_chant.id + (System.currentTimeMillis()) + ".3gp";

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
                        can_next = false;

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
                                    can_next = true;
                                }
                            }, 200);



                            //findViewById(R.id.record_menu).setVisibility(View.VISIBLE);

                            //status_text_view.setVisibility(View.GONE);

                            status_text_view.setText("Play");

                            //status_text_view.setVisibility(View.GONE);
                            //player.setVisibility(View.VISIBLE);


                            gesture.setImageDrawable(getResources().getDrawable(R.drawable.success_press));


                            can_play = true;
                            setCan_done(true);
                            can_record = false;

                            record_button.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                                    .icon(MaterialDesignIconic.Icon.gmi_check)
                                    .color(Color.parseColor("#00ccd6"))
                                    .sizeDp(100));


                            ////Toast.makeText(getApplicationContext(), "Audio " + record_count + " recorded successfully", //Toast.LENGTH_LONG).show();
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


                    player_progress.setMax(mediaPlayer.getDuration());

                    mediaPlayer.start();

                    player_progress.setMax(mediaPlayer.getDuration());
                    player_progress.postDelayed(onEverySecond, 300);

                    play_icon.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                            .icon(MaterialDesignIconic.Icon.gmi_pause)
                            .color(Color.parseColor("#9E9E9E"))
                            .sizeDp(24));




                    can_play = false;
                    can_next = false;
                    can_record = false;
                    setCan_done(false);

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            can_play = true;
                            can_next = true;
                            can_record = false;
                            setCan_done(true);
                            status_text_view.setText("Play");
                            play_icon.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                                    .icon(MaterialDesignIconic.Icon.gmi_play)
                                    .color(Color.parseColor("#9E9E9E"))
                                    .sizeDp(24));
                        }
                    });
                    //Toast.makeText(getApplicationContext(), "Playing audio", //Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        if(can_done & !saved) {
            if(record_count > 0){
                DatabaseHandler.getInstance(RecordActivity.this).saveChantLines(new_chant);
            }
        }else {
            if (!saved) {
                for (int i = 0; i < new_chant.files.size(); i++) {
                    File file = new File(new_chant.files.get(i).filename);
                    file.delete();
                }
            }
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
            if(record_count > 0) {
                DatabaseHandler.getInstance(RecordActivity.this).saveChantLines(new_chant);
                saved = true;
            }
            finish();
            return true;
        }

        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getFileForPosition(int position){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording" + position + ".3gp";
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (can_next) {

                new_chant.files.add(new MyFile(outputFile,((EditText)findViewById(R.id.lineedittext)).getText().toString()));
                ((EditText)findViewById(R.id.lineedittext)).setText("");

                Log.d("saving file", "saving file " + outputFile);
                status_text_view.setVisibility(View.VISIBLE);
                //player.setVisibility(View.GONE);
                status_text_view.setText("Hold to Record");
                record_count++;
                lines_text_view.setText(record_count + (record_count > 1 ? " Lines" : "Line"));
                outputFile = getFilesDir() + "/" +  new_chant.id + (System.currentTimeMillis()) + ".3gp";
                myAudioRecorder = new MediaRecorder();
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                myAudioRecorder.setOutputFile(outputFile);

                can_record = true;
                can_next = false;
                can_play = false;
                setCan_done(true);
                overwriting_record = false;

                record_button.setImageDrawable(getResources().getDrawable(R.drawable.mic_svg));
                gesture.setImageDrawable(getResources().getDrawable(R.drawable.long_press));


                Snackbar.make(findViewById(R.id.base), "New line added!", Snackbar.LENGTH_SHORT).show();

                /*record_button.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                        .icon(MaterialDesignIconic.Icon.gmi_mic)
                        .color(getResources().getColor(R.color.light_primary))
                        .sizeDp(40));
                        */
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {

            if (can_next) {

                new BounceAnimation(record_button).setListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {

                        status_text_view.setText("Hold to record again");
                        status_text_view.setVisibility(View.VISIBLE);
                       //player.setVisibility(View.GONE);

                        record_button.setImageDrawable(getResources().getDrawable(R.drawable.mic_svg));
                        gesture.setImageDrawable(getResources().getDrawable(R.drawable.long_press));


                /*record_button.setImageDrawable(new IconicsDrawable(RecordActivity.this)
                        .icon(MaterialDesignIconic.Icon.gmi_mic)
                        .color(getResources().getColor(R.color.light_primary))
                        .sizeDp(40));
                        */

                        can_record = true;
                        can_next = false;
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

            /*done_button.setIcon(
                    new IconicsDrawable(this)
                            .icon(MaterialDesignIconic.Icon.gmi_check)
                            .color(Color.parseColor("#FFFFFF"))
                            .actionBar());
                            */

        }else{
            done_button.setEnabled(false);

            /*
            done_button.setIcon(
                    new IconicsDrawable(this)
                            .icon(MaterialDesignIconic.Icon.gmi_check)
                            .color(Color.parseColor("#58FFFFFF"))
                            .actionBar());
                            */
        }

        this.can_done = can_done;


    }


    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run(){
            if(true == mediaPlayer.isPlaying()){
                if(player_progress != null) {
                    player_progress.setProgress(mediaPlayer.getCurrentPosition());
                }

                if(mediaPlayer.isPlaying()) {
                    player_progress.postDelayed(onEverySecond, 300);
                }
            }else{
                player_progress.setProgress(0);
            }
        }
    };

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
