package com.delsquared.lightningdots.game;

import android.content.Context;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.OrderedObjectCollection;
import com.delsquared.lightningdots.utilities.PositionEvolvingPolygonalObjectContainer;
import com.delsquared.lightningdots.utilities.PositionEvolvingPolygonalObjectContainerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Game {

    // Global objects
	private static Game singletonGame = new Game();
	public Object lockGame = new Object();

	// Objects shared with drawing thread
	public volatile GameSnapshot gameSnapshot = new GameSnapshot();
    public volatile Map<String, UserClick> mapValidUserClick = Collections.synchronizedMap(new HashMap<String, UserClick>());
	public volatile GameThreadSharedData gameThreadSharedData = new GameThreadSharedData();
	public volatile SurfaceViewGameThreadSharedData surfaceViewGameThreadSharedData = new SurfaceViewGameThreadSharedData();
	public volatile boolean canvasSizeChanged = false;

    // Game definition objects
	private GameType gameType;
	private GameState gameState;
	private int gameLevel;
    private GameLevelDefinition gameLevelDefinition;

    // Objects for timing
    private long startingTimeMillis;
	private long endingTimeMillis;
	private GameTimer startingTimer;
	private GameTimer gameTimer;
    private GameTimer endingTimer;

    // Click targets
    private PositionEvolvingPolygonalObjectContainer<ClickTarget> containerClickTargets;

    // User click lists
    private List<UserClick> listUserClick = new ArrayList<UserClick>();
    private List<UserClick> listNewUserClick = new ArrayList<UserClick>();
    private List<UserClick> listValidUserClick = new ArrayList<UserClick>();

    // Objects for the end of the game
    private boolean isLevelComplete;
    private int awardLevel;
    private boolean isNewHighScore;

	//private double canvasWidth = 1.0;
	//private double canvasHeight = 1.0;

	public Game() {
		this.gameType = GameType.AGILITY;
		this.gameState = GameState.STOPPED;
		this.gameLevel = 1;
        this.gameLevelDefinition = new GameLevelDefinition();
        this.startingTimeMillis = 3000;
		this.endingTimeMillis = 2000;

		this.startingTimer = new GameTimer();
		this.gameTimer = new GameTimer();
        this.endingTimer = new GameTimer();

        this.containerClickTargets = new PositionEvolvingPolygonalObjectContainer<>();

        this.isLevelComplete = false;
        this.awardLevel = 0;
        this.isNewHighScore = false;

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

        gameLevelDefinition =
                GameLevelDefinitionHelper.getGameLevelDefinition(
                        context
                        , this.gameType
                        , this.gameLevel);
		this.startingTimeMillis = context.getResources().getInteger(R.integer.game_values_defaultGameStartingTimeMillis);
        this.endingTimeMillis = context.getResources().getInteger(R.integer.game_values_defaultGameEndingTimeMillis);

		this.gameState = GameState.STOPPED;

		// Create the game timers
		this.startingTimer = new GameTimer();
		this.gameTimer = new GameTimer();
        this.endingTimer = new GameTimer();

        //this.arrayListTargetUserClicks = gameLevelDefinition.arrayListTargetUserClicks;
        this.isLevelComplete = false;
        this.awardLevel = 0;
        this.isNewHighScore = false;

		// Create the list of ClickTargets
        List<ClickTarget> listClickTargets = new ArrayList<>();

        // Iterate through the click target definitions
		for (String clickTargetName : gameLevelDefinition.levelDefinitionLadder.listClickTargetDefinitionNames) {

			// Get the click target definition
			ClickTargetDefinition clickTargetDefinition = gameLevelDefinition.levelDefinitionLadder.mapClickTargetDefinitions.get(clickTargetName);

			// Create the new click target
			ClickTarget newClickTarget = new ClickTarget(
					context
					, clickTargetName
					, clickTargetDefinition.clickTargetProfileScript
					, canvasWidthPixels
					, canvasHeightPixels
			);

			// Add the new click target to the click target list
			listClickTargets.add(newClickTarget);

		}

        // Reinitialize the collection of ClickTargets
        containerClickTargets = new PositionEvolvingPolygonalObjectContainer<>(
                new OrderedObjectCollection(listClickTargets)
                , gameLevelDefinition.levelDefinitionLadder.mapTransitionTriggers
                , gameLevelDefinition.levelDefinitionLadder.mapRandomChangeTriggers
                , gameLevelDefinition.levelDefinitionLadder.mapPositionEvolverVariableAttractors
        );

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
				initializeGameTimer(currentTimeMillis, gameLevelDefinition.gameTimeLimitMillis);
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
	public long getGameTimeLimitMillis() { return gameLevelDefinition.gameTimeLimitMillis; }
	public List<UserClick> getListUserClick() { return listUserClick; }
	public List<UserClick> getListValidUserClick() { return listValidUserClick; }
	public List<UserClick> getListNewUserClick() { return listNewUserClick; }

	public void addUserClick(UserClick userClick) { listUserClick.add(userClick); }
	public void addNewUserClick(UserClick userClick) { listNewUserClick.add(userClick); }
	public void addValidUserClick(UserClick userClick) {
        listValidUserClick.add(userClick);
        String newUserClickIndex = Integer.toString(mapValidUserClick.size() + 1);
        mapValidUserClick.put(newUserClickIndex, userClick);
    }

	public void setCanvasWidthAndHeight(double canvasWidth, double canvasHeight) {

        for (ClickTarget currentClickTarget : containerClickTargets.getCollectionObjects()) {

            // Set the click target width and height
            currentClickTarget.setCanvasWidthAndHeight(canvasWidth, canvasHeight);

        }

	}

	public void processNewTime(Context context, long currentTimeMillis) {

		// Process the new time for the start timer
		startingTimer.processNewTime(currentTimeMillis);

		// Process the new time
		long timeElapsedSinceLastUpdateMillis = gameTimer.processNewTime(currentTimeMillis);

        // Convert the time to seconds
        double timeElapsedSinceLastUpdateSeconds = timeElapsedSinceLastUpdateMillis / 1000.0;

        // Process the new time for the ending timer
        endingTimer.processNewTime(currentTimeMillis);

		if (gameState == GameState.RUNNING) {

			// Create the helper
            PositionEvolvingPolygonalObjectContainerHelper<ClickTarget> clickTargetEvolverHelper =
                    new PositionEvolvingPolygonalObjectContainerHelper<>(context);

            // Evolve the time
            clickTargetEvolverHelper.evolveTime(timeElapsedSinceLastUpdateSeconds, containerClickTargets);

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

    public ArrayList<Integer> getArrayListTargetUserClicks() { return gameLevelDefinition.arrayListTargetUserClicks; }

	public void processUserClick(Context context, float clickX, float clickY) {

		// Initialize the click to being inside the target
		boolean isInsideTarget = false;

        // Check if there are no click targets - if none, set it to true
        if (containerClickTargets.getCollectionObjects().size() == 0) {
            isInsideTarget = true;
        }

		// Check if the game type is agility
		if (gameType == Game.GameType.AGILITY) {

            if (gameSnapshot != null) {

                // Get the map of the current click target snapshots
                Map<String, ClickTargetSnapshot> mapClickTargetSnapshots = gameSnapshot.getMapClickTargetSnapshots();

                // Iterate through the snapshots
                Iterator clickTargetSnapshotIterator = mapClickTargetSnapshots.entrySet().iterator();
                while (clickTargetSnapshotIterator.hasNext()) {

                    // Get the current click target snapshot
                    Map.Entry<String, ClickTargetSnapshot> currentClickTargetSnapshotPair = (Map.Entry) clickTargetSnapshotIterator.next();
                    ClickTargetSnapshot currentClickTargetSnapshot = currentClickTargetSnapshotPair.getValue();

                    // Check if the click target snapshot is visible and clickable
                    if (currentClickTargetSnapshot.getIsClickable()
                            && currentClickTargetSnapshot.getVisibility() == ClickTarget.VISIBILITY.VISIBLE) {

                        // Check if the click is inside the target
                        isInsideTarget = currentClickTargetSnapshot.pointIsInsideTarget(clickX, clickY);

                    }

                    // Check if we have a positive result
                    if (isInsideTarget) {

                        // Break out of the while loop
                        break;

                    }

                }

            }

		}

        // The game type is not ladder
        else {

            // Set the flag to true
            isInsideTarget = true;

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

        // Create the list of click target snapshots
        Map<String, ClickTargetSnapshot> mapClickTargetSnapshots = new HashMap<>();

        // Loop through the click targets
        for (ClickTarget currentClickTarget : containerClickTargets.getCollectionObjects()) {

            // Add the click target snapshot to the snap shot list
            mapClickTargetSnapshots.put(
                    currentClickTarget.getName()
                    , currentClickTarget.getClickTargetSnapshot());

        }

		this.gameSnapshot = new GameSnapshot(
				this.getGameType()
				, this.getGameLevel()
                , this.getGameState()
				, this.getDisplayStartTimeRemaining()
				, this.getGameEndTimeMillis()
				, this.getDisplayGameTimeRemaining()
                , this.gameLevelDefinition.arrayListTargetUserClicks
                , this.isLevelComplete
                , this.awardLevel
                , this.isNewHighScore
				, mapClickTargetSnapshots
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
