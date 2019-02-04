package com.veed.oduchantingapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.EditActivity;
import com.veed.oduchantingapp.EditLineActivity;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.R;
import com.veed.oduchantingapp.RecordActivity;
import com.veed.oduchantingapp.Utils.DrawableUtils;
import com.veed.oduchantingapp.Utils.MyFile;
import com.veed.oduchantingapp.Utils.MyMediaPlayer;
import com.veed.oduchantingapp.Utils.ViewUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.itangqi.waveloadingview.WaveLoadingView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.veed.oduchantingapp.Fragments.EditLinesFragment.EDIT_LINES_ACTIVITY;

/**
 * Created by Saboor Salaam on 10/31/2016.
 */

public class ChantLinesDragAdapter
        extends RecyclerView.Adapter<ChantLinesDragAdapter.MyViewHolder>
        implements DraggableItemAdapter<ChantLinesDragAdapter.MyViewHolder> {
private static final String TAG = "MyDraggableItemAdapter";

// NOTE: Make accessible with short name
private interface Draggable extends DraggableItemConstants {
}

    public List<MyFile> files = new ArrayList<>();
    public Activity parent;
    final MediaPlayer mediaPlayer;
    boolean use_dark_theme;



    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
    public RelativeLayout mContainer;
    //public View mDragHandle;
    public TextView firstTextView;
    public ImageView primaryAction, secondaryAction;
    public AVLoadingIndicatorView waveLoadingView;



    public MyViewHolder(View v) {
        super(v);
        mContainer = (RelativeLayout) v.findViewById(R.id.container);
        //mDragHandle = v.findViewById(R.id.drag_handle);
        firstTextView = (TextView) v.findViewById(R.id.first_text_view);
        primaryAction = (ImageView) v.findViewById(R.id.primary_action);
        secondaryAction = (ImageView) v.findViewById(R.id.secondary_action);
        waveLoadingView = (AVLoadingIndicatorView) v.findViewById(R.id.waveLoadingView);

    }
}

    public ChantLinesDragAdapter(List<MyFile> files, Activity parent ) {

        this.files = files;
        this.parent = parent;
        mediaPlayer = MyMediaPlayer.getInstance(parent);

        for(int i = 0; i < files.size();i++){
                files.get(i).number = i + 1;
        }

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);

        this.use_dark_theme = PreferenceManager.getDefaultSharedPreferences(parent).getBoolean(parent.getResources().getString(R.string.theme_setting), false);

    }

    @Override
    public long getItemId(int position) {
        return files.get(position).number;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_draggable, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        // set text


        holder.secondaryAction.setImageDrawable(new IconicsDrawable(parent)
                .icon(MaterialDesignIconic.Icon.gmi_reorder)
                .color((use_dark_theme ? parent.getResources().getColor(R.color.light_dimmed) : parent.getResources().getColor(R.color.dark_dimmed)))
                .sizeDp(24));

        holder.primaryAction.setImageDrawable(new IconicsDrawable(parent)
                .icon(MaterialDesignIconic.Icon.gmi_info)
                .color((use_dark_theme ? parent.getResources().getColor(R.color.light_dimmed) : parent.getResources().getColor(R.color.dark_dimmed)))
                .actionBar());

        if(files.get(position).text.length() > 2) {
            holder.firstTextView.setText(files.get(position).number +  " " + files.get(position).text);
            holder.firstTextView.setTextColor( use_dark_theme ? Color.parseColor("#ffffff") : Color.parseColor("#000000"));
        }else{
            holder.firstTextView.setText(files.get(position).number +  "  ...");
            holder.firstTextView.setTextColor(use_dark_theme ? Color.parseColor("#757575") : Color.parseColor("#75000000"));
        }

        //holder.mNumber.setText(files.get(position).number + "");

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();


        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            //holder.mContainer.setBackgroundResource(bgResId);
        }


        if(files.get(position).isPlaying){
            holder.waveLoadingView.show();
            holder.primaryAction.setVisibility(View.GONE);
        }else{
            holder.waveLoadingView.hide();
            holder.primaryAction.setVisibility(View.VISIBLE);
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i= 0; i < files.size(); i++){
                    files.get(i).isPlaying = false;
                }

                files.get(position).isPlaying = true;
                notifyDataSetChanged();

                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(files.get(position).filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        files.get(position).isPlaying = false;
                        notifyDataSetChanged();
                    }
                });

                mediaPlayer.start();

            }
        });

        holder.primaryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(parent)
                        .items(new String[]{"Edit", "Delete"})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if(which == 0){ //Edit

                                    MyApplication.current_file = files.get(position);
                                    Intent intent = new Intent(parent, EditLineActivity.class);
                                    parent.getWindow().setExitTransition(new Fade());
                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation(parent);
                                    parent.startActivityForResult(intent, EDIT_LINES_ACTIVITY, options.toBundle());

                                }else if (which == 1){ //Delete
                                    DatabaseHandler.getInstance(parent).deleteChant(files.get(holder.getAdapterPosition()).filename);
                                    files.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());

                                }

                            }
                        })
                        .show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }


        MyFile movedItem = files.remove(fromPosition);
        files.add(toPosition, movedItem);
        mediaPlayer.reset();

        //notifyItemMoved(fromPosition, toPosition);
        //files = Collections.swap(files, , fromPosition);

        //files.add(toPosition, files.remove(fromPosition));

        //notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
       // final View containerView = holder.mContainer;
        //final View dragHandleView = holder.secondaryAction;

        //final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        //final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        //return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }
}