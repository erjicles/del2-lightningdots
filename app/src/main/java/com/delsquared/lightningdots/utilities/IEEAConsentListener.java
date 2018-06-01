package com.delsquared.lightningdots.utilities;

import android.content.Context;

public interface IEEAConsentListener {
    Context getContext();
    void onHandleConsentFinished();
}
