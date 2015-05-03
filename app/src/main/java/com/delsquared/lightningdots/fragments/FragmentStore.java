package com.delsquared.lightningdots.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing_utilities.IabHelper;
import com.delsquared.lightningdots.billing_utilities.IabResult;
import com.delsquared.lightningdots.billing_utilities.Inventory;
import com.delsquared.lightningdots.billing_utilities.Purchase;
import com.delsquared.lightningdots.billing_utilities.SkuDetails;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.PurchaseHelper;

public class FragmentStore extends android.support.v4.app.Fragment {

    // The helper for purchases
    PurchaseHelper purchaseHelper;

    public static FragmentStore newInstance() {
        FragmentStore fragment = new FragmentStore();

        return fragment;
    }

    public FragmentStore() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Setup the purchase helper
        purchaseHelper = new PurchaseHelper(
                getActivity()
                , new PurchaseHelper.InterfaceSetupFinishedCallback() {

                    @Override
                    public void onSetupFinished(boolean success) {

                    }

                }
                , new PurchaseHelper.InterfaceQueryInventoryCallback() {

                    @Override
                    public void onQueryInventoryFinished() {
                        updateUI();
                    }
                }
                , new PurchaseHelper.InterfacePurchaseFinishedCallback() {

                    @Override
                    public void onPurchaseFinished(Purchase purchase) {
                        updateUI();
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (purchaseHelper != null) {
            purchaseHelper.onDestroy();
        }
        purchaseHelper = null;
    }

    public void updateUI() {

        if (purchaseHelper == null) {
            return;
        }

        // Get the inventory
        Inventory inventory = purchaseHelper.getInventory();

        if (inventory == null) {
            return;
        }

        // Get the layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        // Get the purchased items linear layout
        LinearLayout linearLayoutPurchasedItems = (LinearLayout) getView().findViewById(R.id.fragment_store_linearlayout_purchasedmenu);

        // Get the inventory menu linear layout
        LinearLayout linearLayoutInventory = (LinearLayout) getView().findViewById(R.id.fragment_store_linearlayout_inventorymenu);

        // ---------- BEGIN Handle Item Remove Ads ---------- //
        try {

            SkuDetails skuDetailsRemoveAds = inventory.getSkuDetails(PurchaseHelper.PRODUCT_SKU_REMOVE_ADS);
            boolean hasPurchaseRemoveAds = LightningDotsApplication.hasPurchasedNoAds;
            String titleRemoveAds = skuDetailsRemoveAds.getTitle();
            String descriptionRemoveAds = skuDetailsRemoveAds.getDescription();
            String priceRemoveAds = skuDetailsRemoveAds.getPrice();
            if (hasPurchaseRemoveAds) {

                // Inflate the view for purchased ads
                View purchasedAdsView = layoutInflater.inflate(R.layout.menu_item_store_purchased, null);

                // Add the new purchased ads view to the purchased items linear layout
                if (linearLayoutPurchasedItems != null) {

                    TextView textViewTitle = (TextView) purchasedAdsView.findViewById(R.id.menu_item_store_title);
                    TextView textViewDescription = (TextView) purchasedAdsView.findViewById(R.id.menu_item_store_description);

                    textViewTitle.setText(titleRemoveAds);
                    textViewDescription.setText(descriptionRemoveAds);

                    linearLayoutPurchasedItems.addView(purchasedAdsView);
                }

            } else {

                // Inflate the view for non-purchased ads
                View availableAdsView = layoutInflater.inflate(R.layout.menu_item_store_available, null);

                // Add the new available ads view to the purchased items linear layout
                if (linearLayoutInventory != null) {

                    TextView textViewTitle = (TextView) availableAdsView.findViewById(R.id.menu_item_store_title);
                    TextView textViewDescription = (TextView) availableAdsView.findViewById(R.id.menu_item_store_description);
                    TextView textViewPrice = (TextView) availableAdsView.findViewById(R.id.menu_item_store_price);

                    textViewTitle.setText(R.string.product_remove_ads_title);
                    textViewDescription.setText(R.string.product_remove_ads_description);
                    textViewPrice.setText(R.string.product_remove_ads_price);

                    linearLayoutInventory.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v)
                        {

                            // Launch the purchase flow for removing ads
                            purchaseHelper.launchPurchaseFlow(
                                    PurchaseHelper.PRODUCT_SKU_REMOVE_ADS
                                    , PurchaseHelper.PRODUCT_RC_REQUEST_REMOVE_ADS);

                        }

                    });

                    linearLayoutInventory.addView(availableAdsView);

                }

            }

        } catch (Exception e) {

        }
        // ---------- END Handle Item Remove Ads ---------- //

    }

}
