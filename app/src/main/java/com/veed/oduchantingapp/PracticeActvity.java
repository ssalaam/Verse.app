package com.veed.oduchantingapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.Adapters.PracticeChantViewPagerAdapter;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Fragments.ChantFragment;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;
import com.veed.oduchantingapp.Utils.SwipeControlViewPager;

import static android.view.View.GONE;

public class PracticeActvity extends AppCompatActivity {

    Handler mHandler = new Handler();

    ImageView forward, backward;
    public SwipeControlViewPager viewPager;
    ProgressBar progess;
    PracticeChantViewPagerAdapter practiceChantViewPagerAdapter;
    TextView count, total;
    int selected_position = 0;
    View empty_view;

    ViewGroup transitionsContainer;


    private Runnable playCurrentLineTask = new Runnable() {
        public void run() {
            if (selected_position == viewPager.getCurrentItem()) {
                if ((practiceChantViewPagerAdapter.getItemAt(viewPager.getCurrentItem())) instanceof ChantFragment) {
                    // Toast.makeText(PracticeActvity.this, "Playing because switching pages", Toast.LENGTH_SHORT).show();
                    ((ChantFragment) (practiceChantViewPagerAdapter.getItemAt(viewPager.getCurrentItem()))).playRecording();
                }
            }
        }//end run
    };// end runn

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));


        boolean use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);

        if (!use_dark_theme) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_practice_actvity);


        viewPager = (SwipeControlViewPager) findViewById(R.id.viewpager);

        progess = (ProgressBar) findViewById(R.id.progress);


        progess.getProgressDrawable().setColorFilter(use_dark_theme ? getResources().getColor(R.color.dark_primary) : getResources().getColor(R.color.light_primary) , PorterDuff.Mode.SRC_IN);
        forward = (ImageView) findViewById(R.id.forward);
        backward = (ImageView) findViewById(R.id.backward);
        count = (TextView) findViewById(R.id.count);
        total = (TextView) findViewById(R.id.total);
        empty_view = findViewById(R.id.emptyview);

        empty_view.setVisibility(View.GONE);

        transitionsContainer = (ViewGroup) findViewById(R.id.base);


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        total.setText(MyApplication.current_chant.files.size() + "");
        count.setText("1");


        forward.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_chevron_right)
                .color(Color.WHITE)
                .sizeDp(20));

        backward.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_chevron_left)
                .color(Color.WHITE)
                .sizeDp(20));

        forward.setVisibility(GONE);
        backward.setVisibility(GONE);

        if (MyApplication.current_chant.files.size() > 0) {
            practiceChantViewPagerAdapter = new PracticeChantViewPagerAdapter(MyApplication.current_chant, getSupportFragmentManager(), this);
            setTitle(MyApplication.current_chant.name + " (" + (viewPager.getCurrentItem() + 1) + "/" + MyApplication.current_chant.files.size() + ")");
        } else {
            empty_view.setVisibility(View.VISIBLE);
            viewPager.setVisibility(GONE);
        }

        viewPager.setAdapter(practiceChantViewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);


        viewPager.setClipToPadding(false);
        viewPager.setPadding(100,0, 100, 0);
        viewPager.setPageMargin(20);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selected_position = position;
                mHandler.postAtTime(playCurrentLineTask, SystemClock.uptimeMillis() + 200);
                count.setText((position + 1) + "");
                //Log.d("ViewPager", "Current progress: " +  ((double)(position+1)/(double)practiceChantViewPagerAdapter.getCount()) * 100);

                ObjectAnimator animation = ObjectAnimator.ofInt(progess, "progress", progess.getProgress(), (int) (((double) (position + 1) / (double) practiceChantViewPagerAdapter.getCount()) * (double) 100));
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(100);
                animation.start();

                //progess.setProgress((int) (((double) (position + 1) / (double) practiceChantViewPagerAdapter.getCount()) * (double) 100));

                setTitle(MyApplication.current_chant.name + " (" + (position + 1) + "/" + MyApplication.current_chant.files.size() + ")");

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        /*
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {

                if(MyApplication.current_chant.file_names.size() > 0) {
                    m.reset();
                    try {
                        m.setDataSource(MyApplication.current_chant.file_names.get(play_iterator));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        m.prepare();
                        m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                m.start();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    next.setEnabled(false);
                    play.setEnabled(false);
                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            play.setEnabled(true);
                            next.setEnabled(true);
                        }
                    });
                    Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
                }
            }
        });
        */

    }

    @Override
    protected void onStop() {
        //MyMediaPlayer.getInstance(this).stop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice_actvity, menu);

        menu.findItem(R.id.action_edit).setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_edit)
                .color(Color.WHITE)
                .actionBar());


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_edit) {

            Intent intent = new Intent(this, EditActivity.class);
            getWindow().setExitTransition(new Fade());
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this);
            startActivityForResult(intent, 0, options.toBundle());

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MyApplication.all_chants = DatabaseHandler.getInstance(this).getAllChants();

        if (MyApplication.current_chant.files.size() > 0) {
            practiceChantViewPagerAdapter = new PracticeChantViewPagerAdapter(MyApplication.current_chant, getSupportFragmentManager(), this);
        } else {
            empty_view.setVisibility(View.VISIBLE);
            viewPager.setVisibility(GONE);
        }

        viewPager.setAdapter(practiceChantViewPagerAdapter);

        setTitle(MyApplication.current_chant.name + " (" + (viewPager.getCurrentItem() + 1) + "/" + MyApplication.current_chant.files.size() + ")");


    }


}
