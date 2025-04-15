package com.bgrant.wwe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bgrant.wwe.SFS;
import com.bgrant.wwe.objects.BloodPool;
import com.bgrant.wwe.objects.BloodSplatter;
import com.bgrant.wwe.objects.Fighter;
import com.bgrant.wwe.resources.Assets;
import com.bgrant.wwe.resources.GlobalVariables;

import java.util.Locale;

public class GameScreen implements Screen , InputProcessor {
    private final SFS game;
    private final ExtendViewport viewport;

    //game
    private enum GameState {
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    private GameState gameState;

    private GlobalVariables.Difficulty difficulty = GlobalVariables.Difficulty.EASY;

    //rounds
    private enum RoundState {
        STARTING,
        IN_PROGRESS,
        ENDING
    }

    private RoundState roundState;
    private float roundStateTime;
    private static final float START_ROUND_DELAY = 2f;
    private static final float END_ROUND_DELAY = 2f;
    private int currentRound;
    private static final int MAX_ROUNDS = 3;

    private int roundsWon = 0 , roundsLost = 0;
    private static final float MAX_ROUND_TIME = 60.99f;
    private float roundTimer = MAX_ROUND_TIME;

    private static final float CRITICAL_ROUND_TIME = 10f;
    private static final Color CRITICAL_ROUND_TIME_COLOR = Color.RED;


    //fonts
    private BitmapFont smallFont , mediumFont , largeFont;
    private static final Color DEFAULT_FONT_COLOR = Color.WHITE;

    //HUD (Heads Up Display)
    private static final Color HEALTH_BAR_COLOR = Color.RED;
    private static final Color HEALTH_BAR_BACKGROUND_COLOR = GlobalVariables.GOLD;

    //background/ring
    private Texture backgroundTexture;
    private Texture frontRopesTexture;

    private static final float RING_MIN_X = 7f;
    private static final float RING_MAX_X = 60f;
    private static final float RING_MIN_Y = 4f;
    private static final float RING_MAX_Y = 22f;

    private static final float RING_SLOPE = 3.16f;

    //fighters
    private static final float PLAYER_START_POSITION_X = 16f;
    private static final float OPPONENT_START_POSITION_X = 51f;
    private static final float FIGHTER_START_POSITION_Y = 15f;

    private static final float FIGHTER_CONTACT_DISTANCE_X = 7.5f;
    private static final float FIGHTER_CONTACT_DISTANCE_Y = 1.5f;

    //buttons
    private Sprite playAgainButtonSprite;
    private Sprite mainMenuButtonSprite;

    private Sprite continueButtonSprite;
    private Sprite pauseButtonSprite;
    private static final float PAUSE_BUTTON_MARGIN = 1.5f;

    //opponent AI
    private float opponentAiTimer;
    private boolean opponentAiMakingContactDecision;

    private static final float OPPONENT_AI_CONTACT_DECISION_DELAY_EASY = 0.1f;    //original - 0.1f   vs extreme - 0.01f
    private static final float OPPONENT_AI_CONTACT_DECISION_DELAY_MEDIUM = 0.07f; //original - 0.07f  vs extreme - 0.001f
    private static final float OPPONENT_AI_CONTACT_DECISION_DELAY_HARD = 0.01f;  //original - 0.01f  vs extreme - 0.0001f

    private static final float OPPONENT_AI_BLOCK_CHANCE = 0.4f;  //original - 0.4f   vs extreme - 0.9f
    private static final float OPPONENT_AI_ATTACK_CHANCE = 0.7f; //original - 0.7f   vs extreme - 1f
    private static final float OPPONENT_AI_NON_CONTACT_DECISION_DELAY = 0.5f; //original - 0.5f   vs extreme - 0f

    private boolean opponentAiPursuingPlayer;

    private static final float OPPONENT_AI_PURSUE_PLAYER_CHANCE_EASY = 0.5f; //original - 0.2f  vs extreme - 10f
    private static final float OPPONENT_AI_PURSUE_PLAYER_CHANCE_MEDIUM = 1f; //original - 0.5f  vs extreme - 50f
    private static final float OPPONENT_AI_PURSUE_PLAYER_CHANCE_HARD = 10f;  //original - 0.1f  vs extreme - 100f

    //blood
    private boolean showingBlood = true;
    private BloodSplatter[] playerBloodSplatters;
    private BloodSplatter[] opponentBloodSplatters;
    private int currentPlayerBloodSplatterIndex;
    private int currentOpponentBloodSplatterIndex;
    private static final int BLOOD_SPLATTER_AMOUNT = 5;
    private static final float BLOOD_SPLATTER_OFFSET_X = 2.8f;
    private static final float BLOOD_SPLATTER_OFFSET_Y = 11f;

    private BloodPool[] bloodPools;
    private int currentBloodPoolIndex;
    private static final int BLOOD_POOL_AMOUNT = 100;


    // CREATING ALL STUFF ON GAME SCREEN
    public GameScreen(SFS game) {
        this.game = game;

        //set up the viewport
        viewport = new ExtendViewport(GlobalVariables.WORLD_WIDTH, GlobalVariables.MIN_WORLD_HEIGHT,
            GlobalVariables.WORLD_WIDTH, 0);

        //create the game area
        createGameArea();

        //set up the fonts
        setUpFonts();

        //create the buttons
        createButtons();

        // create the blood splatters and pools
        createBlood();
    }

    //CREATING GAME AREA
    private void createGameArea() {
        //get the ring textures from the asset manager
        backgroundTexture = game.assets.manager.get(Assets.BACKGROUND_TEXTURE);
        frontRopesTexture = game.assets.manager.get(Assets.FRONT_ROPES_TEXTURE);

    }

    // FONTS & BUTTONS & BLOOD
    private void setUpFonts() {
        smallFont = game.assets.manager.get(Assets.SMALL_FONT);
        smallFont.getData().setScale(GlobalVariables.WORLD_SCALE);
        smallFont.setColor(DEFAULT_FONT_COLOR);
        smallFont.setUseIntegerPositions(false);

        mediumFont = game.assets.manager.get(Assets.MEDIUM_FONT);
        mediumFont.getData().setScale(GlobalVariables.WORLD_SCALE);
        mediumFont.setColor(DEFAULT_FONT_COLOR);
        mediumFont.setUseIntegerPositions(false);

        largeFont = game.assets.manager.get(Assets.LARGE_FONT);
        largeFont.getData().setScale(GlobalVariables.WORLD_SCALE);
        largeFont.setColor(DEFAULT_FONT_COLOR);
        largeFont.setUseIntegerPositions(false);
    }

