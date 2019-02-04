package com.veed.oduchantingapp.Utils;

/**
 * Created by Saboor Salaam on 1/13/2016.
 */
public class MyFile {
    public MyFile(String filename, String text) {
        this.filename = filename;
        this.text = text;
    }

    public MyFile() {
    }

    public String filename, text;
    public long number;
    public boolean isPlaying = false;
}
