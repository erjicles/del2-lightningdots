package com.delsquared.lightningdots.utilities;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentStatus;

public class EEAConsentFormListener extends ConsentFormListener {

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
        // Consent form loaded successfully
        LightningDotsApplication.logDebugMessage("Consent form loaded");
        if (this.form != null) {
            this.form.show();
        }
    }

    @Override
    public void onConsentFormOpened() {
        // Consent form was displayed.
        LightningDotsApplication.logDebugMessage("Consent form opened");
    }

    @Override
    public void onConsentFormClosed(
            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
        // Consent form was closed.
        LightningDotsApplication.logDebugMessage("Consent form closed; consentStatus: " + consentStatus.toString() + "; userPrefersAdFree: " + userPrefersAdFree.toString());
        LightningDotsApplication.consentStatus = consentStatus;
        LightningDotsApplication.userPrefersNoAds = userPrefersAdFree;
        listener.onHandleConsentFinished();
    }

    @Override
    public void onConsentFormError(String errorDescription) {
        // Consent form error.
        LightningDotsApplication.logDebugErrorMessage("Consent form error: " + errorDescription);
        listener.onHandleConsentFinished();
    }

}
