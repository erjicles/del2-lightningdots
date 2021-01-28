package com.delsquared.lightningdots.activities;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.fragments.FragmentStore;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivityStore extends FragmentActivity {
    private static final String CLASS_NAME = ActivityStore.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentStore())
                    .commit();
        }

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitystore));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String methodName = CLASS_NAME + ".onCreateOptionsMenu";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String methodName = CLASS_NAME + ".onOptionsItemSelected";
        UtilityFunctions.logDebug(methodName, "Entered");

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

    @Override
    public void onDestroy() {
        String methodName = CLASS_NAME + ".onDestroy";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onDestroy();
    }

}
