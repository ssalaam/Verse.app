package com.veed.oduchantingapp.Objects;

import android.graphics.Color;

import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.Utils.MyFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saboor Salaam on 1/11/2016.
 */
public class Chant {
    public String name, id;
    public Integer cover = 0;
    public List<MyFile> files = new ArrayList<>();
    public boolean isLast = false;
    public List<String> tags = new ArrayList<>();
    public Chant() {
    }
}
