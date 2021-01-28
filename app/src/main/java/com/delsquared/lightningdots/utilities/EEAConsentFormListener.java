package com.delsquared.lightningdots.utilities;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentStatus;

public class EEAConsentFormListener extends ConsentFormListener {
    private static final String CLASS_NAME = EEAConsentFormListener.class.getSimpleName();

    private final IEEAConsentListener listener;
    private ConsentForm form;
    public EEAConsentFormListener(IEEAConsentListener listener) {
        this.listener = listener;
    }
    public void setConsentForm(ConsentForm form) {
        this.form = form;
    }

    @Override
    public void onConsentFormLoaded() {
        String methodName = CLASS_NAME + ".onConsentFormLoaded";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Consent form loaded successfully
        if (this.form != null) {
            this.form.show();
        }
    }

    @Override
    public void onConsentFormOpened() {
        String methodName = CLASS_NAME + ".onConsentFormOpened";
        UtilityFunctions.logDebug(methodName, "Entered");
    }

    @Override
    public void onConsentFormClosed(
            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
        String methodName = CLASS_NAME + ".onConsentFormClosed";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Consent form was closed.
        UtilityFunctions.logInfo(
                methodName,
                "ConsentStatus: " + consentStatus.toString()
                + "; userPrefersAdFree: " + userPrefersAdFree.toString());
        LightningDotsApplication.setConsentStatus(consentStatus);
        LightningDotsApplication.setUserPrefersNoAds(userPrefersAdFree);
        listener.onHandleConsentFinished();
    }

    @Override
    public void onConsentFormError(String errorDescription) {
        String methodName = CLASS_NAME + ".onConsentFormError";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Consent form error.
        UtilityFunctions.logError(methodName, "errorDescription: " + errorDescription, null);
        listener.onHandleConsentFinished();
    }

}
