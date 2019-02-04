package com.veed.oduchantingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.CloudChip.ChipCloud;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Utils.MyFile;
import com.veed.oduchantingapp.Utils.Utils;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class ChantCoverActivity extends IcePickActivity {

    ChipCloud chipCloud;
    TextView title, desc, push, edit;
    ImageView cover, edit_icon, cloud_icon;
    LinearLayout info_holder;
    FloatingActionButton fabPractice;


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

        setContentView(R.layout.activity_chant_cover);


        if(MyApplication.current_chant == null){
            supportFinishAfterTransition();
        }
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");



        fabPractice = (FloatingActionButton) findViewById(R.id.fab_practice);
        info_holder = (LinearLayout) findViewById(R.id.info);

        chipCloud = (ChipCloud) findViewById(R.id.chip_cloud);

        title = (TextView) findViewById(R.id.title);
        desc = (TextView) findViewById(R.id.desc);

        push = (TextView) findViewById(R.id.cloud);



        edit = (TextView) findViewById(R.id.edit);


        cover = (ImageView) findViewById(R.id.cover);
        edit_icon = (ImageView) findViewById(R.id.edit_icon);
        cloud_icon = (ImageView) findViewById(R.id.cloud_icon);

        edit_icon.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_edit)
                .color(Color.WHITE)
                .sizeDp(20));

        cloud_icon.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_cloud_upload)
                .color(Color.WHITE)
                .sizeDp(24));

        cover.setImageAlpha(200);


        int[] mPatterns = getResources().getIntArray(R.array.patterns);

        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), Utils.patterns[MyApplication.current_chant.cover]);

        if (myBitmap != null && !myBitmap.isRecycled()) {
            Palette.from(myBitmap).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette palette) {

                    info_holder.setBackgroundColor(palette.getMutedColor(getResources().getColor(R.color.tint)));
                    //floatingActionMenu.setMenuButtonColorNormal(palette.getDarkVibrantColor(Color.BLACK));

/*
                            if(vibrantSwatch != null) {
                                        // access palette colors here
                                        if(palette.getLightVibrantSwatch() != null) {
                                            //chantViewHolder.title.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());
                                            chantViewHolder.cover.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());
                                        }else if(palette.getDarkVibrantSwatch() != null) {
                                            //chantViewHolder.title.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                            chantViewHolder.title_holder.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                        }else {
                                            //chantViewHolder.title.setBackgroundColor(palette.getDominantColor(parent.getResources().getColor(R.color.dark_primary)));
                                            chantViewHolder.title_holder.setBackgroundColor(palette.getDominantColor(parent.getResources().getColor(R.color.tint)));
                                        }
                                //get rgb
                            } else {

                                chantViewHolder.title_holder.setBackgroundColor(palette.getDominantColor(parent.getResources().getColor(R.color.tint)));

                                //get another swatch
                            }
                            */
                }
            });
        }

            info_holder.setAlpha(.95f);



        Glide
                .with(this)
                .load(Utils.patterns[MyApplication.current_chant.cover])
                .centerCrop()
                .thumbnail(0.5f)
                //.bitmapTransform(new ColorFilterTransformation(parent, getMatColor("500")))
                .into(cover);


        chipCloud.removeAllViews();

        for(int i = 0; i < MyApplication.current_chant.tags.size(); i++){
            chipCloud.addChip(MyApplication.current_chant.tags.get(i));
        }


        title.setText(MyApplication.current_chant.name);
        // chantViewHolder.title.setTextColor(chants.get(position).color);

        desc.setText(MyApplication.current_chant.files.size() + " lines saved ");

        fabPractice.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_play)
                .color(Color.WHITE)
                .actionBar());

        /*
        fabEdit.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_edit)
                .color(Color.WHITE)
                .actionBar());

        fabUpload.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_cloud_upload)
                .color(Color.WHITE)
                .actionBar());


        floatingActionMenu.getMenuIconView().setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_more_vert)
                .color(Color.WHITE)
                .actionBar());

        floatingActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {

                if(opened){

                    floatingActionMenu.getMenuIconView().setImageDrawable(new IconicsDrawable(ChantCoverActivity.this)
                            .icon(MaterialDesignIconic.Icon.gmi_play)
                            .color(Color.WHITE)
                            .actionBar());
                    cover.setImageAlpha(70);
                }else{
                    floatingActionMenu.getMenuIconView().setImageDrawable(new IconicsDrawable(ChantCoverActivity.this)
                            .icon(MaterialDesignIconic.Icon.gmi_more_vert)
                            .color(Color.WHITE)
                            .actionBar());
                    cover.setImageAlpha(200);
                }
            }
        });

        */

        fabPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChantCoverActivity.this, PracticeActvity.class);
                ChantCoverActivity.this.startActivityForResult(intent, 0);
            }
        });


        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChantCoverActivity.this, PracticeActvity.class);
                ChantCoverActivity.this.startActivityForResult(intent, 0);
            }
        });
        info_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(MyApplication.isFreeVersion){
                    new MaterialDialog.Builder(ChantCoverActivity.this)
                    .positiveText("UPGRADE")
                            .negativeText("NO THANKS")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Utils.launchGooglePlay(ChantCoverActivity.this, 1234);
                                }
                            })
                            .title("Upgrade to Verse.app Pro")
                            .content("Whoops. Looks like you have the free version. If you would like to back your data up on Google Drive you can purchase the Pro version.")
                            .backgroundColor((Color.WHITE))
                            .titleGravity(GravityEnum.START)
                            .titleColor((Color.BLACK))
                            .negativeColor(Color.BLACK)
                            .contentColor((Color.BLACK))
                            .positiveColor((Color.BLACK))
                            .show();

                }else {

                    Intent intent = new Intent(ChantCoverActivity.this, UploadActivity.class);
                    getWindow().setExitTransition(new Fade());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(ChantCoverActivity.this);
                    startActivityForResult(intent, 0, options.toBundle());
                }

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(ChantCoverActivity.this, EditActivity.class);
                        getWindow().setExitTransition(new Fade());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(ChantCoverActivity.this);
                        startActivityForResult(intent, 0 , options.toBundle());

                    }
                });



        /*
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ChantCoverActivity.this, UploadActivity.class);
                ChantCoverActivity.this.startActivityForResult(intent, 0);
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ChantCoverActivity.this, EditActivity.class);
                ChantCoverActivity.this.startActivityForResult(intent, 0);
                floatingActionMenu.close(true);
            }
        });
        */



        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean( (this.getResources().getString(R.string.show_cover_tap_target)) , true)
                || PreferenceManager.getDefaultSharedPreferences(this).getBoolean( this.getResources().getString(R.string.help_setting), false)
                ) {

            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(fabPractice)
                    .setPrimaryText("Practice Make Perfect")
                    .setBackgroundColour((use_dark_theme ? getResources().getColor(R.color.dark_primary) : getResources().getColor(R.color.light_primary)))
                    .setSecondaryText("Tap the play icon to start practicing this verse")
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            //Do something such as storing a value so that this prompt is never shown again
                        }

                        @Override
                        public void onHidePromptComplete() {
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ChantCoverActivity.this);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(getResources().getString(R.string.show_cover_tap_target), !PreferenceManager.getDefaultSharedPreferences(ChantCoverActivity.this).getBoolean(getResources().getString(R.string.show_cover_tap_target), false));
                            editor.commit();
                        }
                    })
                    .show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cover, menu);

        menu.findItem(R.id.action_cloud).setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_cloud_upload)
                .color(Color.WHITE)
                .actionBar());

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


        if(id == android.R.id.home)
        {
            super.onBackPressed();
        }

        if(id == R.id.action_cloud){


            if(MyApplication.isFreeVersion){
                new MaterialDialog.Builder(ChantCoverActivity.this)
                        .positiveText("UPGRADE")
                        .negativeText("NO THANKS")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Utils.launchGooglePlay(ChantCoverActivity.this, 1234);
                            }
                        })
                        .title("Upgrade to Verse.app Pro")
                        .content("Whoops. Looks like you have the free version. If you would like to back your data up on Google Drive you can purchase the Pro version.")
                        .backgroundColor((Color.WHITE))
                        .titleGravity(GravityEnum.START)
                        .titleColor((Color.BLACK))
                        .negativeColor(Color.BLACK)
                        .contentColor((Color.BLACK))
                        .positiveColor((Color.BLACK))
                        .show();

            }else {

                Intent intent = new Intent(ChantCoverActivity.this, UploadActivity.class);
                getWindow().setExitTransition(new Fade());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ChantCoverActivity.this);
                startActivityForResult(intent, 0, options.toBundle());

            }
        }

        if(id == R.id.action_edit){

            Intent intent = new Intent(ChantCoverActivity.this, EditActivity.class);
            getWindow().setExitTransition(new Fade());
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(ChantCoverActivity.this);
            startActivityForResult(intent, 0 , options.toBundle());

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MyApplication.all_chants = DatabaseHandler.getInstance(this).getAllChants();

        for (int i = 0; i < MyApplication.all_chants.size(); i++) {
            if (MyApplication.all_chants.get(i).id.equals(MyApplication.current_chant.id)) {
                MyApplication.current_chant = MyApplication.all_chants.get(i);
            }
        }

        title.setText(MyApplication.current_chant.name);
        // chantViewHolder.title.setTextColor(chants.get(position).color);

        desc.setText(MyApplication.current_chant.files.size() + " lines saved ");

        int[] mPatterns = getResources().getIntArray(R.array.patterns);

        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), Utils.patterns[MyApplication.current_chant.cover]);

        if (myBitmap != null && !myBitmap.isRecycled()) {
            Palette.from(myBitmap).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette palette) {

                    info_holder.setBackgroundColor(palette.getMutedColor(getResources().getColor(R.color.tint)));
                    //floatingActionMenu.setMenuButtonColorNormal(palette.getDarkVibrantColor(Color.BLACK));

/*
                            if(vibrantSwatch != null) {
                                        // access palette colors here
                                        if(palette.getLightVibrantSwatch() != null) {
                                            //chantViewHolder.title.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());
                                            chantViewHolder.cover.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());
                                        }else if(palette.getDarkVibrantSwatch() != null) {
                                            //chantViewHolder.title.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                            chantViewHolder.title_holder.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                        }else {
                                            //chantViewHolder.title.setBackgroundColor(palette.getDominantColor(parent.getResources().getColor(R.color.dark_primary)));
                                            chantViewHolder.title_holder.setBackgroundColor(palette.getDominantColor(parent.getResources().getColor(R.color.tint)));
                                        }
                                //get rgb
                            } else {

                                chantViewHolder.title_holder.setBackgroundColor(palette.getDominantColor(parent.getResources().getColor(R.color.tint)));

                                //get another swatch
                            }
                            */
                }
            });
        }

        info_holder.setAlpha(.95f);



        Glide
                .with(this)
                .load(Utils.patterns[MyApplication.current_chant.cover])
                .centerCrop()
                .thumbnail(0.5f)
                //.bitmapTransform(new ColorFilterTransformation(parent, getMatColor("500")))
                .into(cover);


        chipCloud.removeAllViews();

        for(int i = 0; i < MyApplication.current_chant.tags.size(); i++){
            chipCloud.addChip(MyApplication.current_chant.tags.get(i));
        }
    }

}
