package com.veed.oduchantingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.marshalchen.ultimaterecyclerview.expanx.Util.parent;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.Adapters.ChantLinesDragAdapter;
import com.veed.oduchantingapp.Adapters.EditNavigationAdapter;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Fragments.EditInfoFragment;
import com.veed.oduchantingapp.Fragments.EditLinesFragment;
import com.veed.oduchantingapp.Utils.AnimUtils;

import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.veed.oduchantingapp.Fragments.EditLinesFragment.EDIT_LINES_ACTIVITY;

public class EditActivity extends IcePickActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    EditNavigationAdapter editNavigationAdapter;
    ViewPager pager;
    FloatingActionButton floatingActionButton;
    View save_item_view;
    public static final int ADD_LINE_ACTIVITY_CODE = 21443;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));


        boolean use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);

        if (!use_dark_theme) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }


        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        tabLayout = (TabLayout) findViewById(R.id.material_tabs);

        pager = (ViewPager) findViewById(R.id.pager);
        setSupportActionBar(toolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);



        editNavigationAdapter = new EditNavigationAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(editNavigationAdapter);
        pager.setOffscreenPageLimit(3);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 0:
                        floatingActionButton.show(true);
                        break;
                    case 1:
                        floatingActionButton.hide(true);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(pager);

        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);


        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean( (this.getResources().getString(R.string.show_edit_tap_target)) , true)
                || PreferenceManager.getDefaultSharedPreferences(this).getBoolean( this.getResources().getString(R.string.help_setting), false)
                ) {

        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(findViewById(R.id.dummy))
                .setPrimaryText("Editing")
                .setBackgroundColour((use_dark_theme ? getResources().getColor(R.color.dark_primary) : getResources().getColor(R.color.light_primary)))
                .setSecondaryText(" - Drag lines to rearrange \n - Tap the plus icon to add new line \n - Save when you are finished")
                .setIconDrawable(new IconicsDrawable(this)
                        .icon(MaterialDesignIconic.Icon.gmi_check)
                        .color(Color.WHITE)
                        .actionBar())
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                    {
                        //Do something such as storing a value so that this prompt is never shown again
                    }

                    @Override
                    public void onHidePromptComplete()
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(EditActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(getResources().getString(R.string.show_edit_tap_target), !PreferenceManager.getDefaultSharedPreferences(EditActivity.this).getBoolean(getResources().getString(R.string.show_edit_tap_target), false));
                        editor.commit();
                    }
                })
                .show();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, AddNewLineActivity.class);
                getWindow().setExitTransition(new Fade());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(EditActivity.this);
                EditActivity.this.startActivityForResult(intent, ADD_LINE_ACTIVITY_CODE, options.toBundle());
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        save_item_view = menu.findItem(R.id.action_save).getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        if(id == R.id.action_save)
        {

            DatabaseHandler.getInstance(this).updateChant(MyApplication.current_chant);

            final Snackbar snackbar = Snackbar.make(findViewById(R.id.base), "Saving...", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                snackbar.dismiss();
                    finish();

                }
            }, 2000);





            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "Called from activity");
        super.onActivityResult(requestCode, resultCode, data);

        try {

            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (requestCode == ADD_LINE_ACTIVITY_CODE && resultCode == RESULT_OK) {

            Snackbar.make(findViewById(R.id.base), "Saving...", Snackbar.LENGTH_LONG).show();

        }

        if (requestCode == EDIT_LINES_ACTIVITY && resultCode == RESULT_OK) {

            Snackbar.make(findViewById(R.id.base), "Saving...", Snackbar.LENGTH_LONG).show();

        }



    }


}
