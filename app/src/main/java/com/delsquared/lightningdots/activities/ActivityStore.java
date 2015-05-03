package com.delsquared.lightningdots.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing_utilities.IabHelper;
import com.delsquared.lightningdots.billing_utilities.IabResult;
import com.delsquared.lightningdots.billing_utilities.Inventory;
import com.delsquared.lightningdots.billing_utilities.Purchase;
import com.delsquared.lightningdots.fragments.FragmentStore;
import com.delsquared.lightningdots.fragments.FragmentTrademark;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;

public class ActivityStore extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_store, menu);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //purchaseHelper.onDestroy();
    }

    public void onMakePurchase() {

    }

    public void queryPurhcases() {


    }

}
