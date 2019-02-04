package com.veed.oduchantingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.veed.oduchantingapp.Utils.AnimUtils;
import com.veed.oduchantingapp.Utils.HTML5WebView;
import com.veed.oduchantingapp.Utils.StyledTextView;
import com.veed.oduchantingapp.Utils.SystemUiHider;

public class AboutActivity extends  AppCompatActivity {

    Activity thisActivity;
    HTML5WebView mWebView;
    FrameLayout mFrameLayout;
    Toolbar toolbar;
    boolean saved = false, show_more_enabled = true;
    ProgressBar progressBar;
    StyledTextView show_more;

    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;



    boolean use_dark_theme;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);
        if (!use_dark_theme) {
            setTheme(R.style.LightTheme);
        } else{
            setTheme(R.style.DarkTheme);
        }


        super.onCreate(savedInstanceState);

        thisActivity = this;
        setContentView(R.layout.activity_about_layout);

        //StatusBarUtil.hide(this);

        mFrameLayout = (FrameLayout) findViewById(R.id.frame);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mWebView = new HTML5WebView(this);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            // autoplay when finished loading via javascript injection
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                //mWebView.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
            }
        });


        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            mWebView.loadUrl("http://thealadeproject.org/#/esetheapp5");
        }

        mFrameLayout.addView(mWebView.getLayout(), COVER_SCREEN_PARAMS);

    }

    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mWebView.stopLoading();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        mWebView.onResume();
        super.onResume();
        //StatusBarUtil.hide(this);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.inCustomView()) {
                mWebView.hideCustomView();
                mWebView.goBack();
                return true;
            }else{
                mWebView.onPause();
                finish();
                super.onBackPressed();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
            new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(toolbar) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(toolbar) == toolbar.getHeight();
    }

    private void showToolbar() {
        AnimUtils.show(toolbar);
        /*
        float headerTranslationY = ViewHelper.getTranslationY(toolbar);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(toolbar).cancel();
            ViewPropertyAnimator.animate(toolbar).translationY(0).setDuration(200).start();
        }
        */
    }

    private void hideToolbar() {
        AnimUtils.hide(toolbar);
        /*
        float headerTranslationY = ViewHelper.getTranslationY(toolbar);
        int toolbarHeight = toolbar.getHeight();
        if (headerTranslationY != toolbarHeight) {
            ViewPropertyAnimator.animate(toolbar).cancel();
            ViewPropertyAnimator.animate(toolbar).translationY(-toolbarHeight).setDuration(200).start();
        }
        */
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            //AnimUtils.hide(toolbar);
        }else{
            //AnimUtils.show(toolbar);
            //AnimUtils.show(show_more);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return  true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
