package com.veed.oduchantingapp.Fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.BounceAnimation;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Objects.CircleButton;
import com.veed.oduchantingapp.PracticeActvity;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.RecordActivity;
import com.veed.oduchantingapp.Utils.AnimUtils;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;
import com.veed.oduchantingapp.Utils.Utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Saboor Salaam on 1/13/2016.
 */
public class ChantFragment extends Fragment {

    float counter = 0;


    // Store instance variables
    public  String filename, line_text;
    public TextView text;
    final MediaPlayer mediaPlayer = MyMediaPlayer.getInstance(getContext());
    boolean showing_text = false, isEditing = false;
    ImageView soundIcon;
    CardView cardView;


    boolean can_next = false;


    GestureDetector gs;


    // newInstance constructor for creating fragment with arguments
    public static ChantFragment newInstance(String filename, String text) {
        ChantFragment chantFragment = new ChantFragment();
        Bundle args = new Bundle();
        args.putString("filename", filename);
        args.putString("text", text);
        chantFragment.setArguments(args);
        return chantFragment;
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.chant_fragment, container, false);

        gs = new GestureDetector(getContext(), new GestureListener());

        text = (TextView)view.findViewById(R.id.linetext);

        soundIcon = (ImageView)view.findViewById(R.id.soundIcon);
        cardView = (CardView) view.findViewById(R.id.cardView);

        filename = getArguments().getString("filename");
        line_text = getArguments().getString("text");

        if(line_text.trim().isEmpty()){
            line_text = "(Line text goes here)";
            text.setTextColor(Color.parseColor("#58FFFFFF"));
        }

        soundIcon.setImageDrawable(new IconicsDrawable(getContext())
                .icon(MaterialDesignIconic.Icon.gmi_volume_up)
                .color(Color.BLACK)
                .sizeDp(24));



        setShowText(true);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showing_text) {
                    setShowText(false);
                    showing_text = false;
                } else {
                    setShowText(true);
                    showing_text = true;
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    playRecording();
                }
            }
        });





        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){

            Log.e("On Config Change","LANDSCAPE");

            CardView.LayoutParams clp = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            clp.setMargins(0, Utils.convertDpToPixel(10, getContext()), 0 , Utils.convertDpToPixel(10, getContext()));

            cardView.setLayoutParams(clp);

        }else{

            Log.e("On Config Change","PORTRAIT");

            CardView.LayoutParams clp = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            clp.setMargins(0, Utils.convertDpToPixel(50, getContext()), 0 , Utils.convertDpToPixel(50, getContext()));

            cardView.setLayoutParams(clp);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void playRecording(){

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(filename);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("FileError", "Error on file " + filename);
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

        soundIcon.setImageDrawable(new IconicsDrawable(getContext())
                .icon(MaterialDesignIconic.Icon.gmi_volume_up)
                .color(getResources().getColor(R.color.md_blue_500))
                .sizeDp(24));

        soundIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    soundIcon.setImageDrawable(new IconicsDrawable(getContext())
                            .icon(MaterialDesignIconic.Icon.gmi_volume_up)
                            .color(getResources().getColor(R.color.black))
                            .sizeDp(24));
                }else{
                    mediaPlayer.start();
                    soundIcon.setImageDrawable(new IconicsDrawable(getContext())
                            .icon(MaterialDesignIconic.Icon.gmi_volume_up)
                            .color(getResources().getColor(R.color.md_blue_500))
                            .sizeDp(24));
                }
            }
        });

        Log.d("PlayingFile", "Playing file " + filename);




        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if(getContext() != null) {
                    soundIcon.setImageDrawable(new IconicsDrawable(getContext())
                            .icon(MaterialDesignIconic.Icon.gmi_volume_up)
                            .color(Color.BLACK)
                            .sizeDp(24));
                }

            }
        });


    }

    public void setShowText(boolean shown){
        if(shown){
            text.setText(line_text);
        }else{
            text.setText("Show text");
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            ((PracticeActvity) getActivity()).viewPager.setPagingEnabled(true);
            //if(!((EditText) getView().findViewById(R.id.edit_line_text)).getText().toString().isEmpty()) {
               // DatabaseHandler.getInstance(getContext()).updateLineText(filename.trim(), ((EditText) getView().findViewById(R.id.edit_line_text)).getText().toString());
            //}
           // text.setText(((EditText) getView().findViewById(R.id.edit_line_text)).getText().toString());
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (can_next) {

                super.onLongPress(e);
            }
        }
    }

}
