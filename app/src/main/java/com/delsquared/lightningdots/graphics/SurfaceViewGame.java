package com.delsquared.lightningdots.graphics;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.game.GameThreadRunnable;
import com.delsquared.lightningdots.game.InterfaceGameCallback;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class SurfaceViewGame
		extends SurfaceView
		implements SurfaceHolder.Callback {
    private static final String CLASS_NAME = SurfaceViewGame.class.getSimpleName();

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

	private final Context context;

    // For swipes
    private float touchX1;
    @SuppressWarnings("FieldCanBeLocal")
    private float touchX2;
    private static final int MINIMUM_SWIPE_DISTANCE = 150;

    public SurfaceViewGame(Context context) {
        super(context);
        String methodName = CLASS_NAME + ".constructor(Context context)";
        UtilityFunctions.logDebug(methodName, "Entered");

		this.context = context;

        registerSurfaceHolder();

    }

	public SurfaceViewGame(Context context, AttributeSet attrs) {
		super(context, attrs);
        String methodName = CLASS_NAME + ".constructor(Context context, AttributeSet attrs)";
        UtilityFunctions.logDebug(methodName, "Entered");

		this.context = context;

        registerSurfaceHolder();

	}

	public SurfaceViewGame(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        String methodName = CLASS_NAME + ".constructor(Context context, AttributeSet attrs, int defStyle)";
        UtilityFunctions.logDebug(methodName, "Entered");

		this.context = context;

        registerSurfaceHolder();

	}

    private void registerSurfaceHolder() {
        String methodName = CLASS_NAME + ".registerSurfaceHolder";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Register our interest in hearing about changes to our surface
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
        String methodName = CLASS_NAME + ".surfaceCreated";
        UtilityFunctions.logDebug(methodName, "Entered");

        startThreads();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        String methodName = CLASS_NAME + ".surfaceChanged";
        UtilityFunctions.logDebug(methodName, "Entered");

		surfaceViewGameThreadRunnable.setSurfaceSize(width, height);
        gameThreadRunnable.setCanvasWidthAndHeight(width, height);

    }

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
        String methodName = CLASS_NAME + ".onWindowFocusChanged";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (!hasWindowFocus) {
            pauseThreads();
        } else {
            resumeThreads();
        }
	}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        String methodName = CLASS_NAME + ".surfaceDestroyed";
        UtilityFunctions.logDebug(methodName, "Entered");

        stopThreads();

    }

    @Override
    public boolean performClick() {
        // Calls the super implementation, which generates an AccessibilityEvent
        // and calls the onClick() listener on the view, if any
        super.performClick();

        // Handle the action for the custom click here

        return true;
    }

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent){
        if (Game.getInstance().gameSnapshot.getGameState() == Game.GameState.RUNNING) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
               gameThreadRunnable.processDownTouch(motionEvent);
            }
        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                touchX1 = motionEvent.getX();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                touchX2 = motionEvent.getX();
                float deltaX = touchX2 - touchX1;
                if (Math.abs(deltaX) > MINIMUM_SWIPE_DISTANCE) {
                    surfaceViewGameThreadRunnable.processSwipe(deltaX);
                } else {
                    boolean gameThreadTouchProcessResult =
                            surfaceViewGameThreadRunnable.processDownTouch(motionEvent);
                    if (!gameThreadTouchProcessResult) {
                        gameThreadRunnable.processDownTouch(motionEvent);
                    }
                    performClick();
                }
            }
        }
		return true;
	}

    private void startThreads() {
        String methodName = CLASS_NAME + ".startThreads";
        UtilityFunctions.logDebug(methodName, "Entered");

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
                        , new Handler(Looper.getMainLooper()) {

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
                , new Handler(Looper.getMainLooper()) {

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
                switch (messageKey) {
                    case surfaceViewGame_message_initializeGame:

                        // Make sure the surface view game thread exists
                        if (surfaceViewGameThreadRunnable != null) {

                            // Reset the user clicks bitmap and canvas
                            surfaceViewGameThreadRunnable.initializeGame();

                        }

                        break;
                    case surfaceViewGame_message_levelCompleted:

                        int nextLevel = bundle.getInt(messageKey);
                        interfaceGameCallback.onLevelCompleted(nextLevel);
                        surfaceViewGameThreadRunnable.setGameResultHighScoreCurrentLevel(
                                interfaceGameCallback.onGetGameResultHighScoreCurrentLevel());
                        break;
                    case surfaceViewGame_message_levelFailed:
                        interfaceGameCallback.onLevelFailed();
                        break;
                    case surfaceViewGame_message_gameEnded:
                        interfaceGameCallback.onGameEnded();
                        break;
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
        String methodName = CLASS_NAME + ".stopThreads";
        UtilityFunctions.logDebug(methodName, "Entered");

        boolean retryGameThread = true;
        boolean retrySurfaceViewGameThread = true;
        gameThreadRunnable.setThreadIsRunning(false);
        surfaceViewGameThreadRunnable.setThreadIsRunning(false);
        while (retryGameThread || retrySurfaceViewGameThread) {
            try {
                gameThread.join();
                retryGameThread = false;
            } catch (InterruptedException e) {
                UtilityFunctions.logError(methodName, "Exception while attempting gameThread.join()", e);
            }
            try {
                surfaceViewGameThread.join();
                retrySurfaceViewGameThread = false;
            } catch (InterruptedException e) {
                UtilityFunctions.logError(methodName, "Exception while attempting surfaceViewGameThread.join()", e);
            }
        }

    }

    public void pauseThreads() {
        String methodName = CLASS_NAME + ".pauseThreads";
        UtilityFunctions.logDebug(methodName, "Entered");
        if (gameThreadRunnable != null) {
            gameThreadRunnable.setThreadIsPaused(true);
        }
        if (surfaceViewGameThreadRunnable != null) {
            surfaceViewGameThreadRunnable.setThreadIsPaused(true);
        }
    }

    public void resumeThreads() {
        String methodName = CLASS_NAME + ".resumeThreads";
        UtilityFunctions.logDebug(methodName, "Entered");
        if (surfaceViewGameThreadRunnable != null) {
            surfaceViewGameThreadRunnable.setThreadIsPaused(false);
        }
        if (gameThreadRunnable != null) {
            gameThreadRunnable.setThreadIsPaused(false);
        }
    }

}
