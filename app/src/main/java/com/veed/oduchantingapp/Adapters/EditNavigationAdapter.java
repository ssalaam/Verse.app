package com.veed.oduchantingapp.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.marshalchen.ultimaterecyclerview.uiUtils.CacheFragmentStatePagerAdapter;
import com.veed.oduchantingapp.Fragments.BlankFragment;
import com.veed.oduchantingapp.Fragments.EditInfoFragment;
import com.veed.oduchantingapp.Fragments.EditLinesFragment;



/**
 * Created by Saboor Salaam on 9/13/2015.
 */


public class EditNavigationAdapter extends CacheFragmentStatePagerAdapter {

    final String[] TITLES = new String[]{"Lines", "Edit Info"};

    private int mScrollY;
    Context context;

    public EditNavigationAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }

    @Override
    protected Fragment createItem(int position) {
        // Initialize fragments.
        // Please be sure to pass scroll position to each fragments using setArguments.
        switch (position) {

            case 1: {
                EditInfoFragment f = new EditInfoFragment();
                return f;
            }
            case 0: {
                EditLinesFragment f = new EditLinesFragment();
                return f;
            }

            default:
                return new BlankFragment();
        }
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
