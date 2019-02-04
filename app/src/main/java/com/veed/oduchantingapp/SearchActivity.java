package com.veed.oduchantingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.veed.oduchantingapp.Adapters.HomeRecyclerViewAdapter;
import com.veed.oduchantingapp.Adapters.SearchRecyclerViewAdapter;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.Utils.AnimUtils;
import com.veed.oduchantingapp.Utils.AsyncError;
import com.veed.oduchantingapp.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import tr.xip.errorview.ErrorView;

public class SearchActivity extends AppCompatActivity implements SearchRecyclerViewAdapter.ClickListener {

    MenuItem clear_search;
    MaterialEditText editText;
    UltimateRecyclerView recyclerView;
    RelativeLayout root;
    LinearLayout loading_view;
    String current_query = "";
    boolean clear_search_visible = false, stop_loading_abruptly = false;
    List<String> categoryList =  new ArrayList<>();
    ErrorView mErrorView;
    boolean use_dark_theme;


    List<Chant> matched_chants = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);

        if (!use_dark_theme) {
            setTheme(R.style.LightTheme_SearchActivity);
        } else {
            setTheme(R.style.DarkTheme_SearchActivity);
        }


        super.onCreate(savedInstanceState);

        //***********************************
        //SharedPreferences.Editor editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        //editor.putBoolean(Utils.PREFS_INTO_COMPLETED, false);
        //editor.commit();
        //***********************************


        setContentView(R.layout.activity_search);


        mErrorView = (ErrorView) findViewById(R.id.error_view);
        mErrorView.setVisibility(View.GONE);

        mErrorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                performSearch();
            }
        });


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        root = (RelativeLayout) findViewById(R.id.root);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clear_search_visible) {
                    finish();
                }
            }
        });


        loading_view = (LinearLayout) findViewById(R.id.loading_view);
        loading_view.setVisibility(View.GONE);//Loading view starts off invisible

        recyclerView = (UltimateRecyclerView) findViewById(R.id.recyclerview);
        recyclerView.hideEmptyView(); //EmptyView is invisible


        editText = (MaterialEditText) findViewById(R.id.editSearch);
        editText.requestFocus();


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    if (clear_search_visible) {
                        clear_search_visible = false;

                        if(clear_search !=null) clear_search.setVisible(false);

                        root.setBackgroundColor((use_dark_theme ? Color.parseColor("#A6000000") : Color.parseColor("#B1FFFFFF")));
                        recyclerView.setVisibility(View.GONE);
                        loading_view.setVisibility(View.GONE);
                        current_query = "";
                        stop_loading_abruptly = true;
                    }
                }else if (!clear_search_visible) {
                    clear_search_visible = true;

                    if(clear_search != null) clear_search.setVisible(true);

                    root.setBackgroundColor((use_dark_theme ? Color.parseColor("#000000") : Color.parseColor("#FFFFFF")));

                }
            }
        });

//DOWNLOAD ALL VIDEOS AND SEARCH THROUGH THEM!!!!!!!!!!!!!!!!!! SO WE DONT HAVE TO DO CONSTANT SEARCHES

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(!editText.getText().toString().equals(current_query)) {

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        performSearch();
                        stop_loading_abruptly = false;

                    }


                    handled = true;
                }
                return handled;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);


        menu.findItem(R.id.clear_search).setIcon(  new IconicsDrawable(this)
        .icon(MaterialDesignIconic.Icon.gmi_close).color(Color.WHITE).sizeDp(18));

        clear_search = menu.findItem(R.id.clear_search);
        clear_search.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(item.getItemId() == R.id.clear_search)
        {
            editText.getText().clear();
        }

        if(id == android.R.id.home)
        {
            AnimUtils.hide(loading_view);
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void performSearch(){

        current_query = editText.getText().toString();

        if(current_query.length() >= 3) {

            new AsyncTask<Void, Void, String>() {

                AsyncError error = new AsyncError(false, 0);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    matched_chants = new ArrayList<>();

                    AnimUtils.hide(recyclerView);
                    AnimUtils.show(loading_view);

                }

                @Override
                protected String doInBackground(Void... params) {

                    MyApplication.all_chants = DatabaseHandler.getInstance(SearchActivity.this).getAllChants();

                    List<String> words = Utils.extractWordsFromQuery(current_query);

                    for (int i = 0; i < MyApplication.all_chants.size(); i++) {
                        for (int z = 0; z < words.size(); z++) {
                            if (MyApplication.all_chants.get(i).name.toLowerCase().trim().contains(words.get(z).toLowerCase().trim())) {
                                boolean found = false;
                                for(int y = 0; y < matched_chants.size(); y++){
                                    if(matched_chants.get(y).id.equals(MyApplication.all_chants.get(i).id)){
                                        found = true;
                                    }
                                }
                                if(!found)matched_chants.add(MyApplication.all_chants.get(i));
                            }

                            for(int x = 0; x < MyApplication.all_chants.get(i).tags.size(); x++) {
                                if (MyApplication.all_chants.get(i).tags.get(x).toLowerCase().trim().contains(words.get(z).toLowerCase().trim())) {
                                    boolean found = false;
                                    for(int y = 0; y < matched_chants.size(); y++){
                                        if(matched_chants.get(y).id.equals(MyApplication.all_chants.get(i).id)){
                                            found = true;
                                        }
                                    }
                                    if(!found)matched_chants.add(MyApplication.all_chants.get(i));
                                }
                            }
                        }
                    }


                    if(error.cancel){
                        cancel(true);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String s) {

                    if(!stop_loading_abruptly) {

                        recyclerView.setAdapter(new SearchRecyclerViewAdapter(recyclerView, matched_chants, SearchActivity.this, SearchActivity.this));

                        recyclerView.addItemDecoration(new ItemOffsetDecoration(SearchActivity.this, R.dimen.list_divider));

                        final GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 2);
                        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                switch(recyclerView.getAdapter().getItemViewType(position)){
                                    case SearchRecyclerViewAdapter.VIEW_TYPE_HEADER:
                                        return gridLayoutManager.getSpanCount();
                                    case SearchRecyclerViewAdapter.RESULT_HEADER:
                                        return gridLayoutManager.getSpanCount();
                                    case SearchRecyclerViewAdapter.CHANT_TYPE:
                                        return 1;
                                    default:
                                        return -1;
                                }
                            }
                        });

                        recyclerView.setLayoutManager(gridLayoutManager);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                AnimUtils.show(recyclerView);
                                AnimUtils.hide(loading_view);

                            }
                        }, 500);

                    }

                    super.onPostExecute(s);
                }

                @Override
                protected void onCancelled() {
                    AnimUtils.hide(loading_view);
                    AnimUtils.hide(recyclerView);
                    AnimUtils.show(mErrorView);

                    super.onCancelled();
                }

            }.execute();

        }else{
            Toast.makeText(SearchActivity.this, "Search query must be alteast 3 characters long", Toast.LENGTH_SHORT).show();
        }

    }

    boolean more_shown = false;


    @Override
    public void onBackPressed() {
        if(more_shown){
            AnimUtils.show(recyclerView);
            AnimUtils.hide(loading_view);
            more_shown = false;
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(int index, View v) {
            MyApplication.current_chant = ((SearchRecyclerViewAdapter) recyclerView.getAdapter()).chants.get(index - 1);
            Intent intent = new Intent(this, ChantCoverActivity.class);
            getWindow().setExitTransition(new Fade());
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this);
            startActivityForResult(intent, 0 , options.toBundle());
    }

    @Override
    public void onLongClick(int index) {

    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
