package com.delsquared.lightningdots.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.fragment.app.FragmentActivity;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.fragments.FragmentGame;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivityGame extends FragmentActivity {
    private static final String CLASS_NAME = ActivityGame.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

		// Get the game type from the intent
		Intent intent = getIntent();
        int gameType = Game.GameType.AGILITY.ordinal();
		try {
			gameType = intent.getIntExtra(ActivityMain.EXTRA_GAME_TYPE, Game.GameType.AGILITY.ordinal());
		} catch (Exception e) {
            UtilityFunctions.logError(methodName, "Exception getting gameType EXTRA from intent", e);
		}

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, FragmentGame.newInstance(gameType))
                    .commit();
        }

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitygame));

    }

	@SuppressWarnings("EmptyMethod")
    @Override
	protected void onPause() {
        String methodName = CLASS_NAME + ".onPause";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onPause();
	}

    @Override
    protected void onStop() {
        String methodName = CLASS_NAME + ".onStop";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onStop();

        // Get the current system time
        long currentTimeMillis = SystemClock.elapsedRealtime();

        // Game activity is stopping, reset the game
        Game game = Game.getInstance();
        game.resetGame(
                this.getApplicationContext()
                , Game.GameType.values()[game.getGameType().ordinal()]
                , game.getGameLevel()
                , currentTimeMillis
                , game.surfaceViewGameThreadSharedData.getCanvasWidth()
                , game.surfaceViewGameThreadSharedData.getCanvasHeight());
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onDestroy() {
        String methodName = CLASS_NAME + ".onDestroy";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onDestroy();
    }
}
