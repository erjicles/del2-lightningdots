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

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentGame extends Fragment implements InterfaceGameCallback {

	public static final String ARGUMENT_GAME_TYPE = "com.delsquared.lightningdots.gametype";

	private int currentGameType = Game.GameType.AGILITY.ordinal();
    private int currentGameLevel = 1;
    public GameResult gameResultHighScoreOverall;
    public GameResult gameResultHighScoreCurrentLevel;
    public int highestScriptedLevel = 0;

    private InterstitialAd gameInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public void onPause() {

        super.onPause();

        LightningDotsApplication.logDebugMessage("FragmentGame onPause()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LightningDotsApplication.logDebugMessage("FragmentGame onResume()");

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
    public void onStop() { super.onStop(); }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onLevelCompleted(int nextLevel) {
        if (nextLevel <= highestScriptedLevel) {
            setCurrentGameLevel(nextLevel);
        }
        loadGameResultHighScoreOverall();
    }

    @Override
    public void onLevelFailed() {
        LightningDotsApplication.logDebugMessage("Level failed");
    }

    @Override
    public void onGameEnded() { processEndOfGameAd(); }

    @Override
    public void onLevelDecrementSelected() {
        if (currentGameLevel > 1) {
           setCurrentGameLevel(currentGameLevel - 1);
        }
    }

    @Override
    public void onLevelIncrementSelected() {
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
        return this.currentGameLevel;
    }

    @Override
    public int onGetCurrentGameType() {
        return this.currentGameType;
    }

    @Override
    public int onGetHighestScriptedLevel() { return this.highestScriptedLevel; }

    @Override
    public GameResult onGetGameResultHighScoreOverall() {
        return this.gameResultHighScoreOverall;
    }

    @Override
    public GameResult onGetGameResultHighScoreCurrentLevel() {
        return this.gameResultHighScoreCurrentLevel;
    }

    public void setCurrentGameLevel(int currentGameLevel) {
        this.currentGameLevel = currentGameLevel;
        if (currentGameType == Game.GameType.TIME_ATTACK.ordinal()) {
            this.currentGameLevel = 1;
        }
        loadGameResultHighScoreCurrentLevel();
    }

    public void loadGameResultHighScoreOverall() {
        Activity activity = getActivity();
        if (activity == null) {
            LightningDotsApplication.logDebugErrorMessage("activity is null");
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
        Activity activity = getActivity();
        if (activity == null) {
            LightningDotsApplication.logDebugErrorMessage("activity is null");
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
        int randomInt = UtilityFunctions.generateRandomIndex(0, 4);
        if ((LightningDotsApplication.numberOfGameTransitions - randomInt) % 4 == 0) {
            showInterstitialAd();
        }
        LightningDotsApplication.numberOfGameTransitions++;
    }

    public void showInterstitialAd() {

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

        // Check if the user has purchased the no ads item
        // or if the user is a non-consenting EEU user
        if (!LightningDotsApplication.getAreAdsEnabled()) {
            LightningDotsApplication.logDebugMessage("Interstitial ad loading skipped");
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
                LightningDotsApplication.logDebugErrorMessage("context is null");
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
        if (gameInterstitialAd == null) {
            LightningDotsApplication.logDebugErrorMessage("gameInterstitialAd is null");
            return;
        }
        if (!LightningDotsApplication.getAreAdsEnabled()) {
            LightningDotsApplication.logDebugMessage("Ads are disabled");
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