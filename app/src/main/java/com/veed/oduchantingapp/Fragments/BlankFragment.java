package com.veed.oduchantingapp.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veed.oduchantingapp.R;


/**
 * Created by Saboor Salaam on 4/27/2015.
 */
public class BlankFragment extends Fragment {

    Activity parent;
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

                 R.layout.fragment_blank, container, false);

        return rootView;
    }


}
