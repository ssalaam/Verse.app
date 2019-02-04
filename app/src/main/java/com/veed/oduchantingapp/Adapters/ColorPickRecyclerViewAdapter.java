package com.veed.oduchantingapp.Adapters;

/**
 * Created by Saboor Salaam on 6/11/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.PracticeActvity;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.Utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class ColorPickRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Integer> covers = new ArrayList<>();
    Activity parent;
    private OnColorSelectedListener onColorSelectedListener;

    static final int VIEW_TYPE_HEADER = 0;
    static final int COLOR_TYPE = 1;
    public int selected = 0, current_cover = 0;

    public ColorPickRecyclerViewAdapter(List<Integer> covers, Activity parent) {

        if (covers.size() > 0) {
            current_cover = 0;
        }

        this.covers = covers;
        this.parent = parent;
        setHasStableIds(true);

    }


    public interface OnColorSelectedListener{
        public void OnColorChanged(int current_cover);
    }

    public  void setOnColorSelectedListener (OnColorSelectedListener onColorSelectedListener, int index){
        this.onColorSelectedListener = onColorSelectedListener;
        onColorSelectedListener.OnColorChanged(index);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return covers.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0456677) {
            return VIEW_TYPE_HEADER;
        } else {
            return COLOR_TYPE;
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        switch(viewType){
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(mInflater.inflate(R.layout.blank_header, parent, false));
            case COLOR_TYPE:
                return new ColorViewHolder(mInflater.inflate(R.layout.color_item, parent, false));
            default:
                return new ColorViewHolder(mInflater.inflate(R.layout.color_item, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int raw_position) {


        switch (getItemViewType(raw_position)) {
            case COLOR_TYPE:

                ColorViewHolder colorViewHolder = (ColorViewHolder) holder;

                final int position = raw_position;


                Glide
                        .with(parent)
                        .load(covers.get(position))
                        .centerCrop()
                        //.thumbnail(0.8f)
                        //.bitmapTransform(new ColorFilterTransformation(parent, getMatColor("500")))
                        .into(colorViewHolder.back);


                //((CardView) colorViewHolder.itemView).setCardBackgroundColor(colors.get(position));

                if(position == selected){
                    colorViewHolder.check.setVisibility(View.VISIBLE);
                }else{
                    colorViewHolder.check.setVisibility(View.INVISIBLE);
                }

                colorViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ColorPickRecyclerViewAdapter.this.notifyItemChanged(selected);
                        selected = position;
                        current_cover = selected;
                        ColorPickRecyclerViewAdapter.this.notifyItemChanged(position);

                            if (onColorSelectedListener != null) {
                                onColorSelectedListener.OnColorChanged(current_cover);
                            }
                        }
                });




               colorViewHolder.check.setImageDrawable(new IconicsDrawable(parent)
                        .icon(MaterialDesignIconic.Icon.gmi_check)
                        .color(Color.WHITE)
                        .sizeDp(24));

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

    static class ColorViewHolder extends RecyclerView.ViewHolder {

        ImageView check, back;

        public ColorViewHolder(View view) {

            super(view);
            check = (ImageView) view.findViewById(R.id.check);
            back = (ImageView) view.findViewById(R.id.back);

        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
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


