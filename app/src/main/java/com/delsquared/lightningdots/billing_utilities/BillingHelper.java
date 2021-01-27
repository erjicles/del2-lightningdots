package com.delsquared.lightningdots.billing_utilities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to interact with BillingClient and facilitate in-app purchases
 * Code inspired by these resources:
 * https://developer.android.com/google/play/billing/integrate
 * https://medium.com/better-programming/how-to-implement-in-app-purchases-in-your-android-app-7cc1f80148a4
 */
public class BillingHelper implements
        BillingClientStateListener,
        SkuDetailsResponseListener,
        PurchasesUpdatedListener,
        ConsumeResponseListener,
        AcknowledgePurchaseResponseListener {
    public static final String LOG_TAG = BillingHelper.class.getName();

    // Values for API requests
    private final int exponentialBackoffDelayMsDefault = 1000;
    private int numberOfConnectionAttempts = 0;
    private int exponentialBackoffDelayMs = exponentialBackoffDelayMsDefault;

    private final BillingClient billingClient;

    private volatile boolean isStartingConnection = false;

    /**
     * The billing response code is observable. The value will be updated when the Billing Library
     * onBillingSetupFinished function is called. All observers will be notified.
     */
    public final MutableLiveData<Integer> billingResponseCode = new MutableLiveData<>();

    /**
     * Purchases are observable. This list will be updated when the Billing Library
     * detects new or existing purchases. All observers will be notified.
     */
    public final MutableLiveData<List<Purchase>> purchases = new MutableLiveData<>();

    /**
     * SkuDetails for all known SKUs.
     */
    public final MutableLiveData<Map<String, SkuDetails>> skusWithSkuDetails = new MutableLiveData<>();

    /**
     * Observable of successful purchases.
     * Updated when entitlement is given. All observers will be notified.
     */
    public final MutableLiveData<Purchase> successfulPurchaseObservable = new MutableLiveData<>();

    // Singleton instance of this class
    private static volatile BillingHelper INSTANCE;

    public BillingHelper(@NonNull Context context) {

        // Create the billing client
        this.billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        startConnection();
    }

    public static BillingHelper getInstance(Application app) {
        if (INSTANCE == null) {
            synchronized (BillingHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BillingHelper(app);
                }
            }
        }
        return INSTANCE;
    }

    public void startConnection() {
        synchronized (this) {
            if (!isStartingConnection
                && !billingClient.isReady()) {
                isStartingConnection = true;
                this.numberOfConnectionAttempts++;
                LightningDotsApplication.logDebugMessage("Starting billingClient connection (attempt " + numberOfConnectionAttempts + ")...");
                billingClient.startConnection(this);
            }
        }
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        isStartingConnection = false;
        int responseCode = billingResult.getResponseCode();
        this.billingResponseCode.postValue(responseCode);
        String debugMessage = billingResult.getDebugMessage();
        LightningDotsApplication.logDebugMessage("onBillingSetupFinished: " + responseCode + " " + debugMessage);
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                LightningDotsApplication.logDebugMessage("billing response OK");
                // Query SKU details and purchases
                querySkuDetails();
                queryPurchases();
                break;
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                LightningDotsApplication.logDebugErrorMessage("Billing unavailable");
                break;
            default:
                LightningDotsApplication.logDebugErrorMessage("Default switch case");
        }

    }

    @Override
    public void onBillingServiceDisconnected() {
        // Try to start a connection again using exponential backoff
        // Algorithm inspired by this article:
        // https://cloud.google.com/iot/docs/how-tos/exponential-backoff
        // Scheduler code inspired by this article:
        // https://www.codejava.net/java-core/concurrency/java-concurrency-scheduling-tasks-to-execute-after-a-given-delay-or-periodically
        isStartingConnection = false;
        ScheduledExecutorService scheduler
                = Executors.newSingleThreadScheduledExecutor();
        Runnable task = this::startConnection;

        int randomDelayOffset = (int)(Math.random() * 1000);
        exponentialBackoffDelayMs *= 2;
        int exponentialBackoffDelayMsMaximum = 120000;
        if (exponentialBackoffDelayMs > exponentialBackoffDelayMsMaximum) {
            exponentialBackoffDelayMs = exponentialBackoffDelayMsMaximum;
        }
        int delayMs = exponentialBackoffDelayMs + randomDelayOffset;
        scheduler.schedule(task, delayMs, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
        LightningDotsApplication.logDebugErrorMessage("billing service disconnected, attempting to reconnect in " + delayMs + "ms");
    }

    /**
     * In order to make purchases, you need the {@link SkuDetails} for the item or subscription.
     * This is an asynchronous call that will receive a result in {@link #onSkuDetailsResponse}.
     */
    private void querySkuDetails() {
        Log.d(LOG_TAG, "querySkuDetails");

        List<String> skus = new ArrayList<>();
        skus.add(Constants.PRODUCT_SKU_SAY_THANKS);
        skus.add(Constants.PRODUCT_SKU_REMOVE_ADS);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setType(BillingClient.SkuType.INAPP)
                .setSkusList(skus)
                .build();

        Log.i(LOG_TAG, "querySkuDetailsAsync");
        billingClient.querySkuDetailsAsync(params, this);
    }

    @Override
    public void onSkuDetailsResponse(
            @NonNull BillingResult billingResult,
            @Nullable List<SkuDetails> skuDetailsList) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                Log.i(LOG_TAG, "onSkuDetailsResponse: " + responseCode + " " + debugMessage);
                if (skuDetailsList == null) {
                    Log.w(LOG_TAG, "onSkuDetailsResponse: null SkuDetails list");
                    skusWithSkuDetails.postValue(Collections.emptyMap());
                } else {
                    Map<String, SkuDetails> newSkusDetailList = new HashMap<>();
                    for (SkuDetails skuDetails : skuDetailsList) {
                        newSkusDetailList.put(skuDetails.getSku(), skuDetails);
                    }
                    skusWithSkuDetails.postValue(newSkusDetailList);
                    Log.i(LOG_TAG, "onSkuDetailsResponse: count " + newSkusDetailList.size());
                }
                break;
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
            case BillingClient.BillingResponseCode.ERROR:
                Log.e(LOG_TAG, "onSkuDetailsResponse: " + responseCode + " " + debugMessage);
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                Log.i(LOG_TAG, "onSkuDetailsResponse: " + responseCode + " " + debugMessage);
                break;
            // These response codes are not expected.
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
            default:
                Log.wtf(LOG_TAG, "onSkuDetailsResponse: " + responseCode + " " + debugMessage);
        }
    }

    /**
     * Query Google Play Billing for existing purchases.
     * <p>
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    private void queryPurchases() {
        if (!billingClient.isReady()) {
            Log.e(LOG_TAG, "queryPurchases: BillingClient is not ready");
        }
        Log.d(LOG_TAG, "queryPurchases: SUBS");
        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchaseList = result.getPurchasesList();
        if (purchaseList == null) {
            Log.i(LOG_TAG, "queryPurchases: null purchase list");
            processPurchases(null);
            this.purchases.postValue(null);
        } else {
            processPurchases(purchaseList);
            this.purchases.postValue(purchaseList);
        }
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    @Override
    public void onPurchasesUpdated(
            @NonNull BillingResult billingResult,
            @Nullable List<Purchase> purchases) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        Log.d(LOG_TAG, "onPurchasesUpdated: " + responseCode + "; " + debugMessage);
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                if (purchases == null) {
                    Log.d(LOG_TAG, "onPurchasesUpdated: null purchase list");
                    processPurchases(null);
                } else {
                    processPurchases(purchases);
                }
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                Log.i(LOG_TAG, "onPurchasesUpdated: User canceled the purchase");
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                Log.i(LOG_TAG, "onPurchasesUpdated: The user already owns this item");
                break;
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                Log.e(LOG_TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                        "does not recognize the configuration. If you are just getting started, " +
                        "make sure you have configured the application correctly in the " +
                        "Google Play Console. The SKU product ID must match and the APK you " +
                        "are using must be signed with release keys."
                );
                break;
        }
    }

    private void processPurchases(List<Purchase> purchasesList) {
        if (purchasesList == null) {
            Log.d(LOG_TAG, "processPurchases: with no purchases");
            return;
        }
        Log.d(LOG_TAG, "processPurchases: " + purchasesList.size() + " purchase(s)");

        // Process each purchase
        for (Purchase purchase : purchasesList) {
            processPurchase(purchase);
        }
        logAcknowledgementStatus(purchasesList);
    }

    private void processPurchase(Purchase purchase) {
        String sku = purchase.getSku();
        LightningDotsApplication.logDebugMessage("Processing purchase: " + sku);

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // TODO: Verify validity:
            // https://developer.android.com/google/play/billing/integrate#process

            if (sku.equals(Constants.PRODUCT_SKU_REMOVE_ADS)) {
                processNonConsumablePurchase(purchase);
            } else if (sku.equals(Constants.PRODUCT_SKU_SAY_THANKS)) {
                processConsumablePurchase(purchase);
            }
        }

    }

    private void processConsumablePurchase(Purchase purchase) {
        String sku = purchase.getSku();
        LightningDotsApplication.logDebugMessage("Consuming purchase: " + sku);
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            grantPurchaseEntitlement(purchase);
            successfulPurchaseObservable.postValue(purchase);

            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            billingClient.consumeAsync(consumeParams, this);
        }
    }

    @Override
    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
        LightningDotsApplication.logDebugMessage("onConsumeResponse");
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            // Handle the success of the consume operation.
            LightningDotsApplication.logDebugMessage("...successful.");
            // Refresh the list of purchases
            queryPurchases();
        } else {
            LightningDotsApplication.logDebugErrorMessage("...not successful: " + billingResult.getResponseCode());
        }
    }

    private void processNonConsumablePurchase(Purchase purchase) {
        String sku = purchase.getSku();
        LightningDotsApplication.logDebugMessage("Processing non-consumable purchase: " + sku);

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            grantPurchaseEntitlement(purchase);
            if (!purchase.isAcknowledged()) {
                successfulPurchaseObservable.postValue(purchase);
                acknowledgePurchase(purchase);
            }
        }
    }

    /**
     * Log the number of purchases that are acknowledge and not acknowledged.
     * <p>
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     * <p>
     * When the purchase is first received, it will not be acknowledge.
     * This application sends the purchase token to the server for registration. After the
     * purchase token is registered to an account, the Android app acknowledges the purchase token.
     * The next time the purchase list is updated, it will contain acknowledged purchases.
     */
    private void logAcknowledgementStatus(List<Purchase> purchasesList) {
        if (purchasesList == null) {
            Log.d(LOG_TAG, "logAcknowledgementStatus: purchaseList is null");
            return;
        }
        int ack_yes = 0;
        int ack_no = 0;
        for (Purchase purchase : purchasesList) {
            if (purchase.isAcknowledged()) {
                ack_yes++;
            } else {
                ack_no++;
            }
        }
        Log.d(LOG_TAG, "logAcknowledgementStatus: acknowledged=" + ack_yes +
                " unacknowledged=" + ack_no);
    }

    /**
     * Launching the billing flow.
     * <p>
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    @SuppressWarnings("UnusedReturnValue")
    public BillingResult launchBillingFlow(Activity activity, BillingFlowParams params) {
        String sku = params.getSku();
        String oldSku = params.getOldSku();
        Log.i(LOG_TAG, "launchBillingFlow: sku: " + sku + ", oldSku: " + oldSku);
        if (!billingClient.isReady()) {
            Log.e(LOG_TAG, "launchBillingFlow: BillingClient is not ready");
        }
        BillingResult billingResult = billingClient.launchBillingFlow(activity, params);
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        Log.d(LOG_TAG, "launchBillingFlow: BillingResponse " + responseCode + " " + debugMessage);
        return billingResult;
    }

    /**
     * Acknowledge a purchase.
     * <p>
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     * <p>
     * Apps should acknowledge the purchase after confirming that the purchase token
     * has been associated with a user. This app only acknowledges purchases after
     * successfully receiving the subscription data back from the server.
     * <p>
     * Developers can choose to acknowledge purchases from a server using the
     * Google Play Developer API. The server has direct access to the user database,
     * so using the Google Play Developer API for acknowledgement might be more reliable.
     * TODO(134506821): Acknowledge purchases on the server.
     * <p>
     * If the purchase token is not acknowledged within 3 days,
     * then Google Play will automatically refund and revoke the purchase.
     * This behavior helps ensure that users are not charged for subscriptions unless the
     * user has successfully received access to the content.
     * This eliminates a category of issues where users complain to developers
     * that they paid for something that the app is not giving to them.
     */
    private void acknowledgePurchase(Purchase purchase) {
        Log.d(LOG_TAG, "acknowledgePurchase");
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.acknowledgePurchase(params, this);
    }

    @Override
    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        Log.d(LOG_TAG, "acknowledgePurchase: " + responseCode + " " + debugMessage);
        // Refresh the list of purchases
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            queryPurchases();
        }
    }

    private void grantPurchaseEntitlement(Purchase purchase) {
        String sku = purchase.getSku();
        LightningDotsApplication.logDebugMessage("Granting purchase entitlement: " + sku);

        if (sku.equals(Constants.PRODUCT_SKU_REMOVE_ADS)) {

            // The user bought the remove ads item
            LightningDotsApplication.setHasPurchasedNoAds(true);

        } else //noinspection StatementWithEmptyBody
            if (sku.equals(Constants.PRODUCT_SKU_SAY_THANKS)) {

            // The user bought the say thanks item
            // TODO: create some sort of prize for this

        } else {
            LightningDotsApplication.logDebugErrorMessage("Unknown sku: " + sku);
        }
    }

}
