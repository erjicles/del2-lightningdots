package com.delsquared.lightningdots.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing_utilities.IabHelper;
import com.delsquared.lightningdots.billing_utilities.IabResult;
import com.delsquared.lightningdots.billing_utilities.Inventory;
import com.delsquared.lightningdots.billing_utilities.Purchase;

import java.util.ArrayList;

public class PurchaseHelper {

    public interface InterfaceSetupFinishedCallback {
        public void onSetupFinished(boolean success, IabResult result);
    }

    public interface InterfaceQueryInventoryCallback {
        public void onQueryInventoryFinished();
    }

    public interface InterfacePurchaseFinishedCallback {
        public void onPurchaseFinished(Purchase info);
    }

    public static final String PRODUCT_SKU_REMOVE_ADS = "com.delsquared.lightningdots.products.remove_ads";
    public static final int PRODUCT_RC_REQUEST_REMOVE_ADS = 10001;

    Context context;
    private IabHelper iabHelper;
    private boolean iabHelperSetupComplete = false;

    private Inventory inventory;

    private final InterfaceSetupFinishedCallback interfaceSetupFinishedCallback;
    private final InterfaceQueryInventoryCallback interfaceQueryInventoryCallback;
    private final InterfacePurchaseFinishedCallback interfacePurchaseFinishedCallback;

    public PurchaseHelper(
            Context context
            , final InterfaceSetupFinishedCallback interfaceSetupFinishedCallback
            , InterfaceQueryInventoryCallback interfaceQueryInventoryCallback
            , InterfacePurchaseFinishedCallback interfacePurchaseFinishedCallback) {

        this.context = context;

        this.interfaceSetupFinishedCallback = interfaceSetupFinishedCallback;
        this.interfaceQueryInventoryCallback = interfaceQueryInventoryCallback;
        this.interfacePurchaseFinishedCallback = interfacePurchaseFinishedCallback;

        // Create the purchasing helper
        iabHelper = new IabHelper(context, LightningDotsApplication.constructBase64EncodedPublicKey());

        // enable debug logging (for a production application, you should set this to false).
        // TODO: Set this to false
        iabHelper.enableDebugLogging(true);

        // Setup the purchasing helper
        Log.d(LightningDotsApplication.logTag, "Creating IAB helper.");
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    if (!LightningDotsApplication.hasDisplayedUnableToSetUpBillingAlert) {
                        LightningDotsApplication.hasDisplayedUnableToSetUpBillingAlert = true;
                        //complain("Problem setting up in-app billing: " + result);
                        interfaceSetupFinishedCallback.onSetupFinished(false, result);
                    }

                    return;
                }

                // Flag the iab helper setup completed
                iabHelperSetupComplete = true;
                interfaceSetupFinishedCallback.onSetupFinished(true, result);

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(LightningDotsApplication.logTag, "Setup successful. Querying inventory.");
                queryInventory();

            }
        });

    }

    public Inventory getInventory() {
        return inventory;
    }

    public void queryInventory() {

        if (iabHelper == null || !iabHelperSetupComplete) {
            return;
        }

        ArrayList<String> arrayListProductSKU = new ArrayList<>();
        arrayListProductSKU.add(PRODUCT_SKU_REMOVE_ADS);

        iabHelper.queryInventoryAsync(
                true
                , arrayListProductSKU
                , new IabHelper.QueryInventoryFinishedListener() {

            public void onQueryInventoryFinished(IabResult result, Inventory inventoryResult) {

                Log.d(LightningDotsApplication.logTag, "Query inventory finished.");
                if (result.isFailure()) {
                    complain("Failed to query inventory: " + result);
                    return;
                }

                Log.d(LightningDotsApplication.logTag, "Query inventory was successful.");

                // Set the inventory
                inventory = inventoryResult;

                // Process the inventory
                processIntentory();

                // Call the callback
                interfaceQueryInventoryCallback.onQueryInventoryFinished();

            }

        });

    }

    public void processIntentory() {

        if (inventory == null) {
            return;
        }

        // Do we have the remove-ads upgrade?
        LightningDotsApplication.setHasPurchasedNoAds(context, getHasPurchasedNoAds());

        Log.d(LightningDotsApplication.logTag, "User has " + (LightningDotsApplication.hasPurchasedNoAds ? " " : "not ") + "removed ads.");

    }

    public void onDestroy() {
        if (iabHelper != null) {
            if (iabHelperSetupComplete) {
                iabHelper.dispose();
            }
        }
        iabHelper = null;
    }

    void complain(String message) {
        Log.e(LightningDotsApplication.logTag, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(LightningDotsApplication.logTag, "Showing alert dialog: " + message);
        bld.create().show();
    }

    public void launchPurchaseFlow(
            String productSKU
            , int purchaseID) {

        if (iabHelper == null || !iabHelperSetupComplete) {
            return;
        }

        // Launch the purchase flow for removing ads
        iabHelper.launchPurchaseFlow(
                (Activity) context
                , productSKU
                , purchaseID
                , new IabHelper.OnIabPurchaseFinishedListener() {

                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

                        Log.d(LightningDotsApplication.logTag, "Purchase finished: " + result + ", purchase: " + purchase);
                        if (result.isFailure()) {
                            // Oh noes!
                            complain("Error purchasing: " + result);
                            //setWaitScreen(false);
                            return;
                        }

                        Log.d(LightningDotsApplication.logTag, "Purchase successful.");

                        // Process the purchase
                        processSuccessfulPurchase(purchase);

                        // Call the callback
                        interfacePurchaseFinishedCallback.onPurchaseFinished(purchase);

                    }
                });

    }

    public void processSuccessfulPurchase(Purchase purchase) {

        if (purchase == null) {
            return;
        }

        // Get the shared preferences reference
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_file_name), Activity.MODE_PRIVATE);

        // Handle the purchase
        if (purchase.getSku().equals(PRODUCT_SKU_REMOVE_ADS)) {

            // The user bought the remove ads item
            Log.d(LightningDotsApplication.logTag, "Purchase is remove ads. Congratulating user.");
            alert("Thank you for supporting independent developers! Say goodbye to those pesky ads!");
            LightningDotsApplication.setHasPurchasedNoAds(context, true);

        }

    }

    public boolean getHasPurchasedNoAds() {
        boolean result = false;

        // Get the shared preference for removing ads
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_file_name), Activity.MODE_PRIVATE);
        synchronized (LightningDotsApplication.lockSharedPreferences) {
            if (sharedPref.contains(context.getString(R.string.pref_product_remove_ads))) {
                result = sharedPref.getBoolean(context.getString(R.string.pref_product_remove_ads), false);
            }
        }

        result = result || LightningDotsApplication.hasPurchasedNoAds;

        if (inventory != null) {
            result = result || inventory.hasPurchase(PRODUCT_SKU_REMOVE_ADS);
        }

        return result;
    }

}
