package com.delsquared.lightningdots.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing_utilities.BillingHelper;
import com.delsquared.lightningdots.billing_utilities.Constants;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragmentStore extends androidx.fragment.app.Fragment {
    private static final String CLASS_NAME = FragmentStore.class.getSimpleName();

    // The helper for billing
    BillingHelper billingHelper;

    @SuppressWarnings("unused")
    public static FragmentStore newInstance() {
        String methodName = CLASS_NAME + ".newInstance";
        UtilityFunctions.logDebug(methodName, "Entered");

        return new FragmentStore();
    }

    public FragmentStore() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);

        // Get the billing helper instance
        FragmentActivity activity = getActivity();
        if (activity != null) {
            LightningDotsApplication application = (LightningDotsApplication) activity.getApplication();
            this.billingHelper = application.getBillingHelperInstanceAndStartConnection();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreateView";
        UtilityFunctions.logDebug(methodName, "Entered");

        View rootView = inflater.inflate(R.layout.fragment_store, container, false);

        // Set the trademark text color
        FragmentTrademark fragmentTrademark =
                (FragmentTrademark) getChildFragmentManager().findFragmentById(R.id.fragment_main_fragment_trademark);
        if (fragmentTrademark != null) {
            fragmentTrademark.setTextColor(getResources().getColor(R.color.white));
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onViewCreated";
        UtilityFunctions.logDebug(methodName, "Entered");

        this.billingHelper.skusWithSkuDetails.observe(this, skusWithSkuDetails -> {
            UtilityFunctions.logDebug(methodName, "skusWithSkuDetails callback in FragmentStore");
            updateStoreUI();
        });

        this.billingHelper.purchases.observe(this, purchaseList -> {
            UtilityFunctions.logDebug(methodName, "purchases observer callback in FragmentStore");
            updateStoreUI();
        });

        this.billingHelper.billingResponseCode.observe(this, responseCode -> {
            UtilityFunctions.logDebug(methodName, "billingResponseCode observer callback in FragmentStore");
            handleBillingResponseCode(responseCode);
        });

        this.billingHelper.successfulPurchaseObservable.observe(this, purchase -> {
            UtilityFunctions.logDebug(methodName, "FragmentStore: successfulPurchaseSkuObserver callback");
            showThanksToCustomer(purchase);
        });

        Integer billingResponseCode = this.billingHelper.billingResponseCode.getValue();
        if (billingResponseCode != null) {
            handleBillingResponseCode(billingResponseCode);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onActivityCreated";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        String methodName = CLASS_NAME + ".onAttach";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        String methodName = CLASS_NAME + ".onResume";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onResume();
        // Get the billing helper instance
        FragmentActivity activity = getActivity();
        if (activity != null) {
            LightningDotsApplication application = (LightningDotsApplication) activity.getApplication();
            this.billingHelper = application.getBillingHelperInstanceAndStartConnection();
            Integer billingResponseCode = this.billingHelper.billingResponseCode.getValue();
            if (billingResponseCode != null) {
                handleBillingResponseCode(billingResponseCode);
            }
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDetach() {
        String methodName = CLASS_NAME + ".onDetach";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onDetach();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDestroy() {
        String methodName = CLASS_NAME + ".onDestroy";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onDestroy();
    }

    private void handleBillingResponseCode(int responseCode) {
        String methodName = CLASS_NAME + ".handleBillingResponseCode";
        UtilityFunctions.logDebug(methodName, "Entered");

        View rootView = getView();
        if (rootView == null) {
            UtilityFunctions.logError(methodName, "rootView is null", null);
            return;
        }
        TextView textViewBillingUnavailable = rootView.findViewById(R.id.fragment_store_textview_billing_unavailable_text);
        if (textViewBillingUnavailable == null) {
            UtilityFunctions.logError(methodName, "textViewBillingUnavailable is null", null);
            return;
        }

        if (BillingClient.BillingResponseCode.BILLING_UNAVAILABLE == responseCode) {
            textViewBillingUnavailable.setVisibility(View.VISIBLE);
        } else {
            textViewBillingUnavailable.setVisibility(View.GONE);
        }

    }

    private void updateStoreUI() {
        String methodName = CLASS_NAME + ".updateStoreUI";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (billingHelper == null) {
            return;
        }

        Map<String, SkuDetails> mapSkuDetails = billingHelper.skusWithSkuDetails.getValue();
        List<Purchase> listPurchases = billingHelper.purchases.getValue();
        if (listPurchases == null) {
            listPurchases = new ArrayList<>();
        }
        Set<String> hashSetPurchaseSkus = new HashSet<>();
        for (Purchase purchase : listPurchases) {
            hashSetPurchaseSkus.add(purchase.getSku());
        }

        // Get the layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        View view = getView();
        if (view == null) {
            UtilityFunctions.logError(methodName, "view is null", null);
            return;
        }

        // Get the purchased items linear layout
        LinearLayout linearLayoutPurchasedItems = view.findViewById(R.id.fragment_store_linearlayout_purchasedmenu);
        if (linearLayoutPurchasedItems == null) {
            UtilityFunctions.logError(methodName, "linearLayoutPurchasedItems is null", null);
            return;
        }

        // Get the inventory menu linear layout
        LinearLayout linearLayoutInventory = view.findViewById(R.id.fragment_store_linearlayout_inventorymenu);
        if (linearLayoutInventory == null) {
            UtilityFunctions.logError(methodName, "linearLayoutInventory is null", null);
            return;
        }

        // First clear the linear layouts
        linearLayoutPurchasedItems.removeAllViews();
        linearLayoutInventory.removeAllViews();

        if (mapSkuDetails == null) {
            return;
        }

        // Loop through all skus
        // Add to inventory if it hasn't been purchased or otherwise is available
        // Add to purchased list if it's been purchased
        int countAvailableItems = 0;
        int countPurchasedItems = 0;
        for (Map.Entry<String, SkuDetails> entry : mapSkuDetails.entrySet()) {
            String sku = entry.getKey();
            SkuDetails skuDetails = entry.getValue();
            UtilityFunctions.logDebug(methodName, "Processing SKU: " + sku);
            boolean isPurchased = hashSetPurchaseSkus.contains(sku);
            UtilityFunctions.logDebug(methodName, "isPurchased: " + isPurchased);

            LinearLayout linearLayoutMenuToWhichToAddItem;
            View viewItem;
            int countItemsInMenu;

            if (isPurchased) {
                linearLayoutMenuToWhichToAddItem = linearLayoutPurchasedItems;
                countPurchasedItems++;
                countItemsInMenu = countPurchasedItems;

                //  Inflate the view for purchased item
                View viewPurchasedItem = layoutInflater.inflate(
                        R.layout.menu_item_store_purchased,
                        linearLayoutMenuToWhichToAddItem,
                        false);

                // Add the new purchased ads view to the purchased items linear layout
                TextView textViewTitle = viewPurchasedItem.findViewById(R.id.menu_item_store_title);
                TextView textViewDescription = viewPurchasedItem.findViewById(R.id.menu_item_store_description);

                textViewTitle.setText(skuDetails.getTitle());
                textViewDescription.setText(skuDetails.getDescription());

                viewItem = viewPurchasedItem;

            } else {
                linearLayoutMenuToWhichToAddItem = linearLayoutInventory;
                countAvailableItems++;
                countItemsInMenu = countAvailableItems;

                // Inflate the view for available item
                View viewAvailableItem = layoutInflater.inflate(
                        R.layout.menu_item_store_available,
                        linearLayoutMenuToWhichToAddItem,
                        false);

                // Add the new available ads view to the available items linear layout
                TextView textViewTitle = viewAvailableItem.findViewById(R.id.menu_item_store_title);
                TextView textViewDescription = viewAvailableItem.findViewById(R.id.menu_item_store_description);
                TextView textViewPrice = viewAvailableItem.findViewById(R.id.menu_item_store_price);

                textViewTitle.setText(skuDetails.getTitle());
                textViewDescription.setText(skuDetails.getDescription());
                textViewPrice.setText(skuDetails.getPrice());

                viewAvailableItem.setOnClickListener(v -> {
                    UtilityFunctions.logDebug(methodName, "Store item clicked: " + sku);
                    // Launch the purchase flow for this item
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    billingHelper.launchBillingFlow(getActivity(), billingFlowParams);
                });

                viewItem = viewAvailableItem;

            }

            if (countItemsInMenu > 1) {
                UtilityFunctions.logDebug(methodName, "Adding divider LinearLayout");
                LinearLayout viewDivider = (LinearLayout) layoutInflater.inflate(
                        R.layout.menu_item_store_divider,
                        linearLayoutMenuToWhichToAddItem,
                        false);
                linearLayoutMenuToWhichToAddItem.addView(viewDivider);
            }
            linearLayoutMenuToWhichToAddItem.addView(viewItem);
        }
    }

    private void showThanksToCustomer(@NonNull Purchase purchase) {
        String methodName = CLASS_NAME + ".showThanksToCustomer";
        UtilityFunctions.logDebug(methodName, "Entered");

        String sku = purchase.getSku();
        UtilityFunctions.logDebug(methodName, "...for sku: " + sku);
        Context context = getContext();
        if (context == null) {
            UtilityFunctions.logError(methodName, "context is null", null);
            return;
        }

        if (sku.equals(Constants.PRODUCT_SKU_REMOVE_ADS)) {

            // The user bought the remove ads item
            Toast toastThanks = Toast.makeText(context, context.getString(R.string.product_remove_ads_thanks), Toast.LENGTH_SHORT);
            toastThanks.show();

        } else if (sku.equals(Constants.PRODUCT_SKU_SAY_THANKS)) {

            // The user bought the say thanks item
            Toast toastThanks = Toast.makeText(context, context.getString(R.string.product_say_thanks_thanks), Toast.LENGTH_SHORT);
            toastThanks.show();

        } else {
            UtilityFunctions.logWtf(methodName, "Unknown sku: " + sku);
        }

    }

}
