package com.delsquared.lightningdots.graphics;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.game.GameThreadRunnable;
import com.delsquared.lightningdots.game.InterfaceGameCallback;

public class SurfaceViewGame
		extends SurfaceView
		implements SurfaceHolder.Callback {

    // Create the interface between the surface view and the fragment to set and retrieve game parameters
    public InterfaceGameCallback interfaceGameCallback;
    public void setGameCallbackListener(InterfaceGameCallback interfaceGameCallback) {
        this.interfaceGameCallback = interfaceGameCallback;
    }

    // Messages passed to the threads
    public static final String surfaceViewGame_message_initializeGame = "initialize_game";
    public static final String surfaceViewGame_message_levelCompleted = "level_completed";
    public static final String surfaceViewGame_message_levelFailed = "level_failed";
    public static final String surfaceViewGame_message_gameEnded = "game_ended";
    public static final String surfaceViewGame_message_buttonPressed_decrementLevel = "buttonPressed_decrementLevel";
    public static final String surfaceViewGame_message_buttonPressed_incrementLevel = "buttonPressed_incrementLevel";

    // The game thread
    Thread gameThread;

    // The drawing thread
    Thread surfaceViewGameThread;

	// The runnable that processes the game
	private GameThreadRunnable gameThreadRunnable;

    // The runnable that draws the game
    private SurfaceViewGameThreadRunnable surfaceViewGameThreadRunnable;

	private Context context;

    //private int gameType;
    //private int gameLevel;

    public SurfaceViewGame(Context context) {
        super(context);

		this.context = context;

        registerSurfaceHolder();

    }

	public SurfaceViewGame(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

        registerSurfaceHolder();

	}

	public SurfaceViewGame(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.context = context;

        registerSurfaceHolder();

	}

    private void registerSurfaceHolder() {
        // Register our interest in hearing about changes to our surface
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

	@Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d("lightningdots", "SurfaceViewGame surfaceCreated()");
        startThreads();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.d("lightningdots", "SurfaceViewGame surfaceChanged()");
		surfaceViewGameThreadRunnable.setSurfaceSize(width, height);

    }

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
        Log.d("lightningdots", "SurfaceViewGame onWindowFocusChanged()");
        if (!hasWindowFocus) {
            pauseThreads();
        } else {
            resumeThreads();
        }
	}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("lightningdots", "SurfaceViewGame surfaceDestroyed()");
        stopThreads();

    }

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent){
        Game gameInstance = Game.getInstance();
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            boolean gameThreadTouchProcessResult =
                    surfaceViewGameThreadRunnable.processDownTouch(motionEvent);
            if (gameThreadTouchProcessResult == false) {
                gameThreadRunnable.processDownTouch(motionEvent);
            }
		} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

		} else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

		}
		return true;
	}

    private void startThreads() {

        Log.d("lightningdots", "SurfaceViewGame startThreads()");

        SurfaceHolder surfaceHolder = getHolder();

        int currentGameType = interfaceGameCallback.onGetCurrentGameType();
        int currentGameLevel = interfaceGameCallback.onGetCurrentLevel();
        final GameResult gameResultHighScoreCurrentLevel =
                interfaceGameCallback.onGetGameResultHighScoreCurrentLevel();

        // Create the drawing thread
        surfaceViewGameThreadRunnable =
                new SurfaceViewGameThreadRunnable(
                        surfaceHolder
                        , context
                        , new Handler() {

                    @Override
                    public void handleMessage(Message m) {

                        // Initialize the message string
                        String message = "";

                        // Get the data bundle
                        Bundle bundle = m.getData();

                        // Check if we have a valid bundle
                        if (bundle != null) {

                            try {

                                if (bundle.containsKey(surfaceViewGame_message_buttonPressed_decrementLevel)) {
                                    message = bundle.getString(surfaceViewGame_message_buttonPressed_decrementLevel);
                                } else if (bundle.containsKey(surfaceViewGame_message_buttonPressed_incrementLevel)) {
                                    message = bundle.getString(surfaceViewGame_message_buttonPressed_incrementLevel);
                                }

                            } catch (Exception e) {

                                message = "";

                            }

                        }

                        // Check if this message is to reset the user clicks bitmap and canvas
                        if (message.equals(surfaceViewGame_message_buttonPressed_decrementLevel)) {
                            int currentLevel = interfaceGameCallback.onGetCurrentLevel();
                            if (currentLevel > 1) {
                                interfaceGameCallback.onLevelDecrementSelected();
                                gameThreadRunnable.setGameLevel(
                                        interfaceGameCallback.onGetCurrentLevel());
                                surfaceViewGameThreadRunnable.setGameResultHighScoreCurrentLevel(
                                        interfaceGameCallback.onGetGameResultHighScoreCurrentLevel());
                            }
                        } else if (message.equals(surfaceViewGame_message_buttonPressed_incrementLevel)) {
                            int currentLevel = interfaceGameCallback.onGetCurrentLevel();
                            GameResult gameResultHighScoreOverall =
                                    interfaceGameCallback.onGetGameResultHighScoreOverall();
                            int bestLevel = (gameResultHighScoreOverall == null) ?
                                    0
                                    : gameResultHighScoreOverall.getGameLevel();
                            if (currentLevel < bestLevel + 1) {
                                interfaceGameCallback.onLevelIncrementSelected();
                                gameThreadRunnable.setGameLevel(
                                        interfaceGameCallback.onGetCurrentLevel());
                                surfaceViewGameThreadRunnable.setGameResultHighScoreCurrentLevel(
                                        interfaceGameCallback.onGetGameResultHighScoreCurrentLevel());
                            }
                        }

                    }
                });

        // Create the game processing thread
        gameThreadRunnable = new GameThreadRunnable(
                context
                , currentGameType
                , currentGameLevel
                , new Handler() {

            @Override
            public void handleMessage(Message m) {

                // Initialize the message string
                String messageKey = "";

                // Get the data bundle
                Bundle bundle = m.getData();

                // Check if we have a valid bundle
                if (bundle != null) {

                    try {

                        if (bundle.containsKey(surfaceViewGame_message_initializeGame)) {
                            messageKey = surfaceViewGame_message_initializeGame;
                        } else if (bundle.containsKey(surfaceViewGame_message_levelCompleted)) {
                            messageKey = surfaceViewGame_message_levelCompleted;
                        } else if (bundle.containsKey(surfaceViewGame_message_levelFailed)) {
                            messageKey = surfaceViewGame_message_levelFailed;
                        } else if (bundle.containsKey(surfaceViewGame_message_gameEnded)) {
                            messageKey = surfaceViewGame_message_gameEnded;
                        }

                    } catch (Exception e) {

                        messageKey = "";

                    }

                }

                // Check if this message is to reset the user clicks bitmap and canvas
                if (messageKey.equals(surfaceViewGame_message_initializeGame)) {

                    // Make sure the surface view game thread exists
                    if (surfaceViewGameThreadRunnable != null) {

                        // Reset the user clicks bitmap and canvas
                        surfaceViewGameThreadRunnable.initializeGame();

                    }

                } else if (messageKey.equals(surfaceViewGame_message_levelCompleted)) {

                    int nextLevel = bundle.getInt(messageKey);
                    interfaceGameCallback.onLevelCompleted(nextLevel);
                    surfaceViewGameThreadRunnable.setGameResultHighScoreCurrentLevel(
                            interfaceGameCallback.onGetGameResultHighScoreCurrentLevel());
                } else if (messageKey.equals(surfaceViewGame_message_levelFailed)) {
                    interfaceGameCallback.onLevelFailed();
                } else if (messageKey.equals(surfaceViewGame_message_gameEnded)) {
                    interfaceGameCallback.onGameEnded();
                }

            }
        });

        gameThreadRunnable.setHighestScriptedLevel(interfaceGameCallback.onGetHighestScriptedLevel());
        surfaceViewGameThreadRunnable.setHighestScriptedLevel(interfaceGameCallback.onGetHighestScriptedLevel());
        surfaceViewGameThreadRunnable.setGameResultHighScoreCurrentLevel(gameResultHighScoreCurrentLevel);

        gameThread = new Thread(gameThreadRunnable);
        surfaceViewGameThread = new Thread(surfaceViewGameThreadRunnable);
        gameThreadRunnable.setThreadIsRunning(true);
        surfaceViewGameThreadRunnable.setThreadIsRunning(true);
        gameThread.start();
        surfaceViewGameThread.start();
    }

    private void stopThreads() {
        Log.d("lightningdots", "SurfaceViewGame stopThreads()");
        boolean retryGameThread = true;
        boolean retrySurfaceViewGameThread = true;
        gameThreadRunnable.setThreadIsRunning(false);
        surfaceViewGameThreadRunnable.setThreadIsRunning(false);
        while (retryGameThread || retrySurfaceViewGameThread) {
            try {
                gameThread.join();
                retryGameThread = false;
            } catch (InterruptedException e) {
            }
            try {
                surfaceViewGameThread.join();
                retrySurfaceViewGameThread = false;
            } catch (InterruptedException e) {
            }
        }

    }

    public void pauseThreads() {
        Log.d("lightningdots", "SurfaceViewGame pauseThreads()");
        if (gameThreadRunnable != null) {
            gameThreadRunnable.setThreadIsPaused(true);
        }
        if (surfaceViewGameThreadRunnable != null) {
            surfaceViewGameThreadRunnable.setThreadIsPaused(true);
        }
    }

    public void resumeThreads() {
        Log.d("lightningdots", "SurfaceViewGame resumeThreads()");
        if (surfaceViewGameThreadRunnable != null) {
            surfaceViewGameThreadRunnable.setThreadIsPaused(false);
        }
        if (gameThreadRunnable != null) {
            gameThreadRunnable.setThreadIsPaused(false);
        }
    }

}
