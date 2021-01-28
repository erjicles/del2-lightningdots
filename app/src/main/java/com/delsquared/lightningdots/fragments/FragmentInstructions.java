package com.delsquared.lightningdots.fragments;

import androidx.fragment.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.Objects;

public class FragmentInstructions extends Fragment {
    private static final String CLASS_NAME = FragmentInstructions.class.getSimpleName();

    public static final String ARGUMENT_GAME_TYPE = "com.delsquared.lightningdots.gametype";

    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreateView";
        UtilityFunctions.logDebug(methodName, "Entered");

        View rootView = inflater.inflate(R.layout.fragment_instructions, container, false);

        // ---------- BEGIN Initialize the settings ---------- //

        // Initialize the never show this again checkbox
        CheckBox checkBoxNeverShowThisAgain = rootView.findViewById(R.id.fragment_instructions_checkbox_nevershowthisagain);
        checkBoxNeverShowThisAgain.setChecked(!LightningDotsApplication.settingShowInstructions);

        // ---------- END Initialize the settings ---------- //

        return rootView;
    }

    public static FragmentInstructions newInstance(int gameType) {
        String methodName = CLASS_NAME + ".newInstance";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Create the new instance
        FragmentInstructions f = new FragmentInstructions();

        // Create the arguments bundle for the game fragment
        Bundle bundleGameFragment = new Bundle();
        bundleGameFragment.putInt(ARGUMENT_GAME_TYPE, gameType);

        // Set the arguments bundle
        f.setArguments(bundleGameFragment);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onActivityCreated";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onActivityCreated(savedInstanceState);
    }

    public void startInstructionsAnimation() {
        String methodName = CLASS_NAME + ".startInstructionsAnimation";
        UtilityFunctions.logDebug(methodName, "Entered");

        View view = getView();
        if (view == null) {
            UtilityFunctions.logError(methodName, "view is null", null);
            return;
        }
        // Get the instructions image view
        ImageView imageViewInstructions = view.findViewById(R.id.fragment_instructions_imageview_instructions);
        if (imageViewInstructions == null) {
            UtilityFunctions.logError(methodName, "imageViewInstructions is null", null);
            return;
        }

        // Get the animation from the src, which has been compiled to an AnimationDrawable object.
        AnimationDrawable animationDrawableInstructions = (AnimationDrawable) imageViewInstructions.getDrawable();
        if (animationDrawableInstructions == null) {
            UtilityFunctions.logError(methodName, "animationDrawableInstructions is null", null);
            return;
        }

        // Start the animation
        animationDrawableInstructions.start();

    }

    public void checkChanged_NeverShowThisAgain(boolean isChecked) {
        String methodName = CLASS_NAME + ".checkChanged_NeverShowThisAgain";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Set the show instructions setting
        LightningDotsApplication.setShowInstructions(Objects.requireNonNull(getActivity()), !isChecked);

    }


}
