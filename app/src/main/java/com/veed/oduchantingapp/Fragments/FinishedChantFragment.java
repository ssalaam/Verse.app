package com.veed.oduchantingapp.Fragments;

import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.PracticeActvity;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.RecordActivity;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;

import java.io.IOException;

/**
 * Created by Saboor Salaam on 1/13/2016.
 */
public class FinishedChantFragment extends Fragment {

    float counter = 0;


    // Store instance variables
    public  String name;
    Button study_again;
    ImageView icon;
    TextView prompt;

    boolean can_next = false;


    GestureDetector gs;


    // newInstance constructor for creating fragment with arguments
    public static FinishedChantFragment newInstance(String name) {
        FinishedChantFragment chantFragment = new FinishedChantFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        chantFragment.setArguments(args);
        return chantFragment;
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.finished_chant_fragment, container, false);

        name = getArguments().getString("name");


        study_again = (Button) view.findViewById(R.id.study_again);
        icon = (ImageView) view.findViewById(R.id.icon);
        prompt = (TextView) view.findViewById(R.id.prompt);


        prompt.setText(MyApplication.current_chant.files.size() > 1 ? "You've learned " + MyApplication.current_chant.files.size() + " lines from " + MyApplication.current_chant.name : "You've learned 1 line from " + MyApplication.current_chant.name);

        icon.setImageDrawable(new IconicsDrawable(getContext())
                .icon(MaterialDesignIconic.Icon.gmi_check)
                .color(Color.parseColor("#00ccd6"))
                .sizeDp(125));







        return view;
    }

}
