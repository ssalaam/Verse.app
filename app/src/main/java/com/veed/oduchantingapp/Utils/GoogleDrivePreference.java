package com.veed.oduchantingapp.Utils;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import com.veed.oduchantingapp.R;

/**
 * Created by Saboor Salaam on 11/8/2016.
 */

public class GoogleDrivePreference extends Preference {

    public GoogleDrivePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GoogleDrivePreference(Context context) {
        super(context);
        setLayoutResource(R.layout.google_drive_preference);
    }
}
