package com.delsquared.lightningdots.fragments;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

public class FragmentInstructions extends Fragment {

    public static final String ARGUMENT_GAME_TYPE = "com.delsquared.lightningdots.gametype";

    private int currentGameType = Game.GameType.AGILITY.ordinal();

    public FragmentInstructions() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_instructions, container, false);

        // Get the game type from the bundle
        Bundle bundle = getArguments();
        int currentGameType = Game.GameType.AGILITY.ordinal();
        try {
            currentGameType = bundle.getInt(ARGUMENT_GAME_TYPE);
        } catch (Exception e) {

        }
        this.currentGameType = currentGameType;

        // ---------- BEGIN Initialize the settings ---------- //

        // Initialize the never show this again checkbox
        CheckBox checkBoxNeverShowThisAgain = (CheckBox) rootView.findViewById(R.id.fragment_instructions_checkbox_nevershowthisagain);
        checkBoxNeverShowThisAgain.setChecked(!LightningDotsApplication.settingShowInstructions);

        // ---------- END Initialize the settings ---------- //

        return rootView;
    }

    public static FragmentInstructions newInstance(int gameType) {

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
        super.onActivityCreated(savedInstanceState);
    }

    public void startInstructionsAnimation() {

        // Get the instructions image view
        ImageView imageViewInstructions = (ImageView) getView().findViewById(R.id.fragment_instructions_imageview_instructions);

        // Get the animation from the src, which has been compiled to an AnimationDrawable object.
        AnimationDrawable animationDrawableInstructions = (AnimationDrawable) imageViewInstructions.getDrawable();

        // Start the animation
        animationDrawableInstructions.start();

    }

    public void checkChanged_NeverShowThisAgain(boolean isChecked) {

        // Set the show instructions setting
        LightningDotsApplication.setShowInstructions(getActivity(), !isChecked);

    }


}
