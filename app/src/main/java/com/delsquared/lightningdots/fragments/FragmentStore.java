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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragmentStore extends androidx.fragment.app.Fragment {

    // The helper for billing
    BillingHelper billingHelper;

    @SuppressWarnings("unused")
    public static FragmentStore newInstance() {

        return new FragmentStore();
    }

    public FragmentStore() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        this.billingHelper.skusWithSkuDetails.observe(this, skusWithSkuDetails -> {
            LightningDotsApplication.logDebugMessage("skusWithSkuDetails callback in FragmentStore");
            updateStoreUI();
        });

        this.billingHelper.purchases.observe(this, purchaseList -> {
            LightningDotsApplication.logDebugMessage("purchases observer callback in FragmentStore");
            updateStoreUI();
        });

        this.billingHelper.billingResponseCode.observe(this, responseCode -> {
            LightningDotsApplication.logDebugMessage("billingResponseCode observer callback in FragmentStore");
            handleBillingResponseCode(responseCode);
        });

        this.billingHelper.successfulPurchaseObservable.observe(this, purchase -> {
            LightningDotsApplication.logDebugMessage("FragmentStore: successfulPurchaseSkuObserver callback");
            showThanksToCustomer(purchase);
        });

        handleBillingResponseCode(this.billingHelper.billingResponseCode.getValue());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get the billing helper instance
        FragmentActivity activity = getActivity();
        if (activity != null) {
            LightningDotsApplication application = (LightningDotsApplication) activity.getApplication();
            this.billingHelper = application.getBillingHelperInstanceAndStartConnection();
            handleBillingResponseCode(this.billingHelper.billingResponseCode.getValue());
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleBillingResponseCode(int responseCode) {
        View rootView = getView();
        if (rootView == null) {
            LightningDotsApplication.logDebugErrorMessage("rootView is null");
            return;
        }
        TextView textViewBillingUnavailable = rootView.findViewById(R.id.fragment_store_textview_billing_unavailable_text);
        if (textViewBillingUnavailable == null) {
            LightningDotsApplication.logDebugErrorMessage("textViewBillingUnavailable is null");
            return;
        }

        if (BillingClient.BillingResponseCode.BILLING_UNAVAILABLE == responseCode) {
            textViewBillingUnavailable.setVisibility(View.VISIBLE);
        } else {
            textViewBillingUnavailable.setVisibility(View.GONE);
        }

    }

    private void updateStoreUI() {
        if (billingHelper == null) {
            return;
        }
        LightningDotsApplication.logDebugMessage("updateStoreUI()");

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
            LightningDotsApplication.logDebugErrorMessage("view is null");
            return;
        }

        // Get the purchased items linear layout
        LinearLayout linearLayoutPurchasedItems = view.findViewById(R.id.fragment_store_linearlayout_purchasedmenu);
        if (linearLayoutPurchasedItems == null) {
            LightningDotsApplication.logDebugErrorMessage("linearLayoutPurchasedItems is null");
            return;
        }

        // Get the inventory menu linear layout
        LinearLayout linearLayoutInventory = view.findViewById(R.id.fragment_store_linearlayout_inventorymenu);
        if (linearLayoutInventory == null) {
            LightningDotsApplication.logDebugErrorMessage("linearLayoutInventory is null");
            return;
        }

        // First clear the linear layouts
        if (linearLayoutPurchasedItems != null) {
            linearLayoutPurchasedItems.removeAllViews();
        }
        if (linearLayoutInventory != null) {
            linearLayoutInventory.removeAllViews();
        }

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
            LightningDotsApplication.logDebugMessage("Processing SKU: " + sku);
            boolean isPurchased = hashSetPurchaseSkus.contains(sku);
            LightningDotsApplication.logDebugMessage("isPurchased: " + isPurchased);

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
                    LightningDotsApplication.logDebugMessage("Store item clicked: " + sku);
                    // Launch the purchase flow for this item
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    billingHelper.launchBillingFlow(getActivity(), billingFlowParams);
                });

                viewItem = viewAvailableItem;

            }

            if (linearLayoutMenuToWhichToAddItem == null) {
                LightningDotsApplication.logDebugErrorMessage("linearLayoutMenuToWhichToAddItem is null");
                continue;
            }
            if (viewItem == null) {
                LightningDotsApplication.logDebugErrorMessage("viewItem is null");
                continue;
            }
            if (countItemsInMenu > 1) {
                LightningDotsApplication.logDebugMessage("Adding divider view");
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
        String sku = purchase.getSku();
        LightningDotsApplication.logDebugMessage("Showing thanks to customer: " + sku);
        Context context = getContext();
        if (context == null) {
            LightningDotsApplication.logDebugErrorMessage("context is null");
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
            LightningDotsApplication.logDebugErrorMessage("Unknown sku: " + sku);
        }

    }

}
