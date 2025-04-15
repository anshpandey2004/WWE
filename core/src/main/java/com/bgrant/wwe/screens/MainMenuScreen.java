package com.bgrant.wwe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bgrant.wwe.SFS;
import com.bgrant.wwe.objects.FighterChoice;
import com.bgrant.wwe.resources.Assets;
import com.bgrant.wwe.resources.GlobalVariables;

public class MainMenuScreen implements Screen {

    private final SFS game;
    private final Stage stage;
    private final TextureAtlas menuItemAtlas;

    //image widgets
    private Image logoImage;
    private Image fighterDisplayBackgroundImage;
    private Image fighterDisplayImage;

    //button widgets
    private Button playGameButton;
    private Button settingsButton;
    private Button quitGameButton;
    private Button previousFighterButton;
    private Button nextFighterButton;

    //label widgets
    private Label fighterDisplayNameLabel;

    //fighter choice
    private int currentFighterChoiceIndex;


    public MainMenuScreen(SFS game) {
        this.game = game;

        //set up the stage
        stage = new Stage();
        stage.setViewport(new ExtendViewport(GlobalVariables.WORLD_WIDTH , GlobalVariables.MIN_WORLD_HEIGHT ,
                GlobalVariables.WORLD_WIDTH , 0 , stage.getCamera()));

        //get the menu items texture atlas from the asset manager
        menuItemAtlas = game.assets.manager.get(Assets.MENU_ITEMS_ATLAS);

        //create the widgets
        createImages();
        createButtons();
        createLabels();

        // create the tables
        createTables();

    }

    private void createImages() {
        //create the logo image
        logoImage = new Image(menuItemAtlas.findRegion("Logo"));
        logoImage.setSize(logoImage.getWidth() * GlobalVariables.WORLD_SCALE ,
                            logoImage.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the fighter  display background image
        fighterDisplayBackgroundImage = new Image(menuItemAtlas.findRegion("FighterDisplayBackground"));
        fighterDisplayBackgroundImage.setSize(fighterDisplayBackgroundImage.getWidth() * GlobalVariables.WORLD_SCALE ,
            fighterDisplayBackgroundImage.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the fighter  display image
        fighterDisplayImage = new Image(menuItemAtlas.findRegion("FighterDisplay"));
        fighterDisplayImage.setSize(fighterDisplayImage.getWidth() * GlobalVariables.WORLD_SCALE ,
            fighterDisplayImage.getHeight() * GlobalVariables.WORLD_SCALE);
    }

    private void createButtons() {
        //create the play game button
        Button.ButtonStyle playGameButtonStyle = new Button.ButtonStyle();
        playGameButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("PlayGameButton"));
        playGameButtonStyle.down = new TextureRegionDrawable(menuItemAtlas.findRegion("PlayGameButtonDown"));
        playGameButton = new Button(playGameButtonStyle);
        playGameButton.setSize(playGameButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            playGameButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the settings button
        Button.ButtonStyle settingsButtonStyle = new Button.ButtonStyle();
        settingsButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("SettingsButton"));
        settingsButtonStyle.down = new TextureRegionDrawable(menuItemAtlas.findRegion("SettingsButtonDown"));
        settingsButton = new Button(settingsButtonStyle);
        settingsButton.setSize(settingsButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            settingsButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the quit game button
        Button.ButtonStyle quitGameButtonStyle = new Button.ButtonStyle();
        quitGameButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("QuitGameButton"));
        quitGameButtonStyle.down = new TextureRegionDrawable(menuItemAtlas.findRegion("QuitGameButtonDown"));
        quitGameButton = new Button(quitGameButtonStyle);
        quitGameButton.setSize(quitGameButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            quitGameButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the triangle button style
        Button.ButtonStyle triangleButtonStyle = new Button.ButtonStyle();
        triangleButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("TriangleButton"));
        triangleButtonStyle.down = new TextureRegionDrawable(menuItemAtlas.findRegion("TriangleButtonDown"));

        //create the previous fighter button
        previousFighterButton = new Button(triangleButtonStyle);
        previousFighterButton.setSize(previousFighterButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            previousFighterButton.getHeight() * GlobalVariables.WORLD_SCALE);
        previousFighterButton.setTransform(true);
        previousFighterButton.setOrigin(previousFighterButton.getWidth() / 2f,
                                            previousFighterButton.getHeight() / 2f);
        previousFighterButton.setScale(-1);


        //create the next fighter button
        nextFighterButton = new Button(triangleButtonStyle);
        nextFighterButton.setSize(nextFighterButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            nextFighterButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //add the button listeners
        addButtonListeners();
    }

    private void addButtonListeners() {
        //add the play game button listener
        playGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //choose a random opponent fighter from  the fighter choice list,
                // making sure it's different from player's fighter
                int index = MathUtils.random(game.fighterChoiceList.size() - 1);
                FighterChoice fighterChoice = game.fighterChoiceList.get(index);
                while (fighterChoice.getName().equals(game.player.getName())) {
                    index = MathUtils.random(game.fighterChoiceList.size() - 1);
                    fighterChoice = game.fighterChoiceList.get(index);
                }

                game.opponent.setName(fighterChoice.getName());
                game.opponent.setColor(fighterChoice.getColor());

                //switch to the game screen
                game.setScreen(game.gameScreen);
            }
        });

