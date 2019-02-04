package com.veed.oduchantingapp.Utils;

/**
 * Created by Saboor Salaam on 10/23/2015.
 */

public class AsyncError {
    public boolean cancel = false;
    public int code = 0;

    public AsyncError(boolean cancell, int code) {
        this.cancel = cancell;
        this.code = code;
    }
}