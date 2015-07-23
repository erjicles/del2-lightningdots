package com.delsquared.lightningdots.game;

import android.content.Context;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionVector;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

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
    private Map<String, ClickTarget> mapClickTargets;

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

        this.mapClickTargets = new HashMap<>();

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

		// Reinitialize the click target map
        this.mapClickTargets = new HashMap<>();

        // Iterate through the click target definitions
        Iterator clickTargetDefinitionIterator = gameLevelDefinition.levelDefinitionLadder.mapClickTargetDefinitions.entrySet().iterator();
        while (clickTargetDefinitionIterator.hasNext()) {

            // Get the current click target definition and name
            Map.Entry<String, ClickTargetDefinition> currentClickTargetDefinitionPair = (Map.Entry)clickTargetDefinitionIterator.next();
            String clickTargetName = currentClickTargetDefinitionPair.getKey();
            ClickTargetDefinition clickTargetDefinition = currentClickTargetDefinitionPair.getValue();

            // Create the new click target
            ClickTarget newClickTarget = new ClickTarget(
                    context
                    , clickTargetName
                    , clickTargetDefinition.clickTargetProfileScript
                    , canvasWidthPixels
                    , canvasHeightPixels
            );

            // Add the new click target to the click target map
            this.mapClickTargets.put(clickTargetName, newClickTarget);
        }

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
	public Map<String, ClickTarget> getMapClickTargets() { return mapClickTargets; }
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
		//this.canvasWidth = canvasWidth;
		//this.canvasHeight = canvasHeight;

        // Set the width and height for the click targets
        Iterator clickTargetIterator = mapClickTargets.entrySet().iterator();
        while (clickTargetIterator.hasNext()) {

            // Get the current click target
            Map.Entry<String, ClickTarget> currentClickTargetPair = (Map.Entry) clickTargetIterator.next();
            ClickTarget currentClickTarget = currentClickTargetPair.getValue();

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

            // -------------------- BEGIN Check for RandomChangeEvents and TransitionEvents -------------------- //

			// Initialize the list of unprocessed RandomChangeEvents
            List<ClickTarget.RandomChangeEvent> listRandomChangeEventsUnprocessed = new ArrayList<>();

            // Initialize the list of unprocessed TransitionEvents
            List<ClickTarget.ClickTargetProfileTransitionEvent> listTransitionEventsUnprocessed = new ArrayList<>();

            // Initialize the click target iterator
            Iterator clickTargetIterator = mapClickTargets.entrySet().iterator();

            // Check the click targets for random changes
            while (clickTargetIterator.hasNext()) {

                // Get the current click target
                Map.Entry<String, ClickTarget> currentClickTargetPair = (Map.Entry) clickTargetIterator.next();
                ClickTarget currentClickTarget = currentClickTargetPair.getValue();

                // Check the current click target for random changes
                ArrayList<ClickTarget.RandomChangeEvent> currentClickTargetArrayListRandomChangeEvents =
                        currentClickTarget.checkRandomChanges(timeElapsedSinceLastUpdateSeconds);

                // Add the list of random change events to the list
                listRandomChangeEventsUnprocessed.addAll(currentClickTargetArrayListRandomChangeEvents);

                // Check the current click target for a profile transition
                if (currentClickTarget.checkProfileTransition(timeElapsedSinceLastUpdateSeconds)) {

                    // Create the transition event
                    ClickTarget.ClickTargetProfileTransitionEvent transitionEvent =
                            new ClickTarget.ClickTargetProfileTransitionEvent(
                                    currentClickTarget.getName()
                                    , currentClickTarget.getCurrentClickTargetProfileName()
                            );

                    // Add the transition event to the list
                    listTransitionEventsUnprocessed.add(transitionEvent);

                }

            }
            // -------------------- END Check for RandomChangeEvents and TransitionEvents -------------------- //

            // Initialize the list of SyncVariableTriggers already processed
            // TransitionTriggers and RandomChangeTriggers contribute to this
            List<LevelDefinitionLadder.SyncVariableTrigger> listGeneratedSyncVariableTriggers = new ArrayList<>();

            // -------------------- BEGIN Create the map of generated RandomChangeEvents -------------------- //

            // Initialize the map of generated random changes
            Map<String, Map<String, Map<String, Boolean>>> mapGeneratedRandomChanges = new HashMap<>();

            // Initialize the map of RandomChangeTriggers already processed
            Map<NTuple, Boolean> mapRandomChangeEventsProcessed = new HashMap<>();

            // Process while there are still unprocessed RandomChangeEvents
            while (listRandomChangeEventsUnprocessed.size() > 0) {

                // Get the first RandomChangeEvent in the list
                ClickTarget.RandomChangeEvent firstRandomChangeEvent = listRandomChangeEventsUnprocessed.get(0);

                // Get the RandomChangeTrigger key
                NTuple randomChangeTriggerKey = firstRandomChangeEvent.toRandomChangeTriggerKey();

                // Remove this RandomChangeEvent from the unprocessed list
                listRandomChangeEventsUnprocessed.remove(0);

                // Check if we have not yet processed this RandomChangeEvent
                if (!mapRandomChangeEventsProcessed.containsKey(randomChangeTriggerKey)) {

                    // Add this random change event to the map of processed random change events
                    mapRandomChangeEventsProcessed.put(randomChangeTriggerKey, true);

                    // -------------------- BEGIN Add this random change to the map of generated random changes -------------------- //

                    // Check if this click target has not been added to the map of generated random changes
                    if (!mapGeneratedRandomChanges.containsKey(firstRandomChangeEvent.clickTargetName)) {

                        // Add this click target to the map of generated random changes
                        mapGeneratedRandomChanges.put(
                                firstRandomChangeEvent.clickTargetName
                                , new HashMap<String, Map<String, Boolean>>()
                        );

                    }

                    // Check if this click target profile has not been added
                    if (!mapGeneratedRandomChanges.get(
                            firstRandomChangeEvent.clickTargetName)
                            .containsKey(firstRandomChangeEvent.clickTargetProfileName)) {

                        // Add this click target profile to the map
                        mapGeneratedRandomChanges.get(firstRandomChangeEvent.clickTargetName)
                                .put(firstRandomChangeEvent.clickTargetProfileName, new HashMap<String, Boolean>());

                    }

                    // Check if this variable has not been added
                    if (!mapGeneratedRandomChanges.get(
                            firstRandomChangeEvent.clickTargetName)
                            .get(firstRandomChangeEvent.clickTargetProfileName)
                            .containsKey(firstRandomChangeEvent.variable)) {

                        // Add this variable to the map
                        mapGeneratedRandomChanges.get(firstRandomChangeEvent.clickTargetName)
                                .get(firstRandomChangeEvent.clickTargetProfileName)
                                .put(firstRandomChangeEvent.variable, true);

                    }

                    // -------------------- END Add this random change to the map of generated random changes -------------------- //

                    // Check if there are any triggers associated with this random change event
                    if (gameLevelDefinition.levelDefinitionLadder != null
                            && gameLevelDefinition.levelDefinitionLadder.mapRandomChangeTriggers.containsKey(randomChangeTriggerKey)) {

                        // Get the list of associated triggers
                        List<LevelDefinitionLadder.RandomChangeTrigger> listRandomChangeTriggers =
                                gameLevelDefinition.levelDefinitionLadder.mapRandomChangeTriggers.get(randomChangeTriggerKey);

                        // Loop through the associated triggers
                        for (LevelDefinitionLadder.RandomChangeTrigger randomChangeTrigger : listRandomChangeTriggers) {

                            // Convert the trigger to a RandomChangeEvent
                            ClickTarget.RandomChangeEvent randomChangeEvent =
                                    randomChangeTrigger.toRandomChangeEvent();

                            // Add the new RandomChangeEvent to the list of RandomChangeEvents
                            listRandomChangeEventsUnprocessed.add(randomChangeEvent);

                            // Loop through the SyncVariableTriggers associated with this RandomChangeTrigger
                            for (LevelDefinitionLadder.SyncVariableTrigger syncVariableTrigger : randomChangeTrigger.listSyncVariableTriggers) {

                                // Add the sync variable trigger to the list
                                listGeneratedSyncVariableTriggers.add(syncVariableTrigger);

                            }

                        }

                    }

                }

            }

            // -------------------- END Create the map of generated RandomChangeEvents -------------------- //


            // -------------------- BEGIN Create the map of generated ClickTargetProfileTransitionEvents -------------------- //

            // Initialize the map of generated transitions
            Map<String, String> mapGeneratedTransitions = new HashMap<>();

            // Initialize the map of ClickTargetProfileTransitionEvents already processed
            Map<NTuple, Boolean> mapTransitionEventsProcessed = new HashMap<>();

            // Process while there are still unprocessed ClickTargetProfileTransitionEvents
            while (listTransitionEventsUnprocessed.size() > 0) {

                // Get the first ClickTargetProfileTransitionEvent in the list
                ClickTarget.ClickTargetProfileTransitionEvent firstTransitionEvent = listTransitionEventsUnprocessed.get(0);

                // Get the TransitionTrigger key
                NTuple transitionTriggerKey = firstTransitionEvent.toTransitionTriggerKey();

                // Remove this TransitionEvent from the unprocessed list
                listTransitionEventsUnprocessed.remove(0);

                // Check if we have not yet processed this transition event
                if (!mapTransitionEventsProcessed.containsKey(transitionTriggerKey)) {

                    // Add this transition event to the map of processed transition events
                    mapTransitionEventsProcessed.put(transitionTriggerKey, true);

                    // Add this transition event to the map of generated transition events
                    mapGeneratedTransitions.put(firstTransitionEvent.clickTargetName, firstTransitionEvent.clickTargetProfileName);

                    // Check if there are any triggers associated with this transition event
                    if (gameLevelDefinition.levelDefinitionLadder != null
                            && gameLevelDefinition.levelDefinitionLadder.mapTransitionTriggers.containsKey(transitionTriggerKey)) {

                        // Get the list of associated triggers
                        List<LevelDefinitionLadder.TransitionTrigger> listTransitionTriggers =
                                gameLevelDefinition.levelDefinitionLadder.mapTransitionTriggers.get(transitionTriggerKey);

                        // Loop through the associated triggers
                        for (LevelDefinitionLadder.TransitionTrigger transitionTrigger : listTransitionTriggers) {

                            // Initialize the target click target name and profile
                            String targetClickTargetName = transitionTrigger.targetClickTargetName;
                            String targetClickTargetProfileName = transitionTrigger.targetClickTargetProfileName;

                            // Check if the transition trigger should randomize the target click target
                            if (transitionTrigger.randomTargetClickTarget
                                    && gameLevelDefinition.levelDefinitionLadder.listClickTargetDefinitionNames.size() > 1) {

                                // Get the index of the source click target for the transition trigger
                                int sourceClickTargetIndex =
                                        gameLevelDefinition
                                                .levelDefinitionLadder
                                                .listClickTargetDefinitionNames.indexOf(transitionTrigger.sourceClickTargetName);

                                // Generate a random index
                                int randomIndex =
                                        UtilityFunctions.generateRandomIndex(
                                                0
                                                , gameLevelDefinition.levelDefinitionLadder.listClickTargetDefinitionNames.size() - 2);

                                // Shift the random index up to ensure that we never select the source
                                if (randomIndex >= sourceClickTargetIndex) {
                                    randomIndex++;
                                }

                                // Set the name of the target
                                targetClickTargetName = gameLevelDefinition.levelDefinitionLadder.listClickTargetDefinitionNames.get(randomIndex);

                            }

                            // Check if the transition trigger should randomize the target click target profile
                            if (transitionTrigger.randomTargetClickTargetProfile) {

                                // Get the click target definition
                                ClickTargetDefinition clickTargetDefinition =
                                        gameLevelDefinition.levelDefinitionLadder.mapClickTargetDefinitions.get(targetClickTargetName);

                                // Get the list of profile names
                                List<String> listClickTargetProfileNames =
                                        clickTargetDefinition.clickTargetProfileScript.getListClickTargetProfileNames();

                                // Get a random index
                                int randomIndex =
                                        UtilityFunctions.generateRandomIndex(
                                                0
                                                , listClickTargetProfileNames.size() - 1
                                        );

                                // Set the name of the target
                                targetClickTargetProfileName = listClickTargetProfileNames.get(randomIndex);

                            }

                            // Convert the trigger to a ClickTargetProfileTransitionEvent
                            ClickTarget.ClickTargetProfileTransitionEvent transitionEvent = new ClickTarget.ClickTargetProfileTransitionEvent(
                                    targetClickTargetName
                                    , targetClickTargetProfileName
                            );

                            // Add the new TransitionEvent to the list of TransitionEvents
                            listTransitionEventsUnprocessed.add(transitionEvent);

                            // Loop through the SyncVariableTriggers associated with this RandomChangeTrigger
                            for (LevelDefinitionLadder.SyncVariableTrigger syncVariableTrigger : transitionTrigger.listSyncVariableTriggers) {

                                // Add the sync variable trigger to the list
                                listGeneratedSyncVariableTriggers.add(syncVariableTrigger);

                            }

                        }

                    }

                }

            }

            // -------------------- END Create the map of generated ClickTargetProfileTransitionEvents -------------------- //


            // -------------------- BEGIN Evolve the click targets -------------------- //

            // Initialize the click target iterator
            clickTargetIterator = mapClickTargets.entrySet().iterator();

            // Check the click targets for random changes
            while (clickTargetIterator.hasNext()) {

                // Get the current click target
                Map.Entry<String, ClickTarget> currentClickTargetPair = (Map.Entry) clickTargetIterator.next();
                ClickTarget currentClickTarget = currentClickTargetPair.getValue();

                // Get the click target name
                String currentClickTargetName = currentClickTarget.getName();

                // Get the click target profile name
                String currentClickTargetProfileName = currentClickTarget.getCurrentClickTargetProfileName();

                // Get the random changes for this click target and profile
                Map<String, Boolean> mapRandomChanges = new HashMap<>();
                if (mapGeneratedRandomChanges.containsKey(currentClickTargetName)
                        && mapGeneratedRandomChanges.get(currentClickTargetName).containsKey(currentClickTargetProfileName)) {
                    mapRandomChanges = mapGeneratedRandomChanges.get(currentClickTargetName).get(currentClickTargetProfileName);
                }

                // Get the transition target profile name
                String targetClickTargetProfileName = null;
                if (mapGeneratedTransitions.containsKey(currentClickTargetName)) {
                    targetClickTargetProfileName = mapGeneratedTransitions.get(currentClickTargetName);
                }

                // Evolve the click target
                currentClickTarget.processElapsedTimeMillis(
                        context
                        , timeElapsedSinceLastUpdateSeconds
                        , new HashMap<String, Boolean>()
                        , mapRandomChanges
                        , targetClickTargetProfileName);

            }

            // -------------------- END Evolve the click targets -------------------- //


            // -------------------- BEGIN Process sync variable triggers -------------------- //

            // Loop through the list of generated sync variable triggers
            for (LevelDefinitionLadder.SyncVariableTrigger syncVariableTrigger : listGeneratedSyncVariableTriggers) {

                // Check the mode
                if (syncVariableTrigger.mode == LevelDefinitionLadder.SyncVariableTrigger.MODE.SNAP_TO_TARGET) {

                    // Check if the source and target ClickTargets exist
                    if (mapClickTargets.containsKey(syncVariableTrigger.sourceClickTargetName)
                            && mapClickTargets.containsKey((syncVariableTrigger.targetClickTargetName))) {

                        // Get the source and target ClickTargets
                        ClickTarget sourceClickTarget = mapClickTargets.get(syncVariableTrigger.sourceClickTargetName);
                        ClickTarget targetClickTarget = mapClickTargets.get(syncVariableTrigger.targetClickTargetName);

                        // Make sure the source and target ClickTargets are using the required ClickTargetProfiles
                        if (sourceClickTarget.getCurrentClickTargetProfileName().equals(syncVariableTrigger.sourceClickTargetProfileName)
                                && targetClickTarget.getCurrentClickTargetProfileName().equals(syncVariableTrigger.targetClickTargetProfileName)) {

                            // Get the source target value for the variable
                            double sourceVariableValue = sourceClickTarget.getVariableValue(syncVariableTrigger.variableName);

                            // Set the target click target value
                            targetClickTarget.setVariableValue(syncVariableTrigger.variableName, sourceVariableValue);

                        }

                    }

                }

                // The mode is RANDOMIZE or LITERAL_VALUE
                else {

                    // Get the target click target
                    if (mapClickTargets.containsKey(syncVariableTrigger.targetClickTargetName)) {

                        // Get the target ClickTarget
                        ClickTarget targetClickTarget = mapClickTargets.get(syncVariableTrigger.targetClickTargetName);

                        // Check the mode
                        if (syncVariableTrigger.mode == LevelDefinitionLadder.SyncVariableTrigger.MODE.LITERAL_VALUE) {

                            // Set the value
                            targetClickTarget.setVariableValue(syncVariableTrigger.variableName, syncVariableTrigger.value, true);

                        } else if (syncVariableTrigger.mode == LevelDefinitionLadder.SyncVariableTrigger.MODE.RANDOMIZE) {

                            // Randomize the value
                            targetClickTarget.randomizeVariableValue(syncVariableTrigger.variableName);

                        }

                    }

                }

            }

            // -------------------- END Process sync variable triggers -------------------- //

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
        if (mapClickTargets.size() == 0) {
            isInsideTarget = true;
        }

		// Check if the game type is agility
		if (gameType == Game.GameType.AGILITY) {

            if (gameSnapshot != null) {

                // Get the map of the current click target snapshots
                HashMap<String, ClickTargetSnapshot> mapClickTargetSnapshots = gameSnapshot.getMapClickTargetSnapshots();

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
        HashMap<String, ClickTargetSnapshot> mapClickTargetSnapshots = new HashMap<>();

        // Iterate through the click target map
        Iterator clickTargetIterator = mapClickTargets.entrySet().iterator();
        while (clickTargetIterator.hasNext()) {

            // Get the current click target
            Map.Entry<String, ClickTarget> currentClickTargetPair = (Map.Entry) clickTargetIterator.next();
            String currentClickTargetName = currentClickTargetPair.getKey();
            ClickTarget currentClickTarget = currentClickTargetPair.getValue();

            // Add the click target snapshot to the snap shot list
            mapClickTargetSnapshots.put(currentClickTargetName, currentClickTarget.getClickTargetSnapshot());

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
