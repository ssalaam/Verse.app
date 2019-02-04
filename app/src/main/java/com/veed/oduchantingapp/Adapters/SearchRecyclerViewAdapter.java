package com.veed.oduchantingapp.Adapters;

/**
 * Created by Saboor Salaam on 6/11/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.veed.oduchantingapp.CloudChip.ChipCloud;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.PracticeActvity;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.Utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Chant> chants = new ArrayList<>();
    Activity parent;

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int CHANT_TYPE = 1;
    public static final int RESULT_HEADER = 2;

    UltimateRecyclerView recyclerView;

    private ClickListener mCallback;

    public interface ClickListener {
        void onClick(int index, View v);

        void onLongClick(int index);
    }


    public SearchRecyclerViewAdapter(UltimateRecyclerView recyclerView, List<Chant> chants, Activity parent, ClickListener callback) {
        mCallback = callback;
        this.chants = chants;
        this.parent = parent;
        this.recyclerView = recyclerView;
        setHasStableIds(true);


        for(int i = 0; i < chants.size(); i++){
            Log.d("HomeRecyclerViewAdapter", chants.get(i).name);
            //chants.get(i).color = getMatColor("500");
            if(i == chants.size()-1){
                chants.get(i).isLast = true;
            }
        }

        if(chants.size() < 1) {
            recyclerView.showEmptyView();
        }else{
            recyclerView.hideEmptyView();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        if(chants.size() < 1) {
            recyclerView.showEmptyView();
        }else{
            recyclerView.hideEmptyView();
        }

        return chants.size()+1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else if( position == 19009)
        {
            return RESULT_HEADER;
        } else {
            return CHANT_TYPE;
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        switch(viewType){
            case RESULT_HEADER:
                return new ResultHeader(mInflater.inflate(R.layout.search_header, parent, false));
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(mInflater.inflate(R.layout.blank_header, parent, false));
            case CHANT_TYPE:
                return new ChantViewHolder(mInflater.inflate(R.layout.large_card_home_item, parent, false));
            default:
                return new ChantViewHolder(mInflater.inflate(R.layout.large_card_home_item, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int raw_position) {


        switch (getItemViewType(raw_position)) {
            case CHANT_TYPE:

                final ChantViewHolder chantViewHolder = (ChantViewHolder) holder;

                CardView.LayoutParams params = new CardView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, Utils.convertDpToPixel(150, parent));
                params.setMargins(Utils.convertDpToPixel(4, parent),Utils.convertDpToPixel(4, parent),Utils.convertDpToPixel(4, parent),Utils.convertDpToPixel(4, parent));
                chantViewHolder.itemView.findViewById(R.id.cardView).setLayoutParams(params);


                final int position = raw_position - 1;


                int[] mPatterns = parent.getResources().getIntArray(R.array.patterns);

                TypedArray patterns;
                patterns = parent.getResources().obtainTypedArray(R.array.patterns);


                Glide
                        .with(parent)
                        .load(Utils.patterns[chants.get(position).cover])
                        .centerCrop()
                        //.thumbnail(0.5f)
                        //.bitmapTransform(new ColorFilterTransformation(parent, getMatColor("500")))
                        .into(chantViewHolder.cover);


                chantViewHolder.chipCloud.removeAllViews();

                for(int i = 0; i < chants.get(position).tags.size(); i++){
                    chantViewHolder.chipCloud.addChip(chants.get(position).tags.get(i));
                }



                chantViewHolder.title.setText(chants.get(position).name);
                // chantViewHolder.title.setTextColor(chants.get(position).color);

                chantViewHolder.desc.setText(chants.get(position).files.size() + " lines saved ");

                /*
                chantViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new MaterialDialog.Builder(parent)
                                .title("Are you sure you want to delete this chant?")
                                .content("This cannot be undone")
                                .positiveText("Yes")
                                .negativeText("Cancel")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                        DatabaseHandler.getInstance(parent).deleteChant(chants.get(position));
                                        chants.remove(position);
                                        notifyDataSetChanged();
                                    }
                                })
                                .show();
                        return false;
                    }
                });
                */


                Bitmap myBitmap = BitmapFactory.decodeResource(parent.getResources(), Utils.patterns[chants.get(position).cover]);
                if (myBitmap != null && !myBitmap.isRecycled()) {
                    Palette.from(myBitmap).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {

                            chantViewHolder.title_holder_back.setBackgroundColor(palette.getMutedColor(parent.getResources().getColor(R.color.tint)));

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

                chantViewHolder.title_holder_back.setAlpha(.1f);
                break;
            default:
                break;
        }

        /*
        if(chants.get(raw_position).isLast){

            params.setMargins(Utils.convertDpToPixel(10, parent), Utils.convertDpToPixel(10, parent), Utils.convertDpToPixel(10, parent), Utils.convertDpToPixel(10, parent));

            chantViewHolder.itemView.setLayoutParams(params);

        }else{
            params.setMargins(Utils.convertDpToPixel(10, parent), Utils.convertDpToPixel(10, parent), Utils.convertDpToPixel(10, parent), Utils.convertDpToPixel(0, parent));

        }*/


    }

    public class ChantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title, desc;
        ImageView cover;
        Toolbar toolbar;
        FrameLayout title_holder_back;

        ChipCloud chipCloud;


        public ChantViewHolder(View view) {

            super(view);
            toolbar = (Toolbar)  view.findViewById(R.id.toolbar);
            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            //title_holder_back = (FrameLayout) view.findViewById(R.id.title_holder_back);

            //toolbar.inflateMenu(R.menu.menu_home_item);


            chipCloud = (ChipCloud) view.findViewById(R.id.chip_cloud);
            title = (TextView) view.findViewById(R.id.title);
            desc = (TextView) view.findViewById(R.id.desc);
            cover = (ImageView) view.findViewById(R.id.cover);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Forwards to the adapter's constructor callback
            if (mCallback != null) mCallback.onClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            // Forwards to the adapter's constructor callback
            if (mCallback != null) mCallback.onLongClick(getAdapterPosition());
            return true;
        }

    }
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ResultHeader extends RecyclerView.ViewHolder {
        public ResultHeader(View view) {
            super(view);
        }
    }

    private int getMatColor(String typeColor)
    {
        int returnColor = Color.BLACK;
        int arrayId = parent.getResources().getIdentifier("mdcolor_" + typeColor, "array", parent.getApplicationContext().getPackageName());

        if (arrayId != 0)
        {
            TypedArray colors = parent.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }
}