        //add the setting button listener
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //switch to the settings screen
                game.setScreen(game.settingsScreen);
            }
        });

        //add the quit game button listener
        quitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //close out the game
                Gdx.app.exit();
            }
        });

        //add the previous fighter button listener
        previousFighterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //choose the previous fighter in the fighter choice list, or choose te last fighter
                //if the beginning of the list has been reached
                if (currentFighterChoiceIndex > 0) {
                    currentFighterChoiceIndex--;
                } else {
                    currentFighterChoiceIndex = game.fighterChoiceList.size() - 1;
                }

                //set the name and color of player's fighter and the fighter display to those of the chosen fighter
                FighterChoice fighterChoice = game.fighterChoiceList.get(currentFighterChoiceIndex);
                game.player.setName(fighterChoice.getName());
                game.player.setColor(fighterChoice.getColor());
                fighterDisplayImage.setColor(fighterChoice.getColor());
                fighterDisplayNameLabel.setText(fighterChoice.getName().toUpperCase());
            }
        });

        //add the next fighter button listener
        nextFighterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //choose the next fighter in the fighter choice list, or choose the first fighter
                //if the beginning of the list has been reached
                if (currentFighterChoiceIndex < game.fighterChoiceList.size() - 1) {
                    currentFighterChoiceIndex++;
                } else {
                    currentFighterChoiceIndex = 0;
                }

                //set the name and color of player's fighter and the fighter display to those of the chosen fighter
                FighterChoice fighterChoice = game.fighterChoiceList.get(currentFighterChoiceIndex);
                game.player.setName(fighterChoice.getName());
                game.player.setColor(fighterChoice.getColor());
                fighterDisplayImage.setColor(fighterChoice.getColor());
                fighterDisplayNameLabel.setText(fighterChoice.getName().toUpperCase());
            }
        });

    }

    private void createLabels() {
        //get the small fonts from the asset manager
        BitmapFont smallFont = game.assets.manager.get(Assets.SMALL_FONT);
        smallFont.getData().setScale(GlobalVariables.WORLD_SCALE);
        smallFont.setUseIntegerPositions(false);

        //create the label style
        Label.LabelStyle fighterDisplayNameLabelStyle = new Label.LabelStyle();
        fighterDisplayNameLabelStyle.font = smallFont;
        fighterDisplayNameLabelStyle.fontColor = Color.BLACK;

        //create  the fighter display name label
        fighterDisplayNameLabel = new Label("" , fighterDisplayNameLabelStyle);
    }

    private void createTables() {
//        stage.setDebugAll(true); // for the lines to set the main screen the green/blue lines for the alignment

        //create the main table and add it to the stage
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setRound(false);
        stage.addActor(mainTable);

        //create the left side table
        Table leftSideTable = new Table();
        leftSideTable.setRound(false);

        //add the logo image to the left side of the table and start a new row
        leftSideTable.add(logoImage).size(logoImage.getWidth() , logoImage.getHeight());
        leftSideTable.row().padTop(1.5f);

        //create the fighter display table and set its background to the fighter display background image
        Table fighterDisplayTable = new Table();
        fighterDisplayTable.setRound(false);
        fighterDisplayTable.setBackground(fighterDisplayBackgroundImage.getDrawable());
        fighterDisplayTable.setSize(fighterDisplayBackgroundImage.getWidth() , fighterDisplayBackgroundImage.getHeight());

        //create the fighter display inner table add the previous and next fighter buttons and
        // the fighter display image to it
        Table fighterDisplayInnerTable = new Table();
        fighterDisplayInnerTable.setRound(false);
        fighterDisplayInnerTable.add(previousFighterButton).size(previousFighterButton.getWidth() ,
                                        previousFighterButton.getHeight());
        fighterDisplayInnerTable.add(fighterDisplayImage).size(fighterDisplayImage.getWidth() ,
                                        fighterDisplayImage.getHeight()).padLeft(0.5f).padRight(0.5f);
        fighterDisplayInnerTable.add(nextFighterButton).size(nextFighterButton.getWidth() ,
                                        nextFighterButton.getHeight());
        fighterDisplayInnerTable.pack();

        //fill in the fighter display table with an empty row on top for alignment ,
        // the fighter display inner table in the center and the fighter display name label at the bottom
        fighterDisplayTable.add().height(fighterDisplayBackgroundImage.getHeight() / 2f -
            fighterDisplayImage.getHeight() / 2f - 0.5f);
        fighterDisplayTable.row();
        fighterDisplayTable.add(fighterDisplayInnerTable).size(fighterDisplayInnerTable.getWidth() ,
                                    fighterDisplayInnerTable.getHeight());
        fighterDisplayTable.row();
        fighterDisplayTable.add(fighterDisplayNameLabel).height(fighterDisplayBackgroundImage.getHeight() / 2f -
                                    fighterDisplayImage.getHeight() / 2f - 0.5f);

        //add the fighter display table to the left side table
        leftSideTable.add(fighterDisplayTable).size(fighterDisplayTable.getWidth() , fighterDisplayTable.getHeight());

        //add the left side table to the main table
        mainTable.add(leftSideTable);

        //create the right side table
        Table rightSideTable = new Table();
        rightSideTable.setRound(false);

        //add the play game , settings , and quit game buttons to the right side table
        rightSideTable.add(playGameButton).size(playGameButton.getWidth() , playGameButton.getHeight());
        rightSideTable.row().padTop(1f);

        rightSideTable.add(settingsButton).size(settingsButton.getWidth() , settingsButton.getHeight());
        rightSideTable.row().padTop(1f);

        rightSideTable.add(quitGameButton).size(quitGameButton.getWidth() , quitGameButton.getHeight());

        // add the right side table to the main table
        mainTable.add(rightSideTable).padLeft(2f);
    }

    @Override
    public void show() {
        //set the stage as the input processor
        Gdx.input.setInputProcessor(stage);

        //set the fighter display name label's text to the name of player's fighter
        fighterDisplayNameLabel.setText(game.player.getName().toUpperCase());

        //set the fighter display image's color to the color of player's fighter
        fighterDisplayImage.setColor(game.player.getColor());

        //find the index of current chosen fighter in the fighter choice list
        currentFighterChoiceIndex = 0;
        for (int i = 0; i < game.fighterChoiceList.size(); i++) {
            if (game.fighterChoiceList.get(i).getName().equals(game.player.getName())) {
                currentFighterChoiceIndex = i;
                break;
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(GlobalVariables.BLUE_BACKGROUND);

        //tell the screen to do actions and draw itself
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //update the stage's viewport with the new screen size
        stage.getViewport().update(width, height , true);
    }

    @Override
    public void pause() {
        //pause music
        game.audioManager.pauseMusic();
    }

    @Override
    public void resume() {
        //resume music
        game.audioManager.playMusic();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
