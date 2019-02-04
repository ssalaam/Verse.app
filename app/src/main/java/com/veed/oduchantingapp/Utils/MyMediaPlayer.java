package com.veed.oduchantingapp.Utils;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Saboor Salaam on 1/14/2016.
 */
public class MyMediaPlayer {

private static MediaPlayer sInstance;

    public static synchronized MediaPlayer getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new MediaPlayer();
        }
        return sInstance;
    }
}
