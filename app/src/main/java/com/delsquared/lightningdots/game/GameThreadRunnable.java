package com.delsquared.lightningdots.game;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.delsquared.lightningdots.database.LoaderHelperGameResult;
import com.delsquared.lightningdots.database.SaveHelperGameResult;
import com.delsquared.lightningdots.graphics.SurfaceViewGame;

public class GameThreadRunnable implements Runnable {

	public static final long maximumGameProcessDelay = 33;
	public static final long targetGameProcessDelay = 10;
	public int currentGameProcessDelay = 33;

	Context context;
	private Handler handler;

	private boolean firstTimeInitialization = true;
	private boolean threadIsRunning = false;
    private boolean threadIsPaused = false;

	private Game game;

	private int gameType = Game.GameType.AGILITY.ordinal();
	private int gameLevel = 1;
    private int highestScriptedLevel = 1;

    double canvasWidth = 1.0;
    double canvasHeight = 1.0;

	public GameThreadRunnable(
            Context context
            , int gameType
            , int gameLevel
            , Handler handler) {
		this.context = context;
		this.gameType = gameType;
		this.gameLevel = gameLevel;
		this.handler = handler;
		this.game = Game.getInstance();
	}

	@Override
	public void run() {

		// Loop while the thread running flag is true
        while (threadIsRunning) {

            if (threadIsPaused == false) {

                // Get the current system time
                long currentTimeMillis = SystemClock.elapsedRealtime();

                processNewTime(currentTimeMillis);

                try {
                    Thread.sleep(currentGameProcessDelay);
                } catch(InterruptedException e) {}

            }

        }

	}

    public synchronized void setThreadIsRunning(boolean threadIsRunning) {
        this.threadIsRunning = threadIsRunning;
    }

    public synchronized void setHighestScriptedLevel(int highestScriptedLevel) {
        this.highestScriptedLevel = highestScriptedLevel;
    }

    public synchronized void setThreadIsPaused(boolean threadIsPaused) {
        this.threadIsPaused = threadIsPaused;
        long currentTimeMillis = SystemClock.elapsedRealtime();
        if (threadIsPaused == true) {
            game.pauseTimers(currentTimeMillis);
        } else {
            game.resumeTimers(currentTimeMillis);
        }

    }

    public synchronized void setGameLevel(int gameLevel) {
        if (game.getGameState() == Game.GameState.STOPPED) {
            long currentTimeMillis = SystemClock.elapsedRealtime();
            this.gameLevel = gameLevel;
            initializeGame(currentTimeMillis);
        }
    }

    public synchronized void setCanvasWidthAndHeight(boolean toggleNew, double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        game.setCanvasWidthAndHeight(canvasWidth, canvasHeight);
    }

	public void initializeGame(long currentTimeMillis) {

		// Get the surface view game thread shared data
		SurfaceViewGameThreadSharedData surfaceViewGameThreadSharedData = game.surfaceViewGameThreadSharedData;

		// Get and set shared data
		int canvasWidth = surfaceViewGameThreadSharedData.getCanvasWidth();
		int canvasHeight = surfaceViewGameThreadSharedData.getCanvasHeight();

		// Set shared data
		updateGameThreadSharedData(currentTimeMillis);

        // Send the message to reset the user clicks bitmap and canvas
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(
                SurfaceViewGame.surfaceViewGame_message_initializeGame
                , SurfaceViewGame.surfaceViewGame_message_initializeGame);
        message.setData(bundle);
        handler.sendMessage(message);

		// Initialize the game
		game.resetGame(
                context
				, Game.GameType.values()[gameType]
				, gameLevel
                , currentTimeMillis
				, canvasWidth
				, canvasHeight);

		}

	public void startGame(long startTime) {

		// Set the game time info
		game.setGameState(Game.GameState.STARTING, startTime);

		updateGameThreadSharedData(startTime);

	}

