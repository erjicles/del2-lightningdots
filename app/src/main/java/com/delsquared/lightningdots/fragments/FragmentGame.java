package com.delsquared.lightningdots.fragments;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delsquared.lightningdots.BuildConfig;
import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.database.LoaderHelperGameResult;
import com.delsquared.lightningdots.game.LevelDefinitionLadderHelper;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.game.InterfaceGameCallback;
import com.delsquared.lightningdots.graphics.SurfaceViewGame;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Date;

public class FragmentGame extends Fragment implements InterfaceGameCallback {
    private static final String CLASS_NAME = FragmentGame.class.getSimpleName();

	public static final String ARGUMENT_GAME_TYPE = "com.delsquared.lightningdots.gametype";

	private int currentGameType = Game.GameType.AGILITY.ordinal();
    private int currentGameLevel = 1;
    public GameResult gameResultHighScoreOverall;
    public GameResult gameResultHighScoreCurrentLevel;
    public int highestScriptedLevel = 0;

    private InterstitialAd gameInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreateView";
        UtilityFunctions.logDebug(methodName, "Entered");

        View rootView = inflater.inflate(R.layout.fragment_game, container, false);

		// Get the game type from the bundle
        int currentGameType = Game.GameType.AGILITY.ordinal();
		Bundle bundle = getArguments();
		if (bundle != null) {
            try {
                currentGameType = bundle.getInt(ARGUMENT_GAME_TYPE);
            } catch (Exception ignored) {

            }
        }

		this.currentGameType = currentGameType;

        // Get the highest scripted level
        this.highestScriptedLevel = 1;
        if (this.currentGameType == Game.GameType.AGILITY.ordinal()) {
            Activity activity = getActivity();
            if (activity != null) {
                this.highestScriptedLevel = LevelDefinitionLadderHelper.getHighestScriptedLevel(
                        activity.getApplicationContext());
            }

        }


        // Get the best score overall
        loadGameResultHighScoreOverall();

        // Determine the current level
        currentGameLevel = 1;
        if (gameResultHighScoreOverall != null) {
            currentGameLevel = gameResultHighScoreOverall.getGameLevel();
            if (currentGameLevel < highestScriptedLevel) {
                currentGameLevel++;
            }
        }

        setCurrentGameLevel(currentGameLevel);

		return rootView;
    }

	public static FragmentGame newInstance(int gameType) {
        String methodName = CLASS_NAME + ".newInstance";
        UtilityFunctions.logDebug(methodName, "Entered");

		// Create the new instance
		FragmentGame f = new FragmentGame();

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

    @Override
    public void onPause() {
        String methodName = CLASS_NAME + ".onPause";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onPause();
    }

    @Override
    public void onResume() {
        String methodName = CLASS_NAME + ".onResume";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onResume();

        // Get the fragment view
        View fragmentView = getView();

        // Make sure the fragment view isn't null
        if (fragmentView != null ) {

            // Get the game surface view
            SurfaceViewGame surfaceViewGame = fragmentView.findViewById(R.id.fragment_game_surfaceviewgame);

            // Set this fragment as the callback listener
            surfaceViewGame.setGameCallbackListener(this);

        }

        // Load the next ad
        startLoadingInterstitialAds();

    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onStop() {
        String methodName = CLASS_NAME + ".onStop";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onStop();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDestroy() {
        String methodName = CLASS_NAME + ".onDestroy";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onDestroy();
    }

    @Override
    public void onLevelCompleted(int nextLevel) {
        String methodName = CLASS_NAME + ".onLevelCompleted";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (nextLevel <= highestScriptedLevel) {
            setCurrentGameLevel(nextLevel);
        }
        loadGameResultHighScoreOverall();
    }

    @Override
    public void onLevelFailed() {
        String methodName = CLASS_NAME + ".onLevelFailed";
        UtilityFunctions.logDebug(methodName, "Entered");
    }

    @Override
    public void onGameEnded() {
        String methodName = CLASS_NAME + ".onGameEnded";
        UtilityFunctions.logDebug(methodName, "Entered");
        processEndOfGameAd();
    }

    @Override
    public void onLevelDecrementSelected() {
        String methodName = CLASS_NAME + ".onLevelDecrementSelected";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (currentGameLevel > 1) {
           setCurrentGameLevel(currentGameLevel - 1);
        }
    }

    @Override
    public void onLevelIncrementSelected() {
        String methodName = CLASS_NAME + ".onLevelIncrementSelected";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (gameResultHighScoreOverall != null) {
            if (currentGameLevel < gameResultHighScoreOverall.getGameLevel() + 1) {
                if (currentGameLevel < highestScriptedLevel) {
                    setCurrentGameLevel(currentGameLevel + 1);
                }
            }
        }
    }

    @Override
    public int onGetCurrentLevel() {
        String methodName = CLASS_NAME + ".onGetCurrentLevel";
        UtilityFunctions.logDebug(methodName, "Entered");

        return this.currentGameLevel;
    }

    @Override
    public int onGetCurrentGameType() {
        String methodName = CLASS_NAME + ".onGetCurrentGameType";
        UtilityFunctions.logDebug(methodName, "Entered");

        return this.currentGameType;
    }

    @Override
    public int onGetHighestScriptedLevel() {
        String methodName = CLASS_NAME + ".onGetHighestScriptedLevel";
        UtilityFunctions.logDebug(methodName, "Entered");

        return this.highestScriptedLevel;
    }

    @Override
    public GameResult onGetGameResultHighScoreOverall() {
        String methodName = CLASS_NAME + ".onGetGameResultHighScoreOverall";
        UtilityFunctions.logDebug(methodName, "Entered");

        return this.gameResultHighScoreOverall;
    }

    @Override
    public GameResult onGetGameResultHighScoreCurrentLevel() {
        String methodName = CLASS_NAME + ".onGetGameResultHighScoreCurrentLevel";
        UtilityFunctions.logDebug(methodName, "Entered");

        return this.gameResultHighScoreCurrentLevel;
    }

    public void setCurrentGameLevel(int currentGameLevel) {
        String methodName = CLASS_NAME + ".setCurrentGameLevel";
        UtilityFunctions.logDebug(methodName, "Entered");

        this.currentGameLevel = currentGameLevel;
        if (currentGameType == Game.GameType.TIME_ATTACK.ordinal()) {
            this.currentGameLevel = 1;
        }
        loadGameResultHighScoreCurrentLevel();
    }

    public void loadGameResultHighScoreOverall() {
        String methodName = CLASS_NAME + ".loadGameResultHighScoreOverall";
        UtilityFunctions.logDebug(methodName, "Entered");

        Activity activity = getActivity();
        if (activity == null) {
            UtilityFunctions.logError(methodName, "activity is null", null);
            return;
        }
        LoaderHelperGameResult loaderHelperGameResult = new LoaderHelperGameResult(activity.getApplicationContext());
        gameResultHighScoreOverall =
                loaderHelperGameResult.loadBestSuccessfulRun(
                        currentGameType
                        , (int) Game.getInstance().getGameTimeLimitMillis());

        // Check if we are in debug mode and should allow all levels
        if (BuildConfig.DEBUG
                && getResources().getBoolean(R.bool.activity_game_allow_all_levels)) {

            // Set the highest result to the highest level
            gameResultHighScoreOverall = new GameResult(
                    0
                    , currentGameType
                    , highestScriptedLevel
                    , (int) Game.getInstance().getGameTimeLimitMillis()
                    , true
                    , 1
                    , 0
                    , new Date()
            );

        }
    }

    public void loadGameResultHighScoreCurrentLevel() {
        String methodName = CLASS_NAME + ".loadGameResultHighScoreCurrentLevel";
        UtilityFunctions.logDebug(methodName, "Entered");

        Activity activity = getActivity();
        if (activity == null) {
            UtilityFunctions.logError(methodName, "activity is null", null);
            return;
        }
        LoaderHelperGameResult loaderHelperGameResult = new LoaderHelperGameResult(activity.getApplicationContext());
        gameResultHighScoreCurrentLevel =
                loaderHelperGameResult.loadBestRunForLevel(
                        currentGameType
                        , currentGameLevel
                        , (int) Game.getInstance().getGameTimeLimitMillis());
    }

    public void processEndOfGameAd() {
        String methodName = CLASS_NAME + ".processEndOfGameAd";
        UtilityFunctions.logDebug(methodName, "Entered");

        int randomInt = UtilityFunctions.generateRandomIndex(0, 4);
        if ((LightningDotsApplication.numberOfGameTransitions - randomInt) % 4 == 0) {
            showInterstitialAd();
        }
        LightningDotsApplication.numberOfGameTransitions++;
    }

    public void showInterstitialAd() {
        String methodName = CLASS_NAME + ".showInterstitialAd";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Check if the user has purchased the no ads item
        // or if the user is a non-consenting EEU user
        if (!LightningDotsApplication.getAreAdsEnabled()) {
            return;
        }

        // Show the interstitial if it is ready
        // and if this is a third click. Otherwise, proceed as usual without showing it
        if (gameInterstitialAd != null
                && gameInterstitialAd.isLoaded()) {
            gameInterstitialAd.show();
        }
    }

    public void startLoadingInterstitialAds() {
        String methodName = CLASS_NAME + ".startLoadingInterstitialAds";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Check if the user has purchased the no ads item
        // or if the user is a non-consenting EEU user
        if (!LightningDotsApplication.getAreAdsEnabled()) {
            UtilityFunctions.logInfo(methodName, "Interstitial ad loading skipped");
            return;
        }

        MobileAds.initialize(getActivity(), initializationStatus -> {
            // Create an interstitial ad. When a natural transition in the app occurs (such as a
            // level ending in a game), show the interstitial. In this simple example, the press of a
            // button is used instead.
            //
            // If the button is clicked before the interstitial is loaded, the user should proceed to
            // the next part of the app (in this case, the next level).
            //
            // If the interstitial is finished loading, the user will view the interstitial before
            // proceeding.
            Context context = getContext();
            if (context == null) {
                UtilityFunctions.logError(methodName, "context is null", null);
                return;
            }
            gameInterstitialAd = new InterstitialAd(context);
            gameInterstitialAd.setAdUnitId(getString(R.string.ads_ad_unit_game_interstitials));

            loadInterstitialAd();

            // Set an AdListener.
            gameInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    //Toast.makeText(MyActivity.this,
                    //        "The interstitial is loaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdClosed() {
                    loadInterstitialAd();
                }
            });
        });

    }

    private void loadInterstitialAd() {
        String methodName = CLASS_NAME + ".loadInterstitialAd";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (gameInterstitialAd == null) {
            UtilityFunctions.logError(methodName, "gameInterstitialAd is null", null);
            return;
        }
        if (!LightningDotsApplication.getAreAdsEnabled()) {
            UtilityFunctions.logInfo(methodName, "Ads are disabled, skipping");
            return;
        }

        // Create an ad request.
        // Global ad request configuration is set in ActivityMain constructor
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Start loading the ad now so that it is ready by the time the user is ready to go to
        // the next level.
        gameInterstitialAd.loadAd(adRequestBuilder.build());
    }
}