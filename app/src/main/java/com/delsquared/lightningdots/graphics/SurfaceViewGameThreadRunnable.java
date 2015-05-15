package com.delsquared.lightningdots.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.game.ClickTargetSnapshot;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.game.GameSnapshot;
import com.delsquared.lightningdots.game.GameThreadSharedData;
import com.delsquared.lightningdots.game.SurfaceViewGameThreadSharedData;
import com.delsquared.lightningdots.game.UserClick;

import java.util.ArrayList;
import java.util.HashMap;

public class SurfaceViewGameThreadRunnable implements Runnable {

    // Variables for frames per second calculations
	public static final long maximumAverageTimeBetweenFramesMillis = 33;
	public static final long targetAverageTimeBetweenFramesMillis = 16;
	public static final int maximumNumberOfFramesPerSecondPolls = 10;
	private double currentAverageTimeBetweenFramesMillis = 0.0;
	private int currentNumberOfFramePolls = -1;
	private long lastFrameTimeMillis = 0;
	private long[] timeBetweenFramesRegister = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	private SurfaceHolder surfaceHolder;
    private Context context;
	private boolean threadIsRunning = false;
    private boolean threadIsPaused = false;
    private Handler handler;

	private Game game;
    private GameResult gameResultHighScoreCurrentLevel;
    private int highestScriptedLevel;
    private int currentHighestValidUserClickIndex = 0;
    private GameSnapshot oldGameSnapshot = null;

	private int canvasWidth;
	private int canvasHeight;

	// ---------- Paint Objects ---------- //
	// Background paints
	private Paint paintBackground;
    private Paint paintGameOverAlphaOverlay;
	private Paint endBorderPaintLoss;
    private Paint endBorderPaintWin;

    private int gameOverAlphaOverlayAlpha = 150;
    private int intermediateClickTargetAlpha = 50;

	// Target paint
    private Paint paintIntermediateTarget;
	private Paint paintTarget;

	// User click paints
	private Paint defaultPaint;

	private HashMap<String, Paint> mapPaint = new HashMap<String, Paint>();
    private HashMap<String, SurfaceViewTextHandler> mapSurfaceViewTextHandler = new HashMap<String, SurfaceViewTextHandler>();

	private float borderWidthFactor = 0.025f;
	private float borderPaddingFactor = 0.025f;
    private float textLineSpacingFactor = 0.025f;
	private float targetWidthFactor = 0.01f;
    private float textStartCountdownHeightFactor = 0.05f;
	private float textTimerHeightFactor = 0.05f;
	private float textCounterHeightFactor = 0.05f;
    private float textLevelDecrementHeightFactor = 0.05f;
    private float textLevelHeightFactor = 0.05f;
    private float textLevelIncrementHeightFactor = 0.05f;
    private float textNewHighScoreHeightFactor = 0.05f;
    private float textLevelCompleteHeightFactor = 0.05f;
    private float textLevelFailedHeightFactor = 0.05f;
    private float textFPSHeightFactor = 0.025f;
    private float textHighScoreHeightFactor = 0.05f;
	private float textOtherTextHeightFactor = 0.05f;
    private float textCongratulationsHeightFactor = 0.05f;
    private float textBeatLastLevelHeighFactor = 0.05f;

	private int borderWidth = 1;
	private int borderPadding = 1;
    private int textLineSpacing = 1;
	private int targetWidth = 1;
	private int canvasPlayableWidth = 1;
	private int canvasPlayableHeight = 1;

    private int bitmapAwardX = 0;
    private int bitmapAwardY = 0;

	private Bitmap bitmapUserClicks;
	private Canvas canvasUserClicks;

    // The award bitmaps
    private Bitmap bitmapAwardEmpty;
    private Bitmap bitmapAwardFull;
    private int bitmapAwardEmptyWidth = 1;
    private int bitmapAwardEmptyHeight = 1;
    private int bitmapAwardFullWidth = 1;
    private int bitmapAwardFullHeight = 1;

    /*
    // Text literals
	private String textGameReadyToStart1;
	private String textGameReadyToStart2;
	private String textGameEnded1;
	private String textGameEnded2;
    private String textNewHighScore;
    private String textLevelComplete;
    */

    public SurfaceViewGameThreadRunnable(
            SurfaceHolder surfaceHolder
            , Context context
            , Handler handler) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
		this.game = Game.getInstance();
        //this.lockThreadIsPaused = new Object();
        //this.threadIsPaused = false;
        //this.threadIsPaused = false;
        this.handler = handler;

		canvasWidth = 1;
		canvasHeight = 1;

		// ---------- Create our paint objects ---------- //

		// Create the background paint
		paintBackground = new Paint();
		paintBackground.setAntiAlias(true);
		paintBackground.setColor(context.getResources().getColor(R.color.black));

        // Create the game over alpha overlay paint
        paintGameOverAlphaOverlay = new Paint();
        paintGameOverAlphaOverlay.setAntiAlias(true);
        paintGameOverAlphaOverlay.setColor(context.getResources().getColor(R.color.black));
        paintGameOverAlphaOverlay.setAlpha(gameOverAlphaOverlayAlpha);

		// Add the loss end border paint
		endBorderPaintLoss = new Paint(Paint.ANTI_ALIAS_FLAG);
		endBorderPaintLoss.setStyle(Paint.Style.FILL);
		endBorderPaintLoss.setColor(context.getResources().getColor(R.color.red));

        // Add the win end border paint
        endBorderPaintWin = new Paint(Paint.ANTI_ALIAS_FLAG);
        endBorderPaintWin.setStyle(Paint.Style.FILL);
        endBorderPaintWin.setColor(context.getResources().getColor(R.color.green));

        // Add the intermediate target paint
        paintIntermediateTarget = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintIntermediateTarget.setStyle(Paint.Style.STROKE);
        paintIntermediateTarget.setColor(context.getResources().getColor(R.color.android_light_blue));
        paintIntermediateTarget.setAlpha(intermediateClickTargetAlpha);

		// Add the target paint
		paintTarget = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintTarget.setStyle(Paint.Style.STROKE);
		paintTarget.setColor(context.getResources().getColor(R.color.android_light_blue));

		// Create the default user click paint
		defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		defaultPaint.setStyle(Paint.Style.FILL);
		defaultPaint.setColor(context.getResources().getColor(R.color.yellow));
		mapPaint.put("default", defaultPaint);

        // Create the start countdown text handler
        Paint paintTextStartCountdown = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextStartCountdown.setColor(context.getResources().getColor(R.color.Orange));
        paintTextStartCountdown.setTextSize(1);
        SurfaceViewTextHandler textHandlerStartCountdown = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_template_start_countdown)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textStartCountdownHeightFactor
                , canvasHeight
                , paintTextStartCountdown);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_start_countdown)
                , textHandlerStartCountdown);

        // Create the timer text handler
        Paint paintTextTimer = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextTimer.setColor(context.getResources().getColor(R.color.Orange));
        paintTextTimer.setTextSize(1);
        SurfaceViewTextHandler textHandlerTimer = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_template_timer)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textTimerHeightFactor
                , canvasHeight
                , paintTextTimer);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_timer)
                , textHandlerTimer);

        // Create the counter text handler
        Paint paintTextCounter = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextCounter.setColor(context.getResources().getColor(R.color.Orange));
        paintTextCounter.setTextSize(1);
        SurfaceViewTextHandler textHandlerCounter = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_template_counter)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.RIGHT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textCounterHeightFactor
                , canvasHeight
                , paintTextCounter);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_counter)
                , textHandlerCounter);

        // Create the counter (goal achieved) text handler
        Paint paintTextCounterGoalAchieved = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextCounterGoalAchieved.setColor(context.getResources().getColor(R.color.green));
        paintTextCounterGoalAchieved.setTextSize(1);
        SurfaceViewTextHandler textHandlerCounterGoalAchieved = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_template_counter)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.RIGHT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textCounterHeightFactor
                , canvasHeight
                , paintTextCounterGoalAchieved);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_counter_goal_achieved)
                , textHandlerCounterGoalAchieved);

        // Create the level decrement text handler
        Paint paintTextLevelDecrement = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextLevelDecrement.setColor(context.getResources().getColor(R.color.android_light_blue));
        paintTextLevelDecrement.setTextSize(1);
        SurfaceViewTextHandler textHandlerLevelDecrement = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_level_decrement)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.BOTTOM
                , 0.0
                , 0.0
                , textLevelDecrementHeightFactor
                , canvasHeight
                , paintTextLevelDecrement);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_level_decrement)
                , textHandlerLevelDecrement);

        // Create the level text handler
        Paint paintTextLevel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextLevel.setColor(context.getResources().getColor(R.color.Orange));
        paintTextLevel.setTextSize(1);
        SurfaceViewTextHandler textHandlerLevel = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_template_current_level)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.BOTTOM
                , 0.0
                , 0.0
                , textLevelHeightFactor
                , canvasHeight
                , paintTextLevel);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_level)
                , textHandlerLevel);

        // Create the level decrement text handler
        Paint paintTextLevelIncrement = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextLevelIncrement.setColor(context.getResources().getColor(R.color.android_light_blue));
        paintTextLevelIncrement.setTextSize(1);
        SurfaceViewTextHandler textHandlerLevelIncrement = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_level_increment)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.BOTTOM
                , 0.0
                , 0.0
                , textLevelIncrementHeightFactor
                , canvasHeight
                , paintTextLevelIncrement);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_level_increment)
                , textHandlerLevelIncrement);

        // Create the new high score text handler
        Paint paintTextNewHighScore = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextNewHighScore.setColor(context.getResources().getColor(R.color.Orange));
        paintTextNewHighScore.setTextSize(1);
        SurfaceViewTextHandler textHandlerNewHighScore = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_new_high_score)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textNewHighScoreHeightFactor
                , canvasHeight
                , paintTextNewHighScore);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_new_high_score)
                , textHandlerNewHighScore);

        // Create the level complete text handler
        Paint paintTextLevelComplete = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextLevelComplete.setColor(context.getResources().getColor(R.color.Orange));
        paintTextLevelComplete.setTextSize(1);
        SurfaceViewTextHandler textHandlerLevelComplete = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_level_complete)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textLevelCompleteHeightFactor
                , canvasHeight
                , paintTextLevelComplete);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_level_complete)
                , textHandlerLevelComplete);

        // Create the level failed text handler
        Paint paintTextLevelFailed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextLevelFailed.setColor(context.getResources().getColor(R.color.red));
        paintTextLevelFailed.setTextSize(1);
        SurfaceViewTextHandler textHandlerLevelFailed = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_level_failed)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textLevelFailedHeightFactor
                , canvasHeight
                , paintTextLevelFailed);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_level_failed)
                , textHandlerLevelFailed);

        // Create the fps text handler
        Paint paintTextFPS = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextFPS.setColor(context.getResources().getColor(R.color.green));
        paintTextFPS.setTextSize(1);
        SurfaceViewTextHandler textHandlerFPS = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_template_fps)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.RIGHT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.BOTTOM
                , 0.0
                , 0.0
                , textFPSHeightFactor
                , canvasHeight
                , paintTextFPS);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_fps)
                , textHandlerFPS);

        // Create the high score text handler
        Paint paintTextHighScore = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextHighScore.setColor(context.getResources().getColor(R.color.Orange));
        paintTextHighScore.setTextSize(1);
        SurfaceViewTextHandler textHandlerHighScore = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_template_high_score)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textHighScoreHeightFactor
                , canvasHeight
                , paintTextHighScore);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_high_score)
                , textHandlerHighScore);

        // Create the other text handler
        Paint paintTextOther = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextOther.setColor(context.getResources().getColor(R.color.green));
        paintTextOther.setTextSize(1);
        SurfaceViewTextHandler textHandlerOther = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_stopped_tap_to_start)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.BOTTOM
                , 0.0
                , 0.0
                , textOtherTextHeightFactor
                , canvasHeight
                , paintTextOther);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_other)
                , textHandlerOther);

        // Create the congratulations text handler
        Paint paintTextCongratulations = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextCongratulations.setColor(context.getResources().getColor(R.color.green));
        paintTextCongratulations.setTextSize(1);
        SurfaceViewTextHandler textHandlerCongratulations = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_congratulations)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textCongratulationsHeightFactor
                , canvasHeight
                , paintTextCongratulations);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_congratulations)
                , textHandlerCongratulations);

        // Create the beat last level text handler
        Paint paintTextBeatLastLevel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextBeatLastLevel.setColor(context.getResources().getColor(R.color.green));
        paintTextBeatLastLevel.setTextSize(1);
        SurfaceViewTextHandler textHandlerBeatLastLevel = new SurfaceViewTextHandler(
                context.getString(R.string.game_text_beat_last_level)
                , SurfaceViewTextHandler.JUSTIFY_HORIZONTAL.LEFT
                , SurfaceViewTextHandler.JUSTIFY_VERTICAL.TOP
                , 0.0
                , 0.0
                , textBeatLastLevelHeighFactor
                , canvasHeight
                , paintTextBeatLastLevel);
        mapSurfaceViewTextHandler.put(
                context.getString(R.string.graphics_textkey_beat_last_level)
                , textHandlerBeatLastLevel);

        // Get the award bitmaps
        bitmapAwardEmpty = BitmapFactory.decodeResource(context.getResources(), R.drawable.award_black);
        bitmapAwardFull = BitmapFactory.decodeResource(context.getResources(), R.drawable.award_yellow);
        bitmapAwardEmptyHeight = bitmapAwardEmpty.getHeight();
        bitmapAwardEmptyWidth = bitmapAwardEmpty.getWidth();
        bitmapAwardFullHeight = bitmapAwardFull.getHeight();
        bitmapAwardFullWidth = bitmapAwardFull.getWidth();

        /*
		// Get the literal message strings
		textGameReadyToStart1 = context.getString(R.string.game_readytostart_message1);
		textGameReadyToStart2 = context.getString(R.string.game_readytostart_message2);
		textGameEnded1 = context.getString(R.string.game_ended_message1);
		textGameEnded2 = context.getString(R.string.game_ended_message2);
        textNewHighScore = context.getString(R.string.game_text_new_high_score);
        textLevelComplete = context.getString(R.string.game_text_level_complete);
        */

    }


    public synchronized void setThreadIsRunning(boolean threadIsRunning) {
        this.threadIsRunning = threadIsRunning;
    }

    public synchronized void setThreadIsPaused(boolean threadIsPaused) {
        this.threadIsPaused = threadIsPaused;
    }

    public synchronized void setGameResultHighScoreCurrentLevel(GameResult gameResultHighScoreCurrentLevel) {
        this.gameResultHighScoreCurrentLevel = gameResultHighScoreCurrentLevel;
    }

    public synchronized void setHighestScriptedLevel(int highestScriptedLevel) {
        this.highestScriptedLevel = highestScriptedLevel;
    }

    public void initializeGame() {
        currentHighestValidUserClickIndex = 0;
        resetUserClicksBitmapAndCanvas();
    }

    @Override
    public void run() {

        while (threadIsRunning) {

            if (threadIsPaused == false) {

                // Get the game snapshot
                GameSnapshot gameSnapshot = game.gameSnapshot;

                try {

                    if (gameSnapshot != null) {

                        boolean blah = gameSnapshot.equals(oldGameSnapshot);

                        // Check if the game snapshot is different from the old one
                        if (true) {

                            /// Perform drawing
                            // Initialize the canvas to null
                            Canvas canvas = null;

                            try {

                                // Lock the canvas for drawing
                                canvas = surfaceHolder.lockCanvas(null);

                                synchronized (surfaceHolder) {

                                    // Update frames per second
                                    updateAverageTimeBetweenFrames();

                                    // Draw the canvas
                                    doDraw(canvas);

                                    // Update the shared data
                                    updateSurfaceViewGameThreadSharedData();

                                }

                            } catch (Exception e) {

                                int asdf = 0;

                            } finally {

                                // Check if the canvas is not null
                                if (canvas != null) {

                                    // Unlock the canvas and post
                                    surfaceHolder.unlockCanvasAndPost(canvas);
                                }

                            }

                        }

                    }

                } catch (Exception e) {

                } finally {

                    // Set the old game snapshot to the current one
                    oldGameSnapshot = gameSnapshot;

                }

            }

        }

    }

	private void doDraw(Canvas canvas) {

		// Get the game snapshot
		GameSnapshot gameSnapshot = game.gameSnapshot;
		GameThreadSharedData gameThreadSharedData = game.gameThreadSharedData;

		// Get shared game data
		long currentTimeMillis = gameThreadSharedData.getCurrentTimeMillis();

		// Get game snapshot data
		Game.GameType gameType = gameSnapshot.getGameType();
		Game.GameState gameState = gameSnapshot.getGameState();
		long endTimeMillis = gameSnapshot.getEndTimeMillis();
        ArrayList<Integer> arrayListTargetUserClicks = gameSnapshot.getArrayListTargetUserClicks();
        boolean isLevelComplete = gameSnapshot.getIsLevelComplete();
        int awardLevel = gameSnapshot.getAwardLevel();
        boolean isNewHighScore = gameSnapshot.getIsNewHighScore();
		ClickTargetSnapshot clickTargetSnapshot = gameSnapshot.getCurrentClickTargetSnapshot();

        //Calculate the framerate
        double framerate = currentAverageTimeBetweenFramesMillis;
        if (currentAverageTimeBetweenFramesMillis != 0.0) {
            framerate = 1000.0 / currentAverageTimeBetweenFramesMillis;
        }

        // Get the current level high score
        int currentLevelHighScore =
                (gameResultHighScoreCurrentLevel == null) ?
                        0
                        : gameResultHighScoreCurrentLevel.getUserClicks();

        // Get the current user click target
        int awardLevelAchieved = 0;
        int userClickTarget = 0;
        for (int currentUserClickTarget : arrayListTargetUserClicks) {
            userClickTarget = currentUserClickTarget;
            if (game.getListValidUserClick().size() < userClickTarget)
                break;
            awardLevelAchieved++;
        }

        // Get the text handlers
        SurfaceViewTextHandler textHandlerStartCountdown =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_start_countdown));
        SurfaceViewTextHandler textHandlerTimer =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_timer));
        SurfaceViewTextHandler textHandlerCounter = (awardLevelAchieved == 0) ?
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_counter))
                : mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_counter_goal_achieved));
        SurfaceViewTextHandler textHandlerLevelDecrement =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_decrement));
        SurfaceViewTextHandler textHandlerLevel =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level));
        SurfaceViewTextHandler textHandlerLevelIncrement =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_increment));
        SurfaceViewTextHandler textHandlerNewHighScore =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_new_high_score));
        SurfaceViewTextHandler textHandlerLevelComplete =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_complete));
        SurfaceViewTextHandler textHandlerLevelFailed =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_failed));
        SurfaceViewTextHandler textHandlerFPS =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_fps));
        SurfaceViewTextHandler textHandlerHighScore =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_high_score));
        SurfaceViewTextHandler textHandlerOther =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_other));
        SurfaceViewTextHandler textHandlerCongratulations =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_congratulations));
        SurfaceViewTextHandler textHandlerBeatLastLevel =
                mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_beat_last_level));

        // Set the text
        textHandlerStartCountdown.setText(
                String.format(
                        context.getString(R.string.game_text_template_start_countdown)
                        , gameSnapshot.getDisplayStartTime()));
        textHandlerTimer.setText(
                String.format(
                        context.getString(R.string.game_text_template_timer)
                        , gameSnapshot.getDisplayTimeRemaining()));
        if (userClickTarget > 0) {
            textHandlerCounter.setText(
                    String.format(
                            context.getString(R.string.game_text_template_counter_with_goal)
                            , game.getListValidUserClick().size()
                            , userClickTarget));
        } else {
            textHandlerCounter.setText(
                    String.format(
                            context.getString(R.string.game_text_template_counter)
                            , game.getListValidUserClick().size()));
        }
        textHandlerLevel.setText(
                String.format(
                        context.getString(R.string.game_text_template_current_level)
                        , game.getGameLevel()));
        textHandlerFPS.setText(
                String.format(
                        context.getString(R.string.game_text_template_fps)
                        , framerate));
        textHandlerHighScore.setText(
                String.format(
                        context.getString(R.string.game_text_template_high_score)
                        , currentLevelHighScore));

        // The status text
        switch (gameState) {
            case ENDED:
                if (isLevelComplete == true) {
                    textHandlerOther.setText(
                            context.getString(R.string.game_text_ended_continue));
                } else {
                    textHandlerOther.setText(
                            context.getString(R.string.game_text_ended_retry));
                }
                break;

            case STOPPED:
                textHandlerOther.setText(
                        context.getString(R.string.game_text_stopped_tap_to_start));
                break;

            case ENDING:
                textHandlerOther.setText(
                        context.getString(R.string.game_text_ending_game_over));
                break;

            default:
                textHandlerOther.setText("");
        }

		// Get the time elapsed since the game end
		long timeElapsedSinceGameEndMillis = currentTimeMillis - endTimeMillis;

		// ---------- BEGIN BACKGROUND DRAWING ---------- //

		// Redraw the background
		canvas.drawColor(context.getResources().getColor(R.color.black));

		if (gameState == Game.GameState.ENDING) {

            // Check if this is a loss
            if (awardLevel == 0) {

                // Draw the red border
                canvas.drawRect(0, 0, borderWidth, canvasHeight, endBorderPaintLoss);
                canvas.drawRect(0, 0, canvasWidth, borderWidth, endBorderPaintLoss);
                canvas.drawRect(0, canvasHeight - borderWidth, canvasWidth, canvasHeight, endBorderPaintLoss);
                canvas.drawRect(canvasWidth - borderWidth, 0, canvasWidth, canvasHeight, endBorderPaintLoss);

            } else { // This is a win

                // Draw the green border
                canvas.drawRect(0, 0, borderWidth, canvasHeight, endBorderPaintWin);
                canvas.drawRect(0, 0, canvasWidth, borderWidth, endBorderPaintWin);
                canvas.drawRect(0, canvasHeight - borderWidth, canvasWidth, canvasHeight, endBorderPaintWin);
                canvas.drawRect(canvasWidth - borderWidth, 0, canvasWidth, canvasHeight, endBorderPaintWin);

            }


		}

		// ---------- END BACKGROUND DRAWING ---------- //


		// ---------- BEGIN USER CLICK DRAWING ---------- //

		// Make sure the user click bitmap is not null
		if (bitmapUserClicks != null && canvasUserClicks != null) {

			//ArrayList<UserClick> newUserClickList = game.getListNewUserClick();

            try {

                for (
                        int currentUserClickIndex = currentHighestValidUserClickIndex;
                        currentUserClickIndex < game.mapValidUserClick.size();
                        currentUserClickIndex++) {

                    String currentUserClickKey = Integer.toString(currentUserClickIndex + 1);
                    UserClick userClick = game.mapValidUserClick.get(currentUserClickKey);

                    // Check if this is a valid user click
                    if (userClick.getIsInsideTarget() == true) {

                        // Get the paint for this click
                        Paint currentPaint = defaultPaint;
                        String currentPaintString = userClick.getPaintString();
                        if (mapPaint.containsKey(currentPaintString)) {
                            currentPaint = mapPaint.get(currentPaintString);
                        }

                        // Draw the oval
                        canvasUserClicks.drawOval(
                                userClick.getBoundingRectangle()
                                , currentPaint);

                    }

                    // Remove the click from the list
                    //newUserClickList.remove(userClick);

                    currentHighestValidUserClickIndex++;

                }

            } catch (Exception e) {
                // TODO: Error handling
            }

			// Finally, draw the user clicks bitmap to the canvas
			canvas.drawBitmap(bitmapUserClicks, 0, 0, null);

		}

		// ---------- END USER CLICK DRAWING ---------- //

		// ---------- BEGIN TARGET DRAWING ---------- //
		// Check if the game type is agility
		if (gameType == Game.GameType.AGILITY) {

			if (gameState == Game.GameState.STARTING
					|| gameState == Game.GameState.RUNNING
					|| gameState == Game.GameState.PAUSED) {

				// Draw the target
                clickTargetSnapshot.draw(canvas, paintTarget);

			}

		}
		// ---------- END TARGET DRAWING ----------//


		// ---------- BEGIN FOREGROUND DRAWING ---------- //

		// Draw the time
        textHandlerTimer.drawText(canvas);

		// Draw the count
        textHandlerCounter.drawText(canvas);

        if (gameState == Game.GameState.STARTING) {

            // Draw the countdown
            textHandlerStartCountdown.drawText(canvas);

        }

        // FPS Text
        textHandlerFPS.drawText(canvas);

        // Level Text
        if (gameType == Game.GameType.AGILITY) {

            if (gameState == Game.GameState.STOPPED) {

                // Check if we can go down a level
                if (gameSnapshot.getGameLevel() > 1) {

                    // Draw the level decrement button
                    textHandlerLevelDecrement.drawText(canvas);

                }

                // Draw the level text
                textHandlerLevel.drawText(
                        canvas
                        , (float)textHandlerLevelDecrement.getTextWidth()
                        , 0.0f);

                // Check if we can go up a level
                if (gameResultHighScoreCurrentLevel != null
                        && gameSnapshot.getGameLevel() < gameResultHighScoreCurrentLevel.getGameLevel() + 1
                        && gameSnapshot.getGameLevel() < highestScriptedLevel) {

                    // Draw the level increment button
                    textHandlerLevelIncrement.drawText(
                            canvas
                            , (float)textHandlerLevelDecrement.getTextWidth()
                                    + (float)textHandlerLevel.getTextWidth()
                            , 0.0f);

                }

                // Check if we have already completed this level
                if (gameResultHighScoreCurrentLevel != null
                        && gameResultHighScoreCurrentLevel.getAwardLevel() > 0) {

                    // Draw the high score for this level
                    textHandlerHighScore.drawText(canvas);

                    // Draw the award for this level
                    int bestAwardLevelCurrentLevel = gameResultHighScoreCurrentLevel.getAwardLevel();
                    for (int i = 0; i < 4; i++) {
                        if (bestAwardLevelCurrentLevel > i) {
                            canvas.drawBitmap(bitmapAwardFull, bitmapAwardX + (bitmapAwardFullWidth * i), bitmapAwardY, null);
                        } else {
                            canvas.drawBitmap(bitmapAwardEmpty, bitmapAwardX + (bitmapAwardEmptyWidth * i), bitmapAwardY, null);
                        }
                    }

                }
            } else { // Game state is not STOPPED
                textHandlerLevel.drawText(canvas);
            }
        }

		// ---------- END FOREGROUND DRAWING ---------- //


        // ---------- BEGIN GAME OVER ALPHA OVERLAY DRAWING ---------- //

        if (gameState == Game.GameState.ENDED) {

            // Draw the alpha overlay
            canvas.drawRect(0, 0, canvasWidth, canvasHeight, paintGameOverAlphaOverlay);

        }

        // ---------- END GAME OVER ALPHA OVERLAY DRAWING ---------- //


		// ---------- BEGIN TOP LAYER DRAWING ---------- //

		if (gameState == Game.GameState.STOPPED) {

			// Draw the game ready text
            textHandlerOther.drawText(canvas);

		}

        if (gameState == Game.GameState.ENDING) {
            // Draw the game over text
            textHandlerOther.drawText(canvas);
        }

		if (gameState == Game.GameState.ENDED) {

            // Draw the play again text
            textHandlerOther.drawText(canvas);

            // Check if the level was completed
            if (isLevelComplete == true) {

                // Check if the current level is the highest scripted level
                if (gameSnapshot.getGameLevel() >= highestScriptedLevel
                        && gameType == Game.GameType.AGILITY) {

                    // Draw the congratulations text
                    textHandlerCongratulations.drawText(canvas);

                    // Draw the beat highest level text
                    textHandlerBeatLastLevel.drawText(canvas);

                } else {

                    // Draw the level complete text
                    textHandlerLevelComplete.drawText(canvas);

                }

            } else if (gameType == Game.GameType.AGILITY) { // The level was failed and it's agility

                // Draw the level failed text (can only pass a level in this game type)
                textHandlerLevelFailed.drawText(canvas);
            }

            // Check if there is a new high score
            if (isNewHighScore == true
                    && !(gameSnapshot.getGameLevel() >= highestScriptedLevel
                        && gameType == Game.GameType.AGILITY)) {

                // Draw the new high score text
                textHandlerNewHighScore.drawText(canvas);

            }

            // Draw the awards
            if (awardLevel > 0) {
                for (int i = 0; i < 4; i++) {
                    if (awardLevel > i) {
                        canvas.drawBitmap(bitmapAwardFull, bitmapAwardX + (bitmapAwardFullWidth * i), bitmapAwardY, null);
                    } else {
                        canvas.drawBitmap(bitmapAwardEmpty, bitmapAwardX + (bitmapAwardEmptyWidth * i), bitmapAwardY, null);
                    }
                }
            }

		}

		// ---------- END TOP LAYER DRAWING ---------- //

	}

	public void setSurfaceSize(int width, int height) {

		// synchronized to make sure these all change atomically
		synchronized (surfaceHolder) {

            // Set the canvas width and height
            canvasWidth = width;
            canvasHeight = height;

            // Calculate widths
            borderWidth = Math.min((int) (width * borderWidthFactor), (int) (height * borderWidthFactor));
            borderPadding = Math.min((int) (width * borderPaddingFactor), (int) (height * borderPaddingFactor));
            textLineSpacing = Math.min((int) (width * textLineSpacingFactor), (int) (height * textLineSpacingFactor));
            targetWidth = Math.min((int) (width * targetWidthFactor), (int) (height * targetWidthFactor));

            // Set the target stroke width
            paintIntermediateTarget.setStrokeWidth((float) targetWidth);
            paintTarget.setStrokeWidth((float) targetWidth);

            // Calculate the playable width
            canvasPlayableWidth = width - borderWidth - borderWidth - borderPadding - borderPadding;
            canvasPlayableHeight = height - borderWidth - borderWidth - borderPadding - borderPadding;

            // Create the user click bitmap and canvas
            resetUserClicksBitmapAndCanvas();

            // Redraw user clicks
            redrawUserClicks();

            // Get the text handlers
            SurfaceViewTextHandler textHandlerStartCountdown =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_start_countdown));
            SurfaceViewTextHandler textHandlerTimer =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_timer));
            SurfaceViewTextHandler textHandlerCounter =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_counter));
            SurfaceViewTextHandler textHandlerCounterGoalAchieved =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_counter_goal_achieved));
            SurfaceViewTextHandler textHandlerLevelDecrement =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_decrement));
            SurfaceViewTextHandler textHandlerLevel =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level));
            SurfaceViewTextHandler textHandlerLevelIncrement =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_increment));
            SurfaceViewTextHandler textHandlerNewHighScore =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_new_high_score));
            SurfaceViewTextHandler textHandlerLevelComplete =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_complete));
            SurfaceViewTextHandler textHandlerLevelFailed =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_failed));
            SurfaceViewTextHandler textHandlerFPS =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_fps));
            SurfaceViewTextHandler textHandlerHighScore =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_high_score));
            SurfaceViewTextHandler textHandlerOther =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_other));
            SurfaceViewTextHandler textHandlerCongratulations =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_congratulations));
            SurfaceViewTextHandler textHandlerBeatLastLevel =
                    mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_beat_last_level));

            // Recalculate the text heights
            textHandlerStartCountdown.recalculateTextHeight(height);
            textHandlerTimer.recalculateTextHeight(height);
            textHandlerCounter.recalculateTextHeight(height);
            textHandlerCounterGoalAchieved.recalculateTextHeight(height);
            textHandlerLevelDecrement.recalculateTextHeight(height);
            textHandlerLevel.recalculateTextHeight(height);
            textHandlerLevelIncrement.recalculateTextHeight(height);
            textHandlerNewHighScore.recalculateTextHeight(height);
            textHandlerLevelComplete.recalculateTextHeight(height);
            textHandlerLevelFailed.recalculateTextHeight(height);
            textHandlerFPS.recalculateTextHeight(height);
            textHandlerHighScore.recalculateTextHeight(height);
            textHandlerOther.recalculateTextHeight(height);
            textHandlerCongratulations.recalculateTextHeight(height);
            textHandlerBeatLastLevel.recalculateTextHeight(height);

            // Recalculate the text positions
            textHandlerStartCountdown.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                    + textLineSpacing);
            textHandlerTimer.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding);
            textHandlerCounter.setPosition(
                    width - borderWidth - borderPadding
                    , borderWidth + borderPadding);
            textHandlerCounterGoalAchieved.setPosition(
                    width - borderWidth - borderPadding
                    , borderWidth + borderPadding);
            textHandlerLevelDecrement.setPosition(
                    borderWidth + borderPadding
                    , height - borderWidth - borderPadding);
            textHandlerLevel.setPosition(
                    borderWidth + borderPadding
                    , height - borderWidth - borderPadding);
            textHandlerLevelIncrement.setPosition(
                    borderWidth + borderPadding
                    , height - borderWidth - borderPadding);
            textHandlerNewHighScore.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                        + textLineSpacing + textHandlerLevelComplete.getTextHeight()
                        + textLineSpacing);
            textHandlerLevelComplete.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                        + textLineSpacing);
            textHandlerLevelFailed.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                            + textLineSpacing);
            textHandlerFPS.setPosition(
                    width - borderWidth - borderPadding
                    , height - borderWidth - borderPadding);
            textHandlerHighScore.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                            + textLineSpacing + textHandlerLevelComplete.getTextHeight()
                            + textLineSpacing);
            textHandlerOther.setPosition(
                    borderWidth + borderPadding
                    , height - borderWidth - borderPadding - textHandlerLevel.getTextHeight()
                        - textLineSpacing);
            textHandlerCongratulations.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                    + textLineSpacing);
            textHandlerBeatLastLevel.setPosition(
                    borderWidth + borderPadding
                    , borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                    + textLineSpacing + textHandlerLevelComplete.getTextHeight()
                    + textLineSpacing);

            // Set the position for the awards
            bitmapAwardX = borderWidth + borderPadding;
            bitmapAwardY = (int) (borderWidth + borderPadding + textHandlerTimer.getTextHeight()
                    + textLineSpacing + textHandlerLevelComplete.getTextHeight()
                    + textLineSpacing + textHandlerNewHighScore.getTextHeight()
                    + textLineSpacing);

            // Update the shared data
            updateSurfaceViewGameThreadSharedData();

            // Set the canvas size changed flag to true
            game.canvasSizeChanged = true;

		}

	}

	public void resetUserClicksBitmapAndCanvas() {

		// Create the new user clicks bitmap
		bitmapUserClicks = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);

		// Create the new user clicks canvas
		canvasUserClicks = new Canvas(bitmapUserClicks);

		// Draw the transparent background color
		canvasUserClicks.drawColor(Color.TRANSPARENT);

	}

	private void redrawUserClicks() {

		// Make sure we have a bitmap and canvas
		if (bitmapUserClicks != null && canvasUserClicks != null) {

			// Check if we have a game object
			if (game != null) {

                try {

                    for (int currentUserClickIndex = 0; currentUserClickIndex < game.mapValidUserClick.size(); currentUserClickIndex++) {
                        //for (UserClick userClick : game.getListValidUserClick()) {

                        // Get the current user click
                        String currentUserClickKey = Integer.toString(currentUserClickIndex + 1);
                        UserClick userClick = game.mapValidUserClick.get(currentUserClickKey);

                        // Get the paint for this click
                        Paint currentPaint = defaultPaint;
                        String currentPaintString = userClick.getPaintString();
                        if (mapPaint.containsKey(currentPaintString)) {
                            currentPaint = mapPaint.get(currentPaintString);
                        }

                        // Draw the oval
                        canvasUserClicks.drawOval(
                                userClick.getBoundingRectangle()
                                , currentPaint);

                    }

                } catch (Exception e) {
                    // TODO: Error handling
                }

			}

		}

	}

    public void processSwipe(float deltaX) {

        // Get the game snapshot
        GameSnapshot gameSnapshot = game.gameSnapshot;

        if (gameSnapshot != null
                && gameSnapshot.getGameType() == Game.GameType.AGILITY) {

            Game.GameState gameState = gameSnapshot.getGameState();
            switch (gameState) {

                case STOPPED:
                    // Check for left swipe
                    if (deltaX > 0.0) {

                        // Send the message to decrement the level
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString(
                                SurfaceViewGame.surfaceViewGame_message_buttonPressed_decrementLevel
                                , SurfaceViewGame.surfaceViewGame_message_buttonPressed_decrementLevel);
                        message.setData(bundle);
                        handler.sendMessage(message);

                    } else {

                        // Send the message to increment the level
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString(
                                SurfaceViewGame.surfaceViewGame_message_buttonPressed_incrementLevel
                                , SurfaceViewGame.surfaceViewGame_message_buttonPressed_incrementLevel);
                        message.setData(bundle);
                        handler.sendMessage(message);

                    }
                    break;

                default:
            }

        }

    }

    public boolean processDownTouch(MotionEvent motionEvent) {
        boolean result = false;

        // Get the game snapshot
        GameSnapshot gameSnapshot = game.gameSnapshot;

        float X = motionEvent.getX();
        float Y = motionEvent.getY();

        if (gameSnapshot != null
                && gameSnapshot.getGameType() == Game.GameType.AGILITY) {

            Game.GameState gameState = gameSnapshot.getGameState();
            switch (gameState) {
                case STOPPED:
                    SurfaceViewTextHandler textHandlerLevelDecrement =
                            mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_decrement));
                    SurfaceViewTextHandler textHandlerLevel =
                            mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level));
                    SurfaceViewTextHandler textHandlerLevelIncrement =
                            mapSurfaceViewTextHandler.get(context.getString(R.string.graphics_textkey_level_increment));
                    RectF decrementLevelRectangle = textHandlerLevelDecrement.getDrawBoundingRectF(0.0f, 0.0f, 2.0f);
                    RectF incrementLevelRectangle = textHandlerLevelIncrement.getDrawBoundingRectF(
                            (float) textHandlerLevelDecrement.getTextWidth()
                            + (float) textHandlerLevel.getTextWidth()
                            , 0.0f
                            , 2.0f);
                    if (decrementLevelRectangle.contains(X, Y) == true) {

                        // Send the message that the decrement level button was pressed
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString(
                                SurfaceViewGame.surfaceViewGame_message_buttonPressed_decrementLevel
                                , SurfaceViewGame.surfaceViewGame_message_buttonPressed_decrementLevel);
                        message.setData(bundle);
                        handler.sendMessage(message);

                        // Set the result to true (meaning a button was pressed)
                        result = true;

                    } else if (incrementLevelRectangle.contains(X, Y) == true) {

                        // Send the message that the increment level button was pressed
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString(
                                SurfaceViewGame.surfaceViewGame_message_buttonPressed_incrementLevel
                                , SurfaceViewGame.surfaceViewGame_message_buttonPressed_incrementLevel);
                        message.setData(bundle);
                        handler.sendMessage(message);

                        // Set the result to true (meaning a button was pressed)
                        result = true;

                    }
                    break;

                default:
            }

        }

        return result;
    }

	public void updateAverageTimeBetweenFrames() {

		// Get the current time
		long currentTimeMillis = SystemClock.elapsedRealtime();

		// Get the time elapsed since the last frame
		long timeElapsedSinceLastFrame = currentTimeMillis - lastFrameTimeMillis;

		// Set the current time as the last frame time
		lastFrameTimeMillis = currentTimeMillis;

		// Check if we have filled up the frame time differences register
		if (currentNumberOfFramePolls >= maximumNumberOfFramesPerSecondPolls - 1) {

			// Shift the values in the register to the left and recalculate the average
			currentAverageTimeBetweenFramesMillis = 0.0;
			for (int i = 0; i < maximumNumberOfFramesPerSecondPolls - 2; i++) {
				timeBetweenFramesRegister[i] = timeBetweenFramesRegister[i + 1];
				currentAverageTimeBetweenFramesMillis += timeBetweenFramesRegister[i];
			}

			// Add the latest time to the register and average
			timeBetweenFramesRegister[maximumNumberOfFramesPerSecondPolls - 1] = timeElapsedSinceLastFrame;
			currentAverageTimeBetweenFramesMillis += timeElapsedSinceLastFrame;
			currentAverageTimeBetweenFramesMillis /= maximumNumberOfFramesPerSecondPolls;

		} else if (currentNumberOfFramePolls >= 0) {

			// Add new time to the register
			timeBetweenFramesRegister[currentNumberOfFramePolls] = timeElapsedSinceLastFrame;
			currentNumberOfFramePolls++;

			// Calculate the current average
			currentAverageTimeBetweenFramesMillis = 0.0;
			for (int i = 0; i < currentNumberOfFramePolls; i++) {
				currentAverageTimeBetweenFramesMillis += timeBetweenFramesRegister[i];
			}
			currentAverageTimeBetweenFramesMillis /= currentNumberOfFramePolls;

		} else {
			currentNumberOfFramePolls = 0;
		}

	}

	private void updateSurfaceViewGameThreadSharedData() {
		game.surfaceViewGameThreadSharedData = new SurfaceViewGameThreadSharedData(
			canvasWidth
			, canvasHeight
			, currentAverageTimeBetweenFramesMillis
		);
	}

}
