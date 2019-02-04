package com.veed.oduchantingapp.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.marshalchen.ultimaterecyclerview.uiUtils.CacheFragmentStatePagerAdapter;
import com.veed.oduchantingapp.Fragments.FinishedChantFragment;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.Fragments.ChantFragment;


/**
 * Created by Saboor Salaam on 9/13/2015.
 */


public class PracticeChantViewPagerAdapter extends CacheFragmentStatePagerAdapter {

    Context context;
    Chant chant;

    public PracticeChantViewPagerAdapter(Chant chant, FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.chant = chant;
    }

    @Override
    public int getCount() {
        return chant.files.size();
    }

    @Override
    protected Fragment createItem(int position) {
        if(position < chant.files.size()) {
            return new ChantFragment().newInstance(chant.files.get(position).filename, chant.files.get(position).text);
        }else{
            return new FinishedChantFragment().newInstance(chant.name);
        }

    }
}