    //creating buttons
    private void createButtons() {
        //get the gameplay button texture atlas from the asset manager
        TextureAtlas buttonTextureAtlas = game.assets.manager.get(Assets.GAMEPLAY_BUTTONS_ATLAS);

        //create the play again button
        playAgainButtonSprite = new Sprite(buttonTextureAtlas.findRegion("PlayAgainButton"));

        playAgainButtonSprite.setSize(playAgainButtonSprite.getWidth() * GlobalVariables.WORLD_SCALE ,
            playAgainButtonSprite.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the main menu button
        mainMenuButtonSprite = new Sprite(buttonTextureAtlas.findRegion("MainMenuButton"));
        mainMenuButtonSprite.setSize(mainMenuButtonSprite.getWidth() * GlobalVariables.WORLD_SCALE ,
            mainMenuButtonSprite.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the continue button
        continueButtonSprite = new Sprite(buttonTextureAtlas.findRegion("ContinueButton"));
        continueButtonSprite.setSize(continueButtonSprite.getWidth() * GlobalVariables.WORLD_SCALE ,
            continueButtonSprite.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the pause button
        pauseButtonSprite = new Sprite(buttonTextureAtlas.findRegion("PauseButton"));
        pauseButtonSprite.setSize(pauseButtonSprite.getWidth() * GlobalVariables.WORLD_SCALE ,
            pauseButtonSprite.getHeight() * GlobalVariables.WORLD_SCALE);
    }

    //creating blood pools and blood splatters
    private void createBlood() {
        //initialize the blood splatter
        playerBloodSplatters = new BloodSplatter[BLOOD_SPLATTER_AMOUNT];
        opponentBloodSplatters = new BloodSplatter[BLOOD_SPLATTER_AMOUNT];

        for (int i = 0; i < BLOOD_SPLATTER_AMOUNT; i++) {
            playerBloodSplatters[i] = new BloodSplatter(game);
            opponentBloodSplatters[i] = new BloodSplatter(game);
        }

        //set the current blood splatter indexes to the start of the arrays
        currentPlayerBloodSplatterIndex = 0;
        currentOpponentBloodSplatterIndex = 0;

        //initialize the blood pools
        bloodPools = new BloodPool[BLOOD_POOL_AMOUNT];
        for (int i = 0; i< BLOOD_POOL_AMOUNT; i++) {
            bloodPools[i] = new BloodPool(game);
        }

        //set the current blood pool index to the start of the array
        currentBloodPoolIndex = 0;
    }

    @Override
    public void show() {
        //process user input
        Gdx.input.setInputProcessor(this);

        //get the difficulty setting from the setting manager
        difficulty = game.settingsManager.getDifficultySetting();

        //get the blood settings from the setting manager
        showingBlood = game.settingsManager.isBloodSettingOn();

        //start the game
        startGame();
    }

    // GAME (START & PAUSE & RESUME)
    private void startGame() {
        gameState = GameState.RUNNING;
        roundsWon = roundsLost = 0;

        //start round 1
        currentRound = 1;
        startRound();
    }
    private void pauseGame() {
        gameState = GameState.PAUSED;

        //pause game sound and music
        game.audioManager.pauseGameSound();
        game.audioManager.pauseMusic();
    }
    private void resumeGame() {
        gameState = GameState.RUNNING;

        //resume game sound and music (if it's enabled)
        game.audioManager.resumeGameSounds();
        game.audioManager.playMusic();
    }

    // ROUND (START & END & WIN & LOSE )
    private void startRound () {
        //get the fighters ready
        game.player.getReady(PLAYER_START_POSITION_X, FIGHTER_START_POSITION_Y);
        game.opponent.getReady(OPPONENT_START_POSITION_X, FIGHTER_START_POSITION_Y);
//        game.opponent.block(); if we want the opponent to start with block posture

        //start the round
        roundState = RoundState.STARTING;
        roundStateTime = 0f;
        roundTimer = MAX_ROUND_TIME;
    }
    private void endRound() {
        //end the round
        roundState = RoundState.ENDING;
        roundStateTime = 0f;
    }
    private void winRound() {
        //player wins the round and opponent looses
        game.player.win();
        game.opponent.lose();
        roundsWon++;

        //play cheer sound
        game.audioManager.playSound(Assets.CHEER_SOUND);

        //end the round
        endRound();
    }
    private void loseRound() {
        //player loses the round and opponent wins
        game.player.lose();
        game.opponent.win();
        roundsLost++;

        //play boo sound
        game.audioManager.playSound(Assets.BOO_SOUND);

        //end the round
        endRound();
    }

    // MAIN RENDER GAME
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        //update the game -- delta time should be 0 if the game isn't running , to freeze the game
        update(gameState == GameState.RUNNING ? delta : 0f);

        //set the sprite batch and the shape renderer to use the viewports camera
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        //begin drawing
        game.batch.begin();

        //draw the background
        game.batch.draw(backgroundTexture, 0, 0, backgroundTexture.getWidth() * GlobalVariables.WORLD_SCALE,
            backgroundTexture.getHeight() * GlobalVariables.WORLD_SCALE);

        //draw  the blood pools
        renderBloodPools();

        //draw the fighters
        renderFighters();

        //draw the front ropes
        game.batch.draw(frontRopesTexture,0,0,
            frontRopesTexture.getWidth() * GlobalVariables.WORLD_SCALE ,
            frontRopesTexture.getHeight() * GlobalVariables.WORLD_SCALE);

        //draw the HUD
        renderHUD();

        //draw the pause button
        renderPauseButton();

        //if the game is over draw the game over overlay
        if (gameState == GameState.GAME_OVER) {
            renderGameOverOverlay();
        } else {
            //if the round is starting , draw the start round text
            if (roundState == RoundState.STARTING) {
                renderStartRoundText();
            }
            //if the game is paused , draw the pause overlay
            if (gameState == GameState.PAUSED) {
                renderPauseOverlay();
            }
        }

        //end drawing
        game.batch.end();
    }

    //RENDER FIGHTERS
    private void renderFighters() {
        //use the y coordinates of the fighter position  to determine which fighter to draw first
        if (game.player.getPosition().y > game.opponent.getPosition().y) {
            //draw player
            game.player.render(game.batch);

            //draw player's blood splatters (if enabled)
            renderBloodSplatters(playerBloodSplatters);

            //draw opponent
            game.opponent.render(game.batch);

            //draw opponent's blood splatters (if enabled)
            renderBloodSplatters(opponentBloodSplatters);
        } else {
            //draw opponent
            game.opponent.render(game.batch);

            //draw opponent's blood splatters (if enabled)
            renderBloodSplatters(opponentBloodSplatters);

            //draw player
            game.player.render(game.batch);

            //draw player's blood splatters (if enabled)
            renderBloodSplatters(playerBloodSplatters);
        }
    }

    //RENDER BLOOD SPLATTERS
    private void renderBloodSplatters(BloodSplatter[] bloodSplatters) {
        //if the showing blood, draw all (active) blood splatters in the given array
        if (showingBlood) {
            for (BloodSplatter bloodSplatter : bloodSplatters) {
                bloodSplatter.render(game.batch);
            }
        }
    }

    //RENDER BLOOD POOLS
    private void renderBloodPools() {
        //if showing blood , draw all (active) blood pools
        if (showingBlood) {
            for (BloodPool bloodPool : bloodPools) {
                bloodPool.render(game.batch);
            }
        }
    }

    //RENDER HEADS UP DISPLAY
    private void renderHUD() {
        float HUDMargin = 1f;

        //draw the rounds won to lost ratio
        smallFont.draw(game.batch, "WINS: " + roundsWon + " - " + roundsLost ,
            HUDMargin , viewport.getWorldHeight() - HUDMargin);

        //draw the difficulty setting
        String text = "DIFFICULTY: " ;
        switch (difficulty) {
            case EASY:
                text += "EASY";
                break;
            case MEDIUM:
                text += "MEDIUM";
                break;
            default:
                text += "HARD";
        }
        smallFont.draw(game.batch , text ,
            viewport.getWorldWidth() - HUDMargin , viewport.getWorldHeight() - HUDMargin,
            0, Align.right , false);

        //set up the layout sizes and positioning
        float healthBarPadding = 0.5f;
        float healthBarHeight = smallFont.getCapHeight() + healthBarPadding * 2f;
        float healthBarMaxWidth = 32f;
        float healthBarBackgroundPadding = 0.2f;
        float healthBarBackgroundHeight = healthBarHeight + healthBarBackgroundPadding * 2f;
        float healthBarBackgroundWidth = healthBarMaxWidth + healthBarBackgroundPadding * 2f;
        float healthBarBackgroundMarginTop = 0.8f;
        float healthBarBackgroundPositionY = viewport.getWorldHeight() - HUDMargin -
            smallFont.getCapHeight() - healthBarBackgroundMarginTop - healthBarBackgroundHeight;
        float healthBarPositionY = healthBarBackgroundPositionY + healthBarBackgroundPadding;
        float fighterNamePositionY = healthBarPositionY + healthBarHeight - healthBarPadding;

        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //draw the fighter health bar background rectangles
        game.shapeRenderer.setColor(HEALTH_BAR_BACKGROUND_COLOR);
        game.shapeRenderer.rect(HUDMargin , healthBarBackgroundPositionY , healthBarBackgroundWidth , healthBarBackgroundHeight);
        game.shapeRenderer.rect(viewport.getWorldWidth() - HUDMargin -healthBarBackgroundWidth , healthBarBackgroundPositionY ,
            healthBarBackgroundWidth , healthBarBackgroundHeight);

        //draw the fighter health bar rectangles
        game.shapeRenderer.setColor(HEALTH_BAR_COLOR);
        float healthBarWidth = healthBarMaxWidth * game.player.getLife() /Fighter.MAX_LIFE;
        game.shapeRenderer.rect(HUDMargin + healthBarBackgroundPadding , healthBarPositionY , healthBarWidth , healthBarHeight);
        healthBarWidth = healthBarMaxWidth * game.opponent.getLife() / Fighter.MAX_LIFE;
        game.shapeRenderer.rect(viewport.getWorldWidth() - HUDMargin -healthBarBackgroundPadding - healthBarWidth ,
            healthBarPositionY , healthBarWidth , healthBarHeight);

        game.shapeRenderer.end();
        game.batch.begin();

        //draw the fighter name
        smallFont.draw(game.batch, game.player.getName(),
            HUDMargin + healthBarBackgroundPadding + healthBarPadding, fighterNamePositionY);

        smallFont.draw(game.batch, game.opponent.getName() ,
            viewport.getWorldWidth() - HUDMargin - healthBarBackgroundPadding - healthBarPadding ,
            fighterNamePositionY , 0 , Align.right , false);

        //draw the round timer
        if (roundTimer <  CRITICAL_ROUND_TIME) {
            mediumFont.setColor(CRITICAL_ROUND_TIME_COLOR);
        }
        mediumFont.draw(game.batch , String.format(Locale.getDefault(), "%02d" ,(int) roundTimer) , viewport.getWorldWidth() / 2f -
            mediumFont.getSpaceXadvance() * 2.3f, viewport.getWorldHeight() - HUDMargin);

        mediumFont.setColor(DEFAULT_FONT_COLOR);
    }

    // RENDER START ROUND TEXT
    private void renderStartRoundText() {
        String text;
        if (roundStateTime < START_ROUND_DELAY * 0.5f) {
            text = "ROUND" + currentRound;
        } else {
            text = "FIGHT!";
        }
        mediumFont.draw(game.batch, text,
            viewport.getWorldWidth() / 2f , viewport.getWorldHeight() / 2f , 0 , Align.center , false);
    }

    //RENDER PAUSE BUTTON
    private void renderPauseButton() {
        pauseButtonSprite.setPosition(viewport.getWorldWidth() - PAUSE_BUTTON_MARGIN -pauseButtonSprite.getWidth(),
            PAUSE_BUTTON_MARGIN);

        pauseButtonSprite.draw(game.batch);
    }

    // RENDER GAME OVER
    private void renderGameOverOverlay() {
        //cover the game area with a partially transparent black rectangle to darken the screen
        game.batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        game.shapeRenderer.setColor(0,0,0,0.7f);
        game.shapeRenderer.rect(0,0, viewport.getWorldWidth(), viewport.getWorldHeight());

        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        game.batch.begin();

        //calculate the layout dimensions
        float textMarginBottom = 2f;
        float buttonSpacing = 0.5f;
        float layoutHeight = largeFont.getCapHeight() + textMarginBottom +
            playAgainButtonSprite.getHeight() + buttonSpacing + mainMenuButtonSprite.getHeight();
        float layoutPositionY = viewport.getWorldHeight() /2f - layoutHeight /2f;

        //draw the buttons
        mainMenuButtonSprite.setPosition(viewport.getWorldWidth() / 2f - mainMenuButtonSprite.getWidth() /2f,
            layoutPositionY);
        mainMenuButtonSprite.draw(game.batch);

        playAgainButtonSprite.setPosition(viewport.getWorldWidth() / 2f - playAgainButtonSprite.getWidth() /2f,
            layoutPositionY + mainMenuButtonSprite.getHeight() + buttonSpacing);
        playAgainButtonSprite.draw(game.batch);

        //draw the text
        String text = roundsWon > roundsLost ? "YOU WON!" : "YOU LOST!";
        largeFont.draw(game.batch, text , viewport.getWorldWidth() / 2f ,
            playAgainButtonSprite.getY() + playAgainButtonSprite.getHeight() + textMarginBottom +
                largeFont.getCapHeight() , 0 , Align.center , false);
    }

    //RENDER PAUSE GAME
    private void renderPauseOverlay() {
        //cover the game area with a partially transparent black rectangle to darken the screen
        game.batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        game.shapeRenderer.setColor(0,0,0,0.7f);
        game.shapeRenderer.rect(0,0, viewport.getWorldWidth(), viewport.getWorldHeight());

        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        game.batch.begin();

        //calculate the layout dimensions
        float textMarginBottom = 2f;
        float buttonSpacing = 0.5f;
        float layoutHeight = largeFont.getCapHeight() + textMarginBottom +
            continueButtonSprite.getHeight() + buttonSpacing + mainMenuButtonSprite.getHeight();
        float layoutPositionY = viewport.getWorldHeight() /2f - layoutHeight /2f;

        //draw the buttons
        mainMenuButtonSprite.setPosition(viewport.getWorldWidth() / 2f - mainMenuButtonSprite.getWidth() /2f,
            layoutPositionY);
        mainMenuButtonSprite.draw(game.batch);

        continueButtonSprite.setPosition(viewport.getWorldWidth() / 2f - continueButtonSprite.getWidth() /2f,
            layoutPositionY + mainMenuButtonSprite.getHeight() + buttonSpacing);
        continueButtonSprite.draw(game.batch);

        //draw the text

        largeFont.draw(game.batch, "GAME PAUSED" , viewport.getWorldWidth() / 2f ,
            continueButtonSprite.getY() + continueButtonSprite.getHeight() + textMarginBottom +
                largeFont.getCapHeight() , 0 , Align.center , false);
    }

    // UPDATING THE GAME
    private void update(float deltaTime) {
        if (roundState == RoundState.STARTING && roundStateTime >= START_ROUND_DELAY) {
            //if the start round delay has been reached , start the fight
            roundState = RoundState.IN_PROGRESS;
            roundStateTime = 0f;
        } else if (roundState == RoundState.ENDING && roundStateTime >= END_ROUND_DELAY) {
            //if the end round delay has been reached and player has won or lost more than half of the max number of orunds,
            //end the game ; otherwise start the next round
            if (roundsWon > MAX_ROUNDS / 2 || roundsLost > MAX_ROUNDS / 2) {
                gameState = GameState.GAME_OVER;
            }else {
                currentRound++;
                startRound();
            }
        }else {
            //increment the round state time by delay time
            roundStateTime += deltaTime;
        }

        game.player.update(deltaTime);
        game.opponent.update(deltaTime);

        //update blood splatters
        for (int i = 0; i<BLOOD_SPLATTER_AMOUNT; i++) {
            playerBloodSplatters[i].update(deltaTime);
            opponentBloodSplatters[i].update(deltaTime);
        }

        // update the blood pools
        for (BloodPool bloodPool : bloodPools) {
            bloodPool.update(deltaTime);
        }

        //make sure the fighters are facing each other
        if(game.player.getPosition().x <= game.opponent.getPosition().x ){
            game.player.faceRight();
            game.opponent.faceLeft();
        }else {
            game.player.faceLeft();
            game.opponent.faceRight();
        }

        //keep the fighters within the bounds of the ring
        keepWithinRingBounds(game.player.getPosition());
        keepWithinRingBounds(game.opponent.getPosition());

        if (roundState == RoundState.IN_PROGRESS) {
            //if the round is in progress, decrease the round timer by delta time
            roundTimer -= deltaTime;
            if (roundTimer <= 0f) {
                //if the round timer has finished and player has the same or more life than opponent ,
                // player wins the round; otherwise , player loses the round
                if (game.player.getLife() >= game.opponent.getLife()) {
                    winRound();
                } else {
                    loseRound();
                }
            }

            //perform the AI for opponent
            performOpponentAi(deltaTime);

            //check if the fighter are within contact distance
            if (areWithContactDistance(game.player.getPosition(), game.opponent.getPosition())) {
                if (game.player.isAttackActive()) {
                    //if the fighters are within contact distance and player is actively attacking, opponents gets hit
                    game.opponent.getHit(Fighter.HIT_STRENGTH);

                    if (game.opponent.isBlocking()) {
                        //if opponent is Blocking, play block sound
                        game.audioManager.playSound(Assets.BLOCK_SOUND);
                    } else {
                        //if the opponent isn't blocking , play hit sound
                        game.audioManager.playSound(Assets.HIT_SOUND);

                        //spill some blood
                        spilledBlood(game.opponent);
                    }

                    //deactivate player attack
                    game.player.makeContact();

                    //check if the opponent has lost
                    if (game.opponent.hasLost()) {
                        //if opponent has lost , player wins the round
                        winRound();
                    }
                } else {
                    if (game.opponent.isAttackActive()) {
                        //if the fighters are within contact distance and opponent is actively attacking, player gets hit
                        game.player.getHit(Fighter.HIT_STRENGTH);

                        if (game.player.isBlocking()) {
                            //if game player is Blocking, play block sound
                            game.audioManager.playSound(Assets.BLOCK_SOUND);
                        } else {
                            //if the player isn't blocking , play hit sound
                            game.audioManager.playSound(Assets.HIT_SOUND);

                            //spill some blood
                            spilledBlood(game.player);
                        }

                        //deactivate opponent's attack
                        game.opponent.makeContact();

                        //check if the player has lost
                        if (game.player.hasLost()) {
                            //if player has lost , player loses the round
                            loseRound();
                        }
                    }
                }
            }
        }
    }

    //SPILLING BLOOD
    private void spilledBlood(Fighter fighter) {
        //use the given fighter to get the correct blood splatter array and current index
        BloodSplatter[] bloodSplatters;

        int currentBloodSplatterIndex;

        if (fighter.equals(game.player)) {
            bloodSplatters = playerBloodSplatters;
            currentBloodSplatterIndex = currentPlayerBloodSplatterIndex;
        } else {
            bloodSplatters = opponentBloodSplatters;
            currentBloodSplatterIndex = currentOpponentBloodSplatterIndex;
        }

        //activate the current blood splatter in the array
        bloodSplatters[currentBloodSplatterIndex].activate(fighter.getPosition().x + BLOOD_SPLATTER_OFFSET_X ,
            fighter.getPosition().y + BLOOD_SPLATTER_OFFSET_Y);

        //increment the correct current blood splatter index ,
        // or return to the first if the end of the array has been reached
        if (fighter.equals(game.player)) {
            if (currentPlayerBloodSplatterIndex < BLOOD_SPLATTER_AMOUNT - 1) {
                currentPlayerBloodSplatterIndex ++ ;
            } else {
                currentPlayerBloodSplatterIndex = 0;
            }
        } else {
            if (currentOpponentBloodSplatterIndex < BLOOD_SPLATTER_AMOUNT - 1) {
                currentOpponentBloodSplatterIndex ++ ;
            } else {
                currentOpponentBloodSplatterIndex = 0;
            }
        }

        //activate the current blood pool in the array
        bloodPools[currentBloodPoolIndex].activate(fighter.getPosition().x , fighter.getPosition().y);

        //increment the current blood pool index , or return to the first if the end of the blood pool
        //array has been reached
        if (currentBloodPoolIndex < BLOOD_POOL_AMOUNT -1) {
            currentBloodPoolIndex++;
        }else {
            currentBloodPoolIndex = 0;
        }
    }

    //keep player inside the ring
    private void keepWithinRingBounds(Vector2 position) {
        if(position.y < RING_MIN_Y) {
            position.y = RING_MIN_Y;
        } else if (position.y > RING_MAX_Y) {
            position.y = RING_MAX_Y;
        }

        if (position.x < position.y / RING_SLOPE + RING_MIN_X) {
            position.x = position.y / RING_SLOPE + RING_MIN_X;
        } else if (position.x > position.y / -RING_SLOPE + RING_MAX_X) {
            position.x = position.y / -RING_SLOPE + RING_MAX_X;
        }
    }

    // MAKING THE OPPONENT AI DO THINGS
    private boolean areWithContactDistance(Vector2 position1 , Vector2 position2) {
        //determine if the position are within the distance in which contact is possible
        float xDistance = Math.abs(position1.x - position2.x);
        float yDistance = Math.abs(position1.y - position2.y);
        return xDistance <=FIGHTER_CONTACT_DISTANCE_X && yDistance <= FIGHTER_CONTACT_DISTANCE_Y;
    }
    private void performOpponentAi(float deltaTime) {
        // check if the opponent is making a contact decision (attack, block , etc.)
        if (opponentAiMakingContactDecision) {
            if (game.opponent.isBlocking()) {
                // if the opponent is blocking , stop blocking if the fighters are not within contact distance ,
                //or player isn't attacking or player has attacked and made contact
                if (!areWithContactDistance(game.player.getPosition() , game.opponent.getPosition()) ||
                    !game.player.isAttacking() || game.player.hasMadeContact()) {
                    game.opponent.stopBlocking();
                }
            } else if (!game.opponent.isAttacking()) {
                // if opponent isn't currently attacking check if the fighters are within the contact distance
                if (areWithContactDistance(game.player.getPosition(), game.opponent.getPosition())) {
                    if (opponentAiTimer <= 0f) {
                        // if the fighters are within contact distances and the opponent AI timer has finished,
                        //make a contact decision
                        opponentAiMakeContactDecision();
                    }else {
                        //decrease the opponent AI timer by delta time
                        opponentAiTimer -= deltaTime;
                    }
                }else {
                    //if the fighters aren't within contact decision, opponent shouldn't make a contact decision
                    opponentAiMakingContactDecision = false;
                }
            }
        } else {
            if (areWithContactDistance(game.player.getPosition(), game.opponent.getPosition())) {
                //if the opponent isn't making contact decision and the fighters are within contact distance,
                //make a contact decision
                opponentAiMakeContactDecision();
            } else {

                if (opponentAiTimer <= 0f) {
                    // if the fighters are not within contact distance and the opponent AI timer has finished ,
                    //either pursue player or move in a random direction
                    float pursueChance = difficulty == GlobalVariables.Difficulty.EASY ? OPPONENT_AI_PURSUE_PLAYER_CHANCE_EASY:
                        difficulty == GlobalVariables.Difficulty.MEDIUM ? OPPONENT_AI_PURSUE_PLAYER_CHANCE_MEDIUM:
                            OPPONENT_AI_PURSUE_PLAYER_CHANCE_HARD;
                    if (MathUtils.random() <= pursueChance) {
                        //opponent is pursuing player
                        opponentAiPursuingPlayer = true;

                        //move in the direction of player
                        opponentAiMoveTowardPlayer();
                    }else {
                        //opponent AI is not pursuing player
                        opponentAiPursuingPlayer = false;

                        //move in a random direction
                        opponentAiMoveRandomly();
                    }
                    //set the opponent AI timer to the non-contact decision delay
                    opponentAiTimer = OPPONENT_AI_NON_CONTACT_DECISION_DELAY;
                }else {
                    //if the opponent is pursuing player, move in the direction of player
                    if (opponentAiPursuingPlayer) {
                        opponentAiMoveTowardPlayer();
                    }

                    //decrease the opponent AI timer by delta time
                    opponentAiTimer -= deltaTime;
                }
            }
        }
    }
    private void opponentAiMakeContactDecision() {
        opponentAiMakingContactDecision = true;

        //make a contact decision
        if (game.player.isAttacking()) {
            //if player is attacking and hasn't yet made contact, determine whether to block player's attack
            //or move away from player
            if (!game.player.hasMadeContact()) {
                if (MathUtils.random() <= OPPONENT_AI_BLOCK_CHANCE) {
                    //block player's attack
                    game.opponent.block();
                } else {
                    //move away from the player
                    opponentAiMoveAwayFromPlayer();
                }
            }
        } else {
            //if player isn't attacking , determine whether to attack player or move away from player
            if (MathUtils.random() <= OPPONENT_AI_ATTACK_CHANCE) {
                // attack player (equal chance of punching or kicking)
                if (MathUtils.random(1) == 0) {
                    game.opponent.punch();
                } else {
                    game.opponent.kick();
                }
            } else {
                // move away from player
                opponentAiMoveAwayFromPlayer();
            }
        }
        //set the opponent AI timer to a difficulty-based contact decision delay
        switch (difficulty) {
            case EASY:
                opponentAiTimer = OPPONENT_AI_CONTACT_DECISION_DELAY_EASY;
                break;
            case MEDIUM:
                opponentAiTimer = OPPONENT_AI_CONTACT_DECISION_DELAY_MEDIUM;
                break;
            default:
                opponentAiTimer = OPPONENT_AI_CONTACT_DECISION_DELAY_HARD;
                break;
        }
    }
    private void opponentAiMoveTowardPlayer() {
        //move in the direction of player's position
        Vector2 playerPosition = game.player.getPosition();
        Vector2 opponentPosition = game.opponent.getPosition();
        if (opponentPosition.x > playerPosition.x + FIGHTER_CONTACT_DISTANCE_X) {
            game.opponent.moveLeft();
        } else if (opponentPosition.x < playerPosition.x - FIGHTER_CONTACT_DISTANCE_X) {
            game.opponent.moveRight();
        } else {
            game.opponent.stopMovingLeft();
            game.opponent.stopMovingRight();
        }
        if (opponentPosition.y < playerPosition.y - FIGHTER_CONTACT_DISTANCE_Y) {
            game.opponent.moveUp();
        } else if (opponentPosition.y > playerPosition.y + FIGHTER_CONTACT_DISTANCE_Y) {
            game.opponent.moveDown();
        } else {
            game.opponent.stopMovingUp();
            game.opponent.stopMovingDown();
        }
    }
    private void opponentAiMoveRandomly() {
        //randomly set opponent's horizontal movement
        switch (MathUtils.random(2)) {
            case 0:
                game.opponent.moveLeft();
                break;
            case 1:
                game.opponent.moveRight();
                break;
            default:
                game.opponent.stopMovingLeft();
                game.opponent.stopMovingRight();
        }

        //randomly set opponent's vertical movement
        switch (MathUtils.random(2)) {
            case 0:
                game.opponent.moveUp();
                break;
            case 1:
                game.opponent.moveDown();
                break;
            default:
                game.opponent.stopMovingUp();
                game.opponent.stopMovingDown();
        }
    }
    private void opponentAiMoveAwayFromPlayer() {
        //move away from player's position
        Vector2 playerPosition = game.player.getPosition();
        Vector2 opponentPosition = game.opponent.getPosition();
        if (opponentPosition.x > playerPosition.x) {
            game.opponent.moveRight();
        } else {
            game.opponent.moveLeft();
        }

        if (opponentPosition.y > playerPosition.y) {
            game.opponent.moveUp();
        } else {
            game.opponent.moveDown();
        }
    }

    @Override
    public void resize(int width, int height) {
        //update the viewport with the new screen size
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        //if the game is running , pause it
        if (gameState == GameState.RUNNING) {
            pauseGame();
        }

        //pause music
        game.audioManager.pauseMusic();
    }

    @Override
    public void resume() {
        //resume music (if it's enabled)
        game.audioManager.playMusic();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }


    //keyboard inputs
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            if (gameState == GameState.RUNNING) {
                //if the game is running and the space key has been pressed , skip any round delays
                if (roundState == RoundState.ENDING) {
                    roundStateTime = START_ROUND_DELAY;
                } else if (roundState == RoundState.ENDING) {
                    roundStateTime = END_ROUND_DELAY;
                }
            } else if (gameState == GameState.GAME_OVER) {
                // if the game is over and the space keys has been passed , restart the game
                startGame();
            } else {
                // if the game is paused and the space key has been pressed , resume the game
                resumeGame();
            }
        } else if ((gameState == GameState.RUNNING || gameState == GameState.PAUSED) &&
            keycode == Input.Keys.P) {
            //if the game is running or paused and the P key has been pause or resume the game
            if (gameState == GameState.RUNNING) {
                pauseGame();
            } else {
                resumeGame();
            }
        } else if (keycode == Input.Keys.M) {
            //toggle music on or off
            game.audioManager.toggleMusic();
        } else if (keycode == Input.Keys.L) {
            //change the difficulty
            switch (difficulty) {
                case EASY:
                    difficulty = GlobalVariables.Difficulty.MEDIUM;
                    break;
                case MEDIUM:
                    difficulty = GlobalVariables.Difficulty.HARD;
                    break;
                default:
                    difficulty = GlobalVariables.Difficulty.EASY;
            }
        } else if (keycode == Input.Keys.K) {
            // toggle blood on / off
            showingBlood = !showingBlood;
        } else {
            if (roundState == RoundState.IN_PROGRESS) {
                //check if player has pressed a movement key
                if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
                    game.player.moveLeft();
                } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
                    game.player.moveRight();
                }
                if(keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    game.player.moveUp();
                } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    game.player.moveDown();
                }
            }

            //check if the player has pressed a block or attack key
            if (keycode == Input.Keys.B) {
                game.player.block();
            } else if (keycode == Input.Keys.F) {
                game.player.punch();
            } else if (keycode == Input.Keys.V) {
                game.player.kick();
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        //if player has released a movement key , stop moving in tha direction
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            game.player.stopMovingLeft();
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            game.player.stopMovingRight();
        }
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            game.player.stopMovingUp();
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            game.player.stopMovingDown();
        }

        //if player has released the block key , stop blocking
        if (keycode == Input.Keys.B) {
            game.player.stopBlocking();
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //convert the screen coordinates of the touch /click into world coordinates
        Vector3 position = new Vector3(screenX , screenY , 0);
        viewport.getCamera().unproject(position , viewport.getScreenX(), viewport.getScreenY(),
            viewport.getScreenWidth(), viewport.getScreenHeight());

        if (gameState == GameState.RUNNING) {
            if (pauseButtonSprite.getBoundingRectangle().contains(position.x , position.y)) {
                //if the pause button has been touched , pause the game
                pauseGame();

                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);
            } else if (roundState == RoundState.STARTING) {
                //if the round is starting and the screen has been touched , skip the start round delay
                roundStateTime = START_ROUND_DELAY;
            } else if (roundState == RoundState.ENDING) {
                //if the round is ending and the screen has been touched , skip the end round delay
                roundStateTime = END_ROUND_DELAY;
            }
        } else {
            if (gameState == GameState.GAME_OVER && playAgainButtonSprite.getBoundingRectangle().contains(position.x , position.y)) {
                //if the game is over and the play again button has been pressed , start the game from the beginning
                startGame();

                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);
            } else if (gameState == GameState.PAUSED && continueButtonSprite.getBoundingRectangle().contains(position.x, position.y)) {
                //if the game is paused and the continue button has been touched , resume the game
                resumeGame();

                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

            } else if (mainMenuButtonSprite.getBoundingRectangle().contains(position.x , position.y)) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //stop all the game sounds
                game.audioManager.stopGameSounds();

                //resume music if the game is paused
                if (gameState == GameState.PAUSED) {
                    game.audioManager.playMusic();
                }

                //deactivate all the blood splatters
                for (int i = 0; i < BLOOD_SPLATTER_AMOUNT; i++) {
                    playerBloodSplatters[i].deactivate();
                    opponentBloodSplatters[i].deactivate();
                }

                //switch to the main menu screen
                game.setScreen(game.mainMenuScreen);
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }
}
