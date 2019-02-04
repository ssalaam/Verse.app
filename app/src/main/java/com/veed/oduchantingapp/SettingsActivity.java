package com.veed.oduchantingapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.veed.oduchantingapp.Fragments.SettingsFragment;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    public boolean should_refresh_home_activity = false;
    public boolean use_dark_theme = false;


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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        use_dark_theme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.theme_setting), false);

        if (!use_dark_theme) {
            setTheme(R.style.LightSettingsTheme);
        } else{
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_settings);


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, new SettingsFragment())
                .commit();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            Intent returnIntent = new Intent();
            returnIntent.putExtra("should_refresh",should_refresh_home_activity);
            setResult(RESULT_OK,returnIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

}
