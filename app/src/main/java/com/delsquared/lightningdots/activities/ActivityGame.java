package com.delsquared.lightningdots.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.fragments.FragmentGame;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivityGame extends Activity {

    private int gameType = Game.GameType.AGILITY.ordinal();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

		// Get the game type from the intent
		Intent intent = getIntent();
		gameType = Game.GameType.AGILITY.ordinal();
		try {
			gameType = intent.getIntExtra(ActivityMain.EXTRA_GAME_TYPE, Game.GameType.AGILITY.ordinal());
		} catch (Exception e) {

		}

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, FragmentGame.newInstance(gameType))
                    .commit();
        }

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitygame));

    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

	@Override
	protected void onPause() {
        super.onPause();
	}

    @Override
    protected void onStop() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
