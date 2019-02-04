package com.veed.oduchantingapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.veed.oduchantingapp.Adapters.ChantLinesDragAdapter;
import com.veed.oduchantingapp.Adapters.PracticeChantViewPagerAdapter;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.R;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.PracticeActvity;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.Utils.MyFile;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.veed.oduchantingapp.EditActivity.ADD_LINE_ACTIVITY_CODE;

/**
 * Created by Saboor Salaam on 1/13/2016.
 */
public class EditLinesFragment extends Fragment {
    
    Activity parent;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    public ChantLinesDragAdapter myItemAdapter;
    public static final int EDIT_LINES_ACTIVITY = 456;
    ProgressBar progressBar;
    View empty_view;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_LINE_ACTIVITY_CODE && resultCode == RESULT_OK) {

            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            MyApplication.all_chants = DatabaseHandler.getInstance(parent).getAllChants();

            for (int i = 0; i < MyApplication.all_chants.size(); i++) {
                if (MyApplication.all_chants.get(i).id.equals(MyApplication.current_chant.id)) {

                    if (MyApplication.all_chants.get(i).files.size() > 0) {

                        MyFile myFile = MyApplication.all_chants.get(i).files.get(MyApplication.all_chants.get(i).files.size() - 1);
                        myFile.number = MyApplication.all_chants.get(i).files.size();
                        MyApplication.current_chant.files.add(MyApplication.current_chant.files.size(), myFile);
                       // Log.MyApplication.all_chants.get(i).files.get(MyApplication.all_chants.get(i).files.size() - 1)

                        if (myItemAdapter != null) {
                            myItemAdapter.files = MyApplication.current_chant.files;
                           myItemAdapter.notifyDataSetChanged();

                            final Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }
                            }, 1500);
                        }
                    }
                }
            }

            if (MyApplication.current_chant.files.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                empty_view.setVisibility(View.GONE);
            } else {
                empty_view.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(GONE);
            }
        }

        if (requestCode == EDIT_LINES_ACTIVITY && resultCode == RESULT_OK) {

            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            MyApplication.all_chants = DatabaseHandler.getInstance(parent).getAllChants();

            for (int i = 0; i < MyApplication.current_chant.files.size(); i++) {

                if (MyApplication.current_chant.files.get(i).number == MyApplication.current_file.number) {

                    MyApplication.current_chant.files.get(i).text = MyApplication.current_file.text;

                    if (myItemAdapter != null) {
                        myItemAdapter.files = MyApplication.current_chant.files;
                        myItemAdapter.notifyItemChanged(i);

                        final Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }, 500);
                    }

                }
            }


        }
    }

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
                R.layout.fragment_edit_lines, container, false);


        empty_view = rootView.findViewById(R.id.emptyview);
        empty_view.setVisibility(View.GONE);


        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(parent);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        //mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                //(NinePatchDrawable) ContextCompat.getDrawable(parent, R.drawable.material_shadow_z3));
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);



        if (MyApplication.current_chant.files.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        } else {
            empty_view.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(GONE);
        }


        //adapter
        myItemAdapter = new ChantLinesDragAdapter(MyApplication.current_chant.files, parent);
        mAdapter = myItemAdapter;

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging

        final GeneralItemAnimator animator = new DraggableItemAnimator();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            //mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(parent, R.drawable.material_shadow_z3)));
        }
        //mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(parent, R.drawable.list_divider_h), true));

        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

        // for debugging
//        animator.setDebug(true);
//        animator.setMoveDuration(2000);

        return rootView;
    }


    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }



}
