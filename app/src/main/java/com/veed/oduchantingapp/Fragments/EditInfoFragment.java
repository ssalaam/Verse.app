package com.veed.oduchantingapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.veed.oduchantingapp.Adapters.ColorPickRecyclerViewAdapter;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.EditTag.EditTag;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.NewChantActivity;
import com.veed.oduchantingapp.PracticeActvity;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;
import com.veed.oduchantingapp.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.cropToPadding;
import static android.R.attr.editable;

/**
 * Created by Saboor Salaam on 1/13/2016.
 */
public class EditInfoFragment extends Fragment {

    Activity parent;
    public MaterialEditText title;
    public EditTag editTag;
    UltimateRecyclerView ultimateRecyclerView;
    public ColorPickRecyclerViewAdapter colorPickRecyclerViewAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = activity;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_edit_info, container, false);


        editTag = (EditTag) rootView.findViewById(R.id.edit_tag_view);
        editTag.addTag("Sample Tag");

        editTag.setOnAddTagListener(new EditTag.OnAddTagListener() {
            @Override
            public void OnTagAdded() {

                MyApplication.current_chant.tags = editTag.getTagList();

            }

            @Override
            public void OnTagDeleted() {

                MyApplication.current_chant.tags = editTag.getTagList();

            }
        });


        title = (MaterialEditText) rootView.findViewById(R.id.chantTitle);
        title.setUnderlineColor(Color.WHITE);


        title.setText(MyApplication.current_chant.name);
        editTag.setTagList(MyApplication.current_chant.tags);


        List<Integer> patterns = new ArrayList<>();


        for(int i = 0; i <  Utils.patterns.length; i++){
            patterns.add(Utils.patterns[i]);
        }


        ultimateRecyclerView = (UltimateRecyclerView) rootView.findViewById(R.id.scroll);
        colorPickRecyclerViewAdapter = new ColorPickRecyclerViewAdapter(patterns,parent);
        colorPickRecyclerViewAdapter.selected = MyApplication.current_chant.cover;
        colorPickRecyclerViewAdapter.selected = MyApplication.current_chant.cover;

        ultimateRecyclerView.setAdapter(colorPickRecyclerViewAdapter);

        //ultimateRecyclerView.addItemDecoration(new HomeActivity.VerticalSpaceItemDecoration(Utils.convertDpToPixel(10, this)));

        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        ultimateRecyclerView.setLayoutManager(new LinearLayoutManager(parent, LinearLayoutManager.HORIZONTAL, false));


        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence input, int start, int before, int count) {

                if (title.getText().toString().length() > 3 &&
                        title.getText().toString().length() < 30){

                    MyApplication.current_chant.name = input.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        colorPickRecyclerViewAdapter.setOnColorSelectedListener(new ColorPickRecyclerViewAdapter.OnColorSelectedListener() {
            @Override
            public void OnColorChanged(int current_cover) {
                MyApplication.current_chant.cover = current_cover;
                Log.d("Color Change", "Changed to " + current_cover);
            }
        }, MyApplication.current_chant.cover);


        return rootView;
    }

}
