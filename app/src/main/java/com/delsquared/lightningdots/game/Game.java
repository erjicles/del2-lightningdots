package com.delsquared.lightningdots.game;

import android.content.Context;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.PositionVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Game {

	private static Game singletonGame = new Game();

	public Object lockGame = new Object();

	public volatile GameSnapshot gameSnapshot = new GameSnapshot();
    public volatile Map<String, UserClick> mapValidUserClick = Collections.synchronizedMap(new HashMap<String, UserClick>());
	public volatile GameThreadSharedData gameThreadSharedData = new GameThreadSharedData();
	public volatile SurfaceViewGameThreadSharedData surfaceViewGameThreadSharedData = new SurfaceViewGameThreadSharedData();
	public volatile boolean canvasSizeChanged = false;

	private GameType gameType;
	private GameState gameState;
	private int gameLevel;
    private long startingTimeMillis;
	private long gameTimeLimitMillis;
    private long endingTimeMillis;

	private GameTimer startingTimer;
	private GameTimer gameTimer;
    private GameTimer endingTimer;

	private ClickTarget clickTarget;

    private ArrayList<Integer> arrayListTargetUserClicks = new ArrayList<Integer>();
    private boolean isLevelComplete;
    private int awardLevel;
    private boolean isNewHighScore;

	private ArrayList<UserClick> listUserClick = new ArrayList<UserClick>();
	private ArrayList<UserClick> listNewUserClick = new ArrayList<UserClick>();
	private ArrayList<UserClick> listValidUserClick = new ArrayList<UserClick>();

	private double canvasWidth = 1.0;
	private double canvasHeight = 1.0;

	public Game() {
		this.gameType = GameType.AGILITY;
		this.gameState = GameState.STOPPED;
		this.gameLevel = 1;
        this.startingTimeMillis = 3000;
		this.gameTimeLimitMillis = 15000;
        this.endingTimeMillis = 2000;

		this.startingTimer = new GameTimer();
		this.gameTimer = new GameTimer();
        this.endingTimer = new GameTimer();

        this.isLevelComplete = false;
        this.awardLevel = 0;
        this.isNewHighScore = false;

		this.clickTarget = new ClickTarget();
	}

	public void resetGame(
        Context context
		, GameType gameType
		, int gameLevel
        , long currentTimeMillis
		, int canvasWidthPixels
		, int canvasHeightPixels) {

		this.gameType = gameType;
		this.gameLevel = gameLevel;

        GameLevelDefinition gameLevelDefinition =
                GameLevelDefinitionHelper.getGameLevelDefinition(
                        context
                        , this.gameType
                        , this.gameLevel);
		this.startingTimeMillis = context.getResources().getInteger(R.integer.game_values_defaultGameStartingTimeMillis);
        this.endingTimeMillis = context.getResources().getInteger(R.integer.game_values_defaultGameEndingTimeMillis);

		this.gameState = GameState.STOPPED;

		// Get the game properties
		this.gameTimeLimitMillis = gameLevelDefinition.gameTimeLimitMillis;

		// Create the game timers
		this.startingTimer = new GameTimer();
		this.gameTimer = new GameTimer();
        this.endingTimer = new GameTimer();

        this.arrayListTargetUserClicks = gameLevelDefinition.arrayListTargetUserClicks;
        this.isLevelComplete = false;
        this.awardLevel = 0;
        this.isNewHighScore = false;

		// Create the click target
		this.clickTarget = new ClickTarget(
                context
                , gameLevelDefinition.clickTargetProfileScript
                , canvasWidthPixels
                , canvasHeightPixels);

		listUserClick.clear();
		listNewUserClick.clear();
		listValidUserClick.clear();
        mapValidUserClick.clear();

		setGameState(GameState.STOPPED, currentTimeMillis);

		updateGameSnapshot();
	}

	public void setGameState(
			GameState newGameState
			, long currentTimeMillis) {

		this.gameState = newGameState;

		switch (newGameState) {

			case STOPPED:
				initializeStartingTimer(currentTimeMillis, startingTimeMillis);
				initializeGameTimer(currentTimeMillis, gameTimeLimitMillis);
                initializeEndingTimer(currentTimeMillis, endingTimeMillis);
				break;

			case READY_TO_START:
				break;

			case STARTING:
				startingTimer.start(currentTimeMillis);
				gameTimer.pause(currentTimeMillis);
                endingTimer.pause(currentTimeMillis);
				break;

			case PAUSED:
				startingTimer.pause(currentTimeMillis);
				gameTimer.pause(currentTimeMillis);
                endingTimer.pause(currentTimeMillis);
				break;

			case RESUMING:
				break;

			case RUNNING:
				startingTimer.reset(currentTimeMillis);
				gameTimer.start(currentTimeMillis);
                endingTimer.reset(currentTimeMillis);
				break;

            case PREPARING_TO_END:
                startingTimer.pause(currentTimeMillis);
                gameTimer.pause(currentTimeMillis);
                endingTimer.reset(currentTimeMillis);
                break;

			case ENDING:
                startingTimer.pause(currentTimeMillis);
                gameTimer.pause(currentTimeMillis);
                endingTimer.start(currentTimeMillis);
				break;

			case ENDED:
				startingTimer.pause(currentTimeMillis);
				gameTimer.pause(currentTimeMillis);
                endingTimer.pause(currentTimeMillis);
				break;

			default:

		}

		updateGameSnapshot();

	}

    public void setGameCompleteState(boolean isLevelComplete, int awardLevel, boolean isNewHighScore) {
        this.isLevelComplete = isLevelComplete;
        this.awardLevel = awardLevel;
        this.isNewHighScore = isNewHighScore;
    }

	public static Game getInstance() { return singletonGame; }
	public GameState getGameState() { return gameState; }
	public GameType getGameType() { return gameType; }
	public int getGameLevel() { return gameLevel; }
    public long getStartingTimeMillis() { return startingTimeMillis; }
	public long getGameTimeLimitMillis() { return gameTimeLimitMillis; }
	public ClickTarget getClickTarget() { return clickTarget; }
	public ArrayList<UserClick> getListUserClick() { return listUserClick; }
	public ArrayList<UserClick> getListValidUserClick() { return listValidUserClick; }
	public ArrayList<UserClick> getListNewUserClick() { return listNewUserClick; }

	public void addUserClick(UserClick userClick) { listUserClick.add(userClick); }
	public void addNewUserClick(UserClick userClick) { listNewUserClick.add(userClick); }
	public void addValidUserClick(UserClick userClick) {
        listValidUserClick.add(userClick);
        String newUserClickIndex = Integer.toString(mapValidUserClick.size() + 1);
        mapValidUserClick.put(newUserClickIndex, userClick);
    }

	public void setCanvasWidthAndHeight(double canvasWidth, double canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		if (clickTarget != null) {
			clickTarget.setCanvasWidthAndHeight(canvasWidth, canvasHeight);
		}
	}

	public void processNewTime(Context context, long currentTimeMillis) {

		// Process the new time for the start timer
		startingTimer.processNewTime(currentTimeMillis);

		// Process the new time
		long timeElapsedSinceLastUpdateMillis = gameTimer.processNewTime(currentTimeMillis);

        // Process the new time for the ending timer
        endingTimer.processNewTime(currentTimeMillis);

		if (gameState == GameState.RUNNING) {

			float radiusPixels = (float) clickTarget.getRadiusPixels();

			// Move the target
			clickTarget.processElapsedTimeMillis(
					context
					, timeElapsedSinceLastUpdateMillis);
					//, radiusPixels / 2
					//, radiusPixels / 2
					//, (double) canvasWidth - (radiusPixels / 2)
					//, (double) canvasHeight - (radiusPixels / 2));

		}

		updateGameSnapshot();

	}

    public void pauseTimers(long currentTimeMillis) {
        startingTimer.pause(currentTimeMillis);
        gameTimer.pause(currentTimeMillis);
        endingTimer.pause(currentTimeMillis);
        updateGameSnapshot();
    }

    public void resumeTimers(long currentTimeMillis) {

        switch (gameState) {

            case STARTING:
                startingTimer.start(currentTimeMillis);
                break;

            case RUNNING:
                gameTimer.start(currentTimeMillis);
                break;

            case ENDING:
                endingTimer.start(currentTimeMillis);
                break;

        }

        updateGameSnapshot();
    }

	private void initializeStartingTimer(long currentTimeMillis, long startingTimeMillis) {
		startingTimer.initialize(currentTimeMillis, startingTimeMillis, GameTimer.GameTimerType.COUNTDOWN);
		updateGameSnapshot();
	}

	private void initializeGameTimer(long currentTimeMillis, long gameTimeLimitMillis) {
		GameTimer.GameTimerType gameTimerTypeToUse = GameTimer.GameTimerType.COUNTDOWN;
		if (gameType == GameType.ENDURANCE) {
			gameTimerTypeToUse = GameTimer.GameTimerType.STOPWATCH;
		}
		gameTimer.initialize(currentTimeMillis, gameTimeLimitMillis, gameTimerTypeToUse);
		updateGameSnapshot();
	}

    private void initializeEndingTimer(long currentTimeMillis, long endingTimeMillis) {
        endingTimer.initialize(currentTimeMillis, endingTimeMillis, GameTimer.GameTimerType.COUNTDOWN);
    }

	public boolean getStartTimerExpired() { return startingTimer.timeLimitExceeded(); }

	public boolean getGameTimeExpired() { return gameTimer.timeLimitExceeded(); }

    public boolean getEndingTimerExpired() { return endingTimer.timeLimitExceeded(); }

	public long getGameEndTimeMillis() { return gameTimer.getEndTimeMillis(); }

	public long getGameTimeElapsedMillis() { return gameTimer.getTimeElapsedMillis(); }

	public int getDisplayStartTimeRemaining() { return startingTimer.getDisplayTimeRemaining(); }

	public int getDisplayGameTimeRemaining() { return gameTimer.getDisplayTimeRemaining(); }

	public int getDisplayGameTimeElapsed() { return gameTimer.getDisplayTimeElapsed(); }

    public ArrayList<Integer> getArrayListTargetUserClicks() { return arrayListTargetUserClicks; }

	public void processUserClick(Context context, float clickX, float clickY) {

		// Initialize the click to being inside the target
		boolean isInsideTarget = true;

		// Check if the game type is agility
		if (gameType == Game.GameType.AGILITY) {

            if (gameSnapshot != null) {

                // Get the current click target snapshot
                ClickTargetSnapshot currentClickTargetSnapshot = gameSnapshot.getCurrentClickTargetSnapshot();

                if (currentClickTargetSnapshot != null) {

                    isInsideTarget = currentClickTargetSnapshot.pointIsInsideTarget(clickX, clickY);

                }

            }

            // Determine if the click is within the target
            //isInsideTarget = clickTarget.pointIsInsideTarget(clickX, clickY);

		}

		// Create the new user click
		UserClick userClick = new UserClick(
				(int) clickX
				, (int) clickY
				, 10
				, "default"
				, isInsideTarget);

		// Add the user click
		addUserClick(userClick);
		addNewUserClick(userClick);
		if (isInsideTarget == true) {
			addValidUserClick(userClick);
		}

		// Update the game snapshot
		updateGameSnapshot();
	}

	public void updateGameSnapshot() {
		this.gameSnapshot = new GameSnapshot(
				this.getGameType()
				, this.getGameLevel()
                , this.getGameState()
				, this.getDisplayStartTimeRemaining()
				, this.getGameEndTimeMillis()
				, this.getDisplayGameTimeRemaining()
                , this.arrayListTargetUserClicks
                , this.isLevelComplete
                , this.awardLevel
                , this.isNewHighScore
				, clickTarget.getClickTargetSnapshot()
		);
	}

	public enum GameType {
		TIME_ATTACK
		, ENDURANCE
		, AGILITY
	}

	public enum GameState {
		STOPPED
		, READY_TO_START
		, STARTING
		, PAUSED
		, RESUMING
		, RUNNING
        , PREPARING_TO_END
		, ENDING
		, ENDED
	}
}
