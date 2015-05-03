package com.delsquared.lightningdots.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.database.LoaderHelperGameResult;
import com.delsquared.lightningdots.game.ClickTargetProfileScriptHelper;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.game.InterfaceGameCallback;
import com.delsquared.lightningdots.graphics.SurfaceViewGame;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentGame extends Fragment implements InterfaceGameCallback {

	public static final String ARGUMENT_GAME_TYPE = "com.delsquared.lightningdots.gametype";
    private static final String LIGHTNINGDOTS_GAME_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3137388249745402/5514643977";

	private int currentGameType = Game.GameType.AGILITY.ordinal();
    private int currentGameLevel = 1;
    public GameResult gameResultHighScoreOverall;
    public GameResult gameResultHighScoreCurrentLevel;
    public int highestScriptedLevel = 0;

    private InterstitialAd gameInterstitialAd;

    public FragmentGame() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);

		// Get the game type from the bundle
		Bundle bundle = getArguments();
		int currentGameType = Game.GameType.AGILITY.ordinal();
		try {
			currentGameType = bundle.getInt(ARGUMENT_GAME_TYPE);
		} catch (Exception e) {

		}
		this.currentGameType = currentGameType;

        // Get the highest scripted level
        this.highestScriptedLevel = ClickTargetProfileScriptHelper.getHighestScriptedLevel(
                getActivity().getApplicationContext()
                , Game.GameType.values()[this.currentGameType]);

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
        Log.d("lightningdots", "FragmentGame onPause()");
    }

    @Override
    public void onResume() {

        super.onResume();

        Log.d("lightningdots", "FragmentGame onResume()");

        // Get the fragment view
        View fragmentView = getView();

        // Make sure the fragment view isn't null
        if (fragmentView != null ) {

            // Get the game surface view
            SurfaceViewGame surfaceViewGame = (SurfaceViewGame) fragmentView.findViewById(R.id.fragment_game_surfaceviewgame);

            // Set this fragment as the callback listener
            surfaceViewGame.setGameCallbackListener(this);

        }

        // Load the next ad
        loadInterstitialAd();

    }

    @Override
    public void onStop() { super.onStop(); }

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
        loadGameResultHighScoreCurrentLevel();
    }

    public void loadGameResultHighScoreOverall() {
        LoaderHelperGameResult loaderHelperGameResult = new LoaderHelperGameResult(getActivity().getApplicationContext());
        gameResultHighScoreOverall =
                loaderHelperGameResult.loadBestSuccessfulRun(
                        currentGameType
                        , (int) Game.getInstance().getGameTimeLimitMillis());
    }

    public void loadGameResultHighScoreCurrentLevel() {
        LoaderHelperGameResult loaderHelperGameResult = new LoaderHelperGameResult(getActivity().getApplicationContext());
        gameResultHighScoreCurrentLevel =
                loaderHelperGameResult.loadBestRunForLevel(
                        currentGameType
                        , currentGameLevel
                        , (int) Game.getInstance().getGameTimeLimitMillis());
    }

    public void processEndOfGameAd() {
        int randomInt = (int) Math.floor(Math.random() * 4.0);
        if ((LightningDotsApplication.numberOfGameTransitions - randomInt) % 4 == 0) {
            showInterstitialAd();
        }
        LightningDotsApplication.numberOfGameTransitions++;
    }

    public void showInterstitialAd() {

        // Check if the user has not purchased the nod ads item
        if (LightningDotsApplication.hasPurchasedNoAds) {
            return;
        }

        // Show the interstitial if it is ready
        // and if this is a third click. Otherwise, proceed as usual without showing it
        if (gameInterstitialAd != null
                && gameInterstitialAd.isLoaded()) {
            gameInterstitialAd.show();
        }
    }

    public void loadInterstitialAd() {

        // Check if the user has not purchased the nod ads item
        if (LightningDotsApplication.hasPurchasedNoAds) {
            return;
        }

        // Create an interstitial ad. When a natural transition in the app occurs (such as a
        // level ending in a game), show the interstitial. In this simple example, the press of a
        // button is used instead.
        //
        // If the button is clicked before the interstitial is loaded, the user should proceed to
        // the next part of the app (in this case, the next level).
        //
        // If the interstitial is finished loading, the user will view the interstitial before
        // proceeding.
        gameInterstitialAd = new InterstitialAd(getActivity());
        gameInterstitialAd.setAdUnitId(getString(R.string.ads_ad_unit_game_interstitials));

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adRequestBuilder.addTestDevice("C9BC6FE19C043A1AD5D28B767D91CE18");

        // Start loading the ad now so that it is ready by the time the user is ready to go to
        // the next level.
        gameInterstitialAd.loadAd(adRequestBuilder.build());

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
    }
}