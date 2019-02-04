package com.veed.oduchantingapp.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.veed.oduchantingapp.R;


/**
 * Created by Saboor Salaam on 6/12/2015.
 */
public class StyledTextView extends TextView {

    public final static int NORMAL = 1, LIGHT = 0, BOLD = 2;
    static int style;

    public StyledTextView(Context context) {
        super(context);
    }

    public void setCustomTypeFace(int style){
        Typeface tf;
        switch(style) {
            case BOLD:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoSlab-Bold.ttf");
                setTypeface(tf);
                break;
            case NORMAL:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoSlab-Regular.ttf");
                setTypeface(tf);
                break;
            case LIGHT:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoSlab-Light.ttf");
                setTypeface(tf);
                break;
            default:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoMono-Regular.ttf");
                setTypeface(tf);
                break;
        }
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StyledTextView);
        try {
                style = a.getInt(R.styleable.StyledTextView_text_style, NORMAL);
        } finally {
            a.recycle();
        }

        style(context);
    }

    public StyledTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void style(Context context) {
        Typeface tf;

        switch(style) {
            case BOLD:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoMono-Bold.ttf");
                setTypeface(tf);
                break;
            case NORMAL:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoMono-Regular.ttf");
                setTypeface(tf);
                break;
            case LIGHT:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoMono-Light.ttf");
                setTypeface(tf);
                break;
            default:
                tf = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/RobotoMono-Regular.ttf");
                setTypeface(tf);
                break;
        }
    }
}
