package com.delsquared.lightningdots.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.fragments.FragmentSettings;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivitySettings extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentSettings())
                    .commit();
        }

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitysettings));
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    public void clicked_button_delete_game_history(View view) {
        FragmentSettings fragmentSettings = (FragmentSettings) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentSettings != null) {
            fragmentSettings.clicked_button_delete_game_history();
        }
    }
}
