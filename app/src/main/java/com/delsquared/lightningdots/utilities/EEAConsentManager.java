package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.BuildConfig;
import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

import java.net.MalformedURLException;
import java.net.URL;

public class EEAConsentManager {
    private static final String CLASS_NAME = EEAConsentManager.class.getSimpleName();

    private final IEEAConsentListener listener;

    public EEAConsentManager(IEEAConsentListener listener) {
        this.listener = listener;
    }

    public void handleAdConsent() {
        handleAdConsent(false);
    }

    public void handleAdConsent(boolean forceShowDialog) {
        String methodName = CLASS_NAME + ".handleAdConsent(boolean forceShowDialog)";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Get the consent information
        ConsentInformation consentInformation = ConsentInformation.getInstance(listener.getContext());

        // Set test devices
        String[] testDeviceIds = listener.getContext().getResources().getStringArray(R.array.test_device_ids_consent);
        for (String testDeviceId : testDeviceIds) {
            consentInformation.addTestDevice(testDeviceId);
        }

        // For debug, set geographical area to EEA
        if (BuildConfig.DEBUG) {
            consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        }

        // Check if the user is in the EEA
        if (consentInformation.isRequestLocationInEeaOrUnknown()
                || consentInformation.isTestDevice()) {

            // Set the flag for the user being from the EEA
            GlobalSettings.getInstance().setIsUserFromEEA(true);

            // Retrieve the EEA user's consent
            handleEEAConsent(forceShowDialog, consentInformation);

        } else { // The user is not in the EEA
            listener.onHandleConsentFinished();
        }

    }

    private void handleEEAConsent(final boolean forceShowDialog, final ConsentInformation consentInformation) {
        String methodName = CLASS_NAME + ".handleEEAConsent(final boolean forceShowDialog, final ConsentInformation consentInformation)";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Create the publisher ids
        String[] publisherIds = {listener.getContext().getString(R.string.admob_publisher_id)};

        // Refresh the user's consent information
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                UtilityFunctions.logInfo(methodName, "Consent status updated: " + consentStatus.toString());

                // Check if the user's consent status is unknown
                if (consentStatus.equals(ConsentStatus.UNKNOWN)
                        || forceShowDialog) {

                    // Create the privacy policy url
                    URL privacyUrl = null;
                    try {
                        privacyUrl = new URL(listener.getContext().getString(R.string.privacy_policy_url));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        // Handle error.
                    }

                    // Create the consent form
                    EEAConsentFormListener formListener = new EEAConsentFormListener(listener);
                    ConsentForm form = new ConsentForm.Builder(listener.getContext(), privacyUrl)
                            .withListener(formListener)
                            .withPersonalizedAdsOption()
                            .withNonPersonalizedAdsOption()
                            .withAdFreeOption()
                            .build();
                    formListener.setConsentForm(form);

                    // Load the form
                    form.load();

                } else { // The user's consent status is known

                    GlobalSettings.getInstance().setConsentStatus(consentStatus);
                    listener.onHandleConsentFinished();

                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                UtilityFunctions.logError(methodName, "onFailedToUpdateConsentInfo called, errorDescription: " + errorDescription, null);
                listener.onHandleConsentFinished();
            }
        });

    }

}