	public void endGame(long currentTimeMillis) {

		// Set the game state to preparing to end
		game.setGameState(Game.GameState.PREPARING_TO_END, currentTimeMillis);

        // Set the shared data
        updateGameThreadSharedData(currentTimeMillis);

        // Get the previous high score for this level
        int currentHighScore = 0;
        LoaderHelperGameResult loaderHelperGameResult = new LoaderHelperGameResult(context);
        GameResult bestGameResult = loaderHelperGameResult.loadBestRunForLevel(
                game.getGameType().ordinal()
                , game.getGameLevel()
                , (int) game.getGameTimeLimitMillis()
        );
        if (bestGameResult != null) {
            currentHighScore = bestGameResult.getUserClicks();
        }

        // Get the award level
        int awardLevel = GameLevelDefinitionHelper.getAwardLevel(game);

        // Check if the level was completed
        if (awardLevel > 0) {

            // Create the game result to save
            GameResult result = new GameResult(
                    -1
                    , game.getGameType().ordinal()
                    , game.getGameLevel()
                    , (int) game.getGameTimeLimitMillis()
                    , true
                    , awardLevel
                    , game.getListValidUserClick().size()
                    , null);

            // Save the game result to the database
            SaveHelperGameResult saveHelperGameResult = new SaveHelperGameResult(context);
            saveHelperGameResult.saveGameResult(result);

            // Increment the game level if this is a regular game type
            if (gameType == Game.GameType.AGILITY.ordinal()) {

                // Make sure we haven't reached the highest scripted level
                if (gameLevel < highestScriptedLevel) {
                    gameLevel++;
                }

                // Send the message that the level has changed
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt(
                        SurfaceViewGame.surfaceViewGame_message_levelCompleted
                        , gameLevel);
                message.setData(bundle);
                handler.sendMessage(message);
            }

        } else { // Award level = 0

            // Send the message that the level has failed
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt(
                    SurfaceViewGame.surfaceViewGame_message_levelFailed
                    , gameLevel);
            message.setData(bundle);
            handler.sendMessage(message);

        }

        boolean isLevelComplete = false;
        if (game.getGameType() == Game.GameType.AGILITY) {
            isLevelComplete = awardLevel > 0;
        }

        boolean isNewHighScore = game.getListNewUserClick().size() > currentHighScore
                && (bestGameResult != null || game.getGameType() == Game.GameType.TIME_ATTACK)
                && awardLevel > 0;

        // Set the game award level
        game.setGameCompleteState(
                isLevelComplete
                , awardLevel
                , isNewHighScore);

        currentTimeMillis = SystemClock.elapsedRealtime();

        // Set the game state to ending
        game.setGameState(Game.GameState.ENDING, currentTimeMillis);

        // Set the shared data
        updateGameThreadSharedData(currentTimeMillis);

	}

	public void processDownTouch(MotionEvent motionEvent) {

        long currentTimeMillis = SystemClock.elapsedRealtime();

        updateGameThreadSharedData(currentTimeMillis);

        float clickX = motionEvent.getX();
        float clickY = motionEvent.getY();

        switch (game.getGameState()) {

            case STOPPED:

                startGame(currentTimeMillis);

                break;

            case READY_TO_START:
                break;

            case STARTING:
                break;

            case RUNNING:

                game.processUserClick(context, clickX, clickY);

                break;

            case ENDING:
                break;

            case ENDED:

                long endTime = game.getGameEndTimeMillis();

                if (currentTimeMillis - endTime > 2000) {
                    initializeGame(currentTimeMillis);
                }

            default:

        }

	}

	public Bundle saveState(Bundle bundle) {

		// Check if the bundle is null
		if (bundle != null) {
			// Save the game state to the bundle
		}

		return bundle;
	}

	public synchronized void restoreState(Bundle savedState) {

		// Check if the bundle is null
		if (savedState != null) {
			// Restore the saved state
		}

	}

	public void processNewTime(long currentTimeMillis) {

		// Get the surface view game thread shared data
		SurfaceViewGameThreadSharedData surfaceViewGameThreadSharedData = game.surfaceViewGameThreadSharedData;

		// Set shared data
		updateGameThreadSharedData(currentTimeMillis);

		// Process the new time in the game
		game.processNewTime(context, currentTimeMillis);

		switch (game.getGameState()) {

			case STOPPED:
				if (game.canvasSizeChanged) {
					initializeGame(currentTimeMillis);
					firstTimeInitialization = false;
					game.canvasSizeChanged = false;
				}
				break;

			case READY_TO_START:
				break;

			case STARTING:
				if (game.getStartTimerExpired() == true) {
					game.setGameState(Game.GameState.RUNNING, currentTimeMillis);
				}
				break;

			case RUNNING:

				// Check if the game is over
				if (game.getGameTimeExpired() == true) {

					// End the game
					endGame(currentTimeMillis);

				}

				break;

			case PAUSED:
				break;

			case RESUMING:
				break;

			case ENDING:

                if (game.getEndingTimerExpired() == true) {
                    game.setGameState(Game.GameState.ENDED, currentTimeMillis);

                    // Send the message that the game has ended
                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            SurfaceViewGame.surfaceViewGame_message_gameEnded
                            , SurfaceViewGame.surfaceViewGame_message_gameEnded);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
				break;

			case ENDED:
				break;

			default:

		}

	}

	public void updateGameThreadSharedData(
			long currentTimeMillis) {

		game.gameThreadSharedData = new GameThreadSharedData(currentTimeMillis);
	}

}