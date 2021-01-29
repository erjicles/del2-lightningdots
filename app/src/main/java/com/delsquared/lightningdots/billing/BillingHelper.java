package com.delsquared.lightningdots.billing;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

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
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

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
    private static final String CLASS_NAME = BillingHelper.class.getSimpleName();

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
        String methodName = CLASS_NAME + ".constructor";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Create the billing client
        this.billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        startConnection();
    }

    public static BillingHelper getInstance(Application app) {
        String methodName = CLASS_NAME + ".getInstance";
        UtilityFunctions.logDebug(methodName, "Entered");

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
        String methodName = CLASS_NAME + ".startConnection";
        UtilityFunctions.logDebug(methodName, "Entered");

        synchronized (this) {
            if (!isStartingConnection
                && !billingClient.isReady()) {
                isStartingConnection = true;
                this.numberOfConnectionAttempts++;
                UtilityFunctions.logDebug(methodName, "Starting billingClient connection (attempt " + numberOfConnectionAttempts + ")...");
                billingClient.startConnection(this);
            }
        }
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        String methodName = CLASS_NAME + ".onBillingSetupFinished";
        UtilityFunctions.logDebug(methodName, "Entered");

        isStartingConnection = false;
        int responseCode = billingResult.getResponseCode();
        this.billingResponseCode.postValue(responseCode);
        String debugMessage = billingResult.getDebugMessage();
        UtilityFunctions.logInfo(methodName, "ResponseCode: " + responseCode + "; debugMessage: " + debugMessage);
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                UtilityFunctions.logDebug(methodName, "...billing response: OK");
                // Query SKU details and purchases
                querySkuDetails();
                queryPurchases();
                break;
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                UtilityFunctions.logInfo(methodName, "...billing response: Billing unavailable");
                break;
            default:
                UtilityFunctions.logDebug(methodName, "...default switch case");
        }

    }

    @Override
    public void onBillingServiceDisconnected() {
        String methodName = CLASS_NAME + ".onBillingServiceDisconnected";
        UtilityFunctions.logDebug(methodName, "Entered");

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
        UtilityFunctions.logInfo(methodName, "Attempting to reconnect in " + delayMs + "ms");
    }

    /**
     * In order to make purchases, you need the {@link SkuDetails} for the item or subscription.
     * This is an asynchronous call that will receive a result in {@link #onSkuDetailsResponse}.
     */
    private void querySkuDetails() {
        String methodName = CLASS_NAME + ".querySkuDetails";
        UtilityFunctions.logDebug(methodName, "Entered");

        List<String> skus = new ArrayList<>();
        skus.add(BillingConstants.PRODUCT_SKU_SAY_THANKS);
        skus.add(BillingConstants.PRODUCT_SKU_REMOVE_ADS);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setType(BillingClient.SkuType.INAPP)
                .setSkusList(skus)
                .build();

        UtilityFunctions.logInfo(methodName, "Calling querySkuDetailsAsync");
        billingClient.querySkuDetailsAsync(params, this);
    }

    @Override
    public void onSkuDetailsResponse(
            @NonNull BillingResult billingResult,
            @Nullable List<SkuDetails> skuDetailsList) {
        String methodName = CLASS_NAME + ".onSkuDetailsResponse";
        UtilityFunctions.logDebug(methodName, "Entered");

        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        UtilityFunctions.logInfo(methodName, "ResponseCode: " + responseCode + "; debugMessage: " + debugMessage);
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                UtilityFunctions.logInfo(methodName, "Billing response: OK");
                if (skuDetailsList == null) {
                    UtilityFunctions.logWarning(methodName, "...skuDetailsList is null");
                    skusWithSkuDetails.postValue(Collections.emptyMap());
                } else {
                    UtilityFunctions.logDebug(methodName, "...creating new detail list to post...");
                    Map<String, SkuDetails> newSkusDetailList = new HashMap<>();
                    for (SkuDetails skuDetails : skuDetailsList) {
                        String sku = skuDetails.getSku();
                        newSkusDetailList.put(sku, skuDetails);
                        UtilityFunctions.logDebug(methodName, "......adding sku: " + sku);
                    }
                    skusWithSkuDetails.postValue(newSkusDetailList);
                    UtilityFunctions.logInfo(methodName, "...skuDetailsList count: " + newSkusDetailList.size());
                }
                break;
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
            case BillingClient.BillingResponseCode.ERROR:
                UtilityFunctions.logError(methodName, "Received error response: " + debugMessage, null);
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                UtilityFunctions.logInfo(methodName, "User cancelled response");
                break;
            // These response codes are not expected.
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
            default:
                UtilityFunctions.logWtf(methodName, "Should never receive this sku details response");
        }
    }

    /**
     * Query Google Play Billing for existing purchases.
     * <p>
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    private void queryPurchases() {
        String methodName = CLASS_NAME + ".queryPurchases";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (!billingClient.isReady()) {
            UtilityFunctions.logError(methodName, "billingClient is not ready", null);
            return;
        }
        UtilityFunctions.logInfo(methodName, "Calling queryPurchases: INAPP");
        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchaseList = result.getPurchasesList();
        if (purchaseList == null) {
            UtilityFunctions.logInfo(methodName, "...purchaseList is null");
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
        String methodName = CLASS_NAME + ".onPurchasesUpdated";
        UtilityFunctions.logDebug(methodName, "Entered");

        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        UtilityFunctions.logInfo(methodName, "ResponseCode: " + responseCode + "; debugMessage: " + debugMessage);
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                UtilityFunctions.logDebug(methodName, "responseCode: OK");
                if (purchases == null) {
                    UtilityFunctions.logInfo(methodName, "purchases is null");
                    processPurchases(null);
                } else {
                    processPurchases(purchases);
                }
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                UtilityFunctions.logInfo(methodName, "responseCode: User canceled the purchase");
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                UtilityFunctions.logInfo(methodName, "responseCode: The user already owns this item");
                break;
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                UtilityFunctions.logError(
                        methodName,
                        "Developer error means that Google Play " +
                        "does not recognize the configuration. If you are just getting started, " +
                        "make sure you have configured the application correctly in the " +
                        "Google Play Console. The SKU product ID must match and the APK you " +
                        "are using must be signed with release keys.",
                        null
                );
                break;
        }
    }

    private void processPurchases(List<Purchase> purchasesList) {
        String methodName = CLASS_NAME + ".processPurchases";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (purchasesList == null) {
            UtilityFunctions.logDebug(methodName, "purchaseList is null");
            return;
        }
        UtilityFunctions.logDebug(methodName, "purchaseList count: " + purchasesList.size());

        // Process each purchase
        for (Purchase purchase : purchasesList) {
            processPurchase(purchase);
        }
        logAcknowledgementStatus(purchasesList);
    }

    private void processPurchase(Purchase purchase) {
        String methodName = CLASS_NAME + ".processPurchase";
        UtilityFunctions.logDebug(methodName, "Entered");

        String sku = purchase.getSku();
        UtilityFunctions.logInfo(methodName, "Processing purchase: " + sku);

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // TODO: Verify validity:
            // https://developer.android.com/google/play/billing/integrate#process

            if (sku.equals(BillingConstants.PRODUCT_SKU_REMOVE_ADS)) {
                processNonConsumablePurchase(purchase);
            } else if (sku.equals(BillingConstants.PRODUCT_SKU_SAY_THANKS)) {
                processConsumablePurchase(purchase);
            }
        }

    }

    private void processConsumablePurchase(Purchase purchase) {
        String methodName = CLASS_NAME + ".processConsumablePurchase";
        UtilityFunctions.logDebug(methodName, "Entered");

        String sku = purchase.getSku();
        UtilityFunctions.logDebug(methodName, "...sku: " + sku);
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            UtilityFunctions.logDebug(methodName, "purchaseState: PURCHASED...");
            grantPurchaseEntitlement(purchase);
            successfulPurchaseObservable.postValue(purchase);

            String purchaseToken = purchase.getPurchaseToken();
            UtilityFunctions.logInfo(methodName, "Calling billingClient.consumeAsync() for sku: " + sku);
            UtilityFunctions.logDebug(methodName, "...and purchase token: " + purchaseToken);
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchaseToken)
                            .build();
            billingClient.consumeAsync(consumeParams, this);
        }
    }

    @Override
    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
        String methodName = CLASS_NAME + ".onConsumeResponse";
        UtilityFunctions.logDebug(methodName, "Entered");

        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        UtilityFunctions.logInfo(methodName, "ResponseCode: " + responseCode + "; debugMessage: " + debugMessage);

        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // Handle the success of the consume operation.
            UtilityFunctions.logInfo(methodName, "...successful.");
            // Refresh the list of purchases
            queryPurchases();
        } else {
            UtilityFunctions.logError(methodName, "...unsuccessful, debugMessage: " + debugMessage, null);
        }
    }

    private void processNonConsumablePurchase(Purchase purchase) {
        String methodName = CLASS_NAME + ".processNonConsumablePurchase";
        UtilityFunctions.logDebug(methodName, "Entered");

        String sku = purchase.getSku();
        UtilityFunctions.logDebug(methodName, "...sku: " + sku);

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            UtilityFunctions.logDebug(methodName, "purchaseState: PURCHASED...");
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
        String methodName = CLASS_NAME + ".logAcknowledgementStatus";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (purchasesList == null) {
            UtilityFunctions.logDebug(methodName, "purchaseList is null");
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
        UtilityFunctions.logDebug(methodName, "Acknowledged:" + ack_yes
                + ";  Unacknowledged: " + ack_no);
    }

    /**
     * Launching the billing flow.
     * <p>
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    @SuppressWarnings("UnusedReturnValue")
    public BillingResult launchBillingFlow(Activity activity, BillingFlowParams params) {
        String methodName = CLASS_NAME + ".launchBillingFlow";
        UtilityFunctions.logDebug(methodName, "Entered");

        String sku = params.getSku();
        String oldSku = params.getOldSku();
        UtilityFunctions.logDebug(methodName, "Sku: " + sku + "; oldSku: " + oldSku);
        if (!billingClient.isReady()) {
            UtilityFunctions.logError(methodName, "billingClient is not ready", null);
        }
        BillingResult billingResult = billingClient.launchBillingFlow(activity, params);
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        UtilityFunctions.logDebug(methodName, "responseCode: " + responseCode + "; debugMessage: " + debugMessage);
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
        String methodName = CLASS_NAME + ".acknowledgePurchase";
        UtilityFunctions.logDebug(methodName, "Entered");

        String sku = purchase.getSku();
        String purchaseToken = purchase.getPurchaseToken();
        UtilityFunctions.logInfo(methodName, "Calling billingClient.acknowledgePurchase() for sku: " + sku);
        UtilityFunctions.logDebug(methodName, "...and purchaseToken: " + purchaseToken);
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build();
        billingClient.acknowledgePurchase(params, this);
    }

    @Override
    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
        String methodName = CLASS_NAME + ".acknowledgePurchase";
        UtilityFunctions.logDebug(methodName, "Entered");

        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        UtilityFunctions.logInfo(methodName, "ResponseCode: " + responseCode + "; debugMessage: " + debugMessage);
        // Refresh the list of purchases
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            queryPurchases();
        }
    }

    private void grantPurchaseEntitlement(Purchase purchase) {
        String methodName = CLASS_NAME + ".grantPurchaseEntitlement";
        UtilityFunctions.logDebug(methodName, "Entered");

        String sku = purchase.getSku();
        UtilityFunctions.logInfo(methodName, "Granting purchase entitlement for sku: " + sku);

        if (sku.equals(BillingConstants.PRODUCT_SKU_REMOVE_ADS)) {

            // The user bought the remove ads item
            GlobalSettings.getInstance().setHasPurchasedNoAds(true);

        } else //noinspection StatementWithEmptyBody
            if (sku.equals(BillingConstants.PRODUCT_SKU_SAY_THANKS)) {

            // The user bought the say thanks item
            // TODO: create some sort of prize for this

        } else {
            UtilityFunctions.logWtf(methodName, "Unknown sku: " + sku);
        }
    }

}
