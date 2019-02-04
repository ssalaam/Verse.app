package com.veed.oduchantingapp;

import android.content.Intent;
import android.graphics.Color;
import android.icu.util.RangeValueIterator;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.veed.oduchantingapp.Adapters.ColorPickRecyclerViewAdapter;
import com.veed.oduchantingapp.Adapters.HomeRecyclerViewAdapter;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.EditTag.EditTag;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.Utils.Utils;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewChantActivity extends AppCompatActivity {
    Toolbar toolbar;
    MaterialEditText title;
    EditTag editTag;
    UltimateRecyclerView ultimateRecyclerView;
    ColorPickRecyclerViewAdapter colorPickRecyclerViewAdapter;


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



        setContentView(R.layout.activity_new_chant);




        editTag = (EditTag) findViewById(R.id.edit_tag_view);
        editTag.addTag("Sample Tag");

        title = (MaterialEditText) findViewById(R.id.chantTitle);
        title.setUnderlineColor(Color.WHITE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        List<Integer> patterns = new ArrayList<>();


        for(int i = 0; i <  Utils.patterns.length; i++){
            patterns.add(Utils.patterns[i]);
        }





        ultimateRecyclerView = (UltimateRecyclerView) findViewById(R.id.scroll);
        colorPickRecyclerViewAdapter = new ColorPickRecyclerViewAdapter(patterns,this);

        ultimateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ultimateRecyclerView.setAdapter(colorPickRecyclerViewAdapter);

        //ultimateRecyclerView.addItemDecoration(new HomeActivity.VerticalSpaceItemDecoration(Utils.convertDpToPixel(10, this)));

        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);



        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence input, int start, int before, int count) {


                if(toolbar.getMenu().findItem(R.id.action_create) != null) {
                    if (!DatabaseHandler.getInstance(NewChantActivity.this).isChantUnique(input.toString()) || input.toString().trim().length() < 1) {
                        toolbar.getMenu().findItem(R.id.action_create).setEnabled(false);
                    } else {
                        toolbar.getMenu().findItem(R.id.action_create).setEnabled(true);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_chant, menu);


        if(toolbar.getMenu().findItem(R.id.action_create) != null) {
            if (!DatabaseHandler.getInstance(NewChantActivity.this).isChantUnique(title.getText().toString()) || title.getText().toString().trim().length() < 1) {
                toolbar.getMenu().findItem(R.id.action_create).setEnabled(false);
            } else {
                toolbar.getMenu().findItem(R.id.action_create).setEnabled(true);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create && title.getText().toString().length() > 3 && title.getText().toString().length() < 30) {

            Chant chant = new Chant();
            chant.tags = editTag.getTagList();
            chant.cover = colorPickRecyclerViewAdapter.current_cover;
            Log.d("NewChantActivity", "Current color " + colorPickRecyclerViewAdapter.current_cover);
            chant.name = title.getText().toString().trim();
            chant.id = chant.name.replace(" ", "") + System.currentTimeMillis();
            DatabaseHandler.getInstance(this).saveChant(chant);

            Intent intent = new Intent(this, RecordActivity.class);

            intent.putExtra("chant_id", chant.id);
            intent.putExtra("chant_name", chant.name);

            startActivityForResult(intent, 0);

            finish();

            //Intent intent = new Intent(this, HomeActivity.class);
            //startActivity(intent);
            return true;
        }else if(!(title.getText().toString().length() > 3 && title.getText().toString().length() < 31)){
            Snackbar.make(findViewById(R.id.base), "Please enter a correct title", Snackbar.LENGTH_LONG);
        }

        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

}



