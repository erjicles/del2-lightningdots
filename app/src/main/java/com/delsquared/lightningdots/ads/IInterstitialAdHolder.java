package com.delsquared.lightningdots.ads;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.interstitial.InterstitialAd;

public interface IInterstitialAdHolder {
    void onInterstitialAdLoaded(@NonNull InterstitialAd interstitialAd);
    void onAdShowedFullScreenContent();
}
