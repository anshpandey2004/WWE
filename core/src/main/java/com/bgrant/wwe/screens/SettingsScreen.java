package com.bgrant.wwe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bgrant.wwe.SFS;
import com.bgrant.wwe.resources.Assets;
import com.bgrant.wwe.resources.GlobalVariables;

public class SettingsScreen implements Screen {
    private final SFS game;
    private final Stage stage;
    private final TextureAtlas menuItemAtlas;

    //image widgets
    private Image settingsImage;
    private Image musicSettingBackgroundImage;
    private Image soundsSettingBackgroundImage;
    private Image difficultySettingBackgroundImage;
    private Image fullScreenSettingBackgroundImage;
    private Image bloodSettingBackgroundImage;
    private Image easyImage;
    private Image mediumImage;
    private Image hardImage;

    //button widgets
    private Button backButton;
    private Button musicToggleButton;
    private Button soundsToggleButton;
    private Button previousDifficultyButton;
    private Button nextDifficultyButton;
    private Button fullScreenCheckButton;
    private Button bloodCheckButton;

    public SettingsScreen(SFS game) {
        this.game = game;

        //set up the stage
        stage = new Stage();
        stage.setViewport(new ExtendViewport(GlobalVariables.WORLD_WIDTH , GlobalVariables.MIN_WORLD_HEIGHT ,
                                GlobalVariables.WORLD_WIDTH ,0,  stage.getCamera()));

        //get the menu items texture atlas from the asset manager
        menuItemAtlas = game.assets.manager.get(Assets.MENU_ITEMS_ATLAS);

        //create the widgets
        createImages();
        createButtons();

        //create the tables
        createTables();
    }

    private void createImages() {
        //create the settings image
        settingsImage = new Image(menuItemAtlas.findRegion("Settings"));
        settingsImage.setSize(settingsImage.getWidth() * GlobalVariables.WORLD_SCALE,
                                    settingsImage.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the settings background images
        musicSettingBackgroundImage = new Image(menuItemAtlas.findRegion("MusicSettingBackground"));
        musicSettingBackgroundImage.setSize(musicSettingBackgroundImage.getWidth() * GlobalVariables.WORLD_SCALE ,
                                                musicSettingBackgroundImage.getHeight() * GlobalVariables.WORLD_SCALE);

        soundsSettingBackgroundImage = new Image(menuItemAtlas.findRegion("SoundsSettingBackground"));
        soundsSettingBackgroundImage.setSize(soundsSettingBackgroundImage.getWidth() * GlobalVariables.WORLD_SCALE ,
                                                soundsSettingBackgroundImage.getHeight() * GlobalVariables.WORLD_SCALE);

        difficultySettingBackgroundImage = new Image(menuItemAtlas.findRegion("DifficultySettingBackground"));
        difficultySettingBackgroundImage.setSize(difficultySettingBackgroundImage.getWidth() * GlobalVariables.WORLD_SCALE ,
            difficultySettingBackgroundImage.getHeight() * GlobalVariables.WORLD_SCALE);

        fullScreenSettingBackgroundImage = new Image(menuItemAtlas.findRegion("FullScreenSettingBackground"));
        fullScreenSettingBackgroundImage.setSize(fullScreenSettingBackgroundImage.getWidth() * GlobalVariables.WORLD_SCALE ,
            fullScreenSettingBackgroundImage.getHeight() * GlobalVariables.WORLD_SCALE);

        bloodSettingBackgroundImage = new Image(menuItemAtlas.findRegion("BloodSettingBackground"));
        bloodSettingBackgroundImage.setSize(bloodSettingBackgroundImage.getWidth() * GlobalVariables.WORLD_SCALE ,
            bloodSettingBackgroundImage.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the difficulty image
        easyImage = new Image(menuItemAtlas.findRegion("Easy"));
        easyImage.setSize(easyImage.getWidth() * GlobalVariables.WORLD_SCALE ,
                            easyImage.getHeight() * GlobalVariables.WORLD_SCALE);

        mediumImage = new Image(menuItemAtlas.findRegion("Medium"));
        mediumImage.setSize(mediumImage.getWidth() * GlobalVariables.WORLD_SCALE ,
            mediumImage.getHeight() * GlobalVariables.WORLD_SCALE);

        hardImage = new Image(menuItemAtlas.findRegion("Hard"));
        hardImage.setSize(hardImage.getWidth() * GlobalVariables.WORLD_SCALE ,
            hardImage.getHeight() * GlobalVariables.WORLD_SCALE);
    }

    private void createButtons() {
        //create the back button
        Button.ButtonStyle backButtonStyle = new Button.ButtonStyle();
        backButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("BackButton"));
        backButtonStyle.down = new TextureRegionDrawable(menuItemAtlas.findRegion("BackButtonDown"));
        backButton = new Button(backButtonStyle);
        backButton.setSize(backButton.getWidth() * GlobalVariables.WORLD_SCALE ,
                                backButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the toggle button style
        Button.ButtonStyle toggleButtonStyle = new Button.ButtonStyle();
        toggleButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("ToggleButtonOff"));
        toggleButtonStyle.checked = new TextureRegionDrawable(menuItemAtlas.findRegion("ToggleButtonOn"));

        //create the music toggle button
        musicToggleButton = new Button(toggleButtonStyle);
        musicToggleButton.setSize(musicToggleButton.getWidth() * GlobalVariables.WORLD_SCALE ,
                                    musicToggleButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the sounds toggle button
        soundsToggleButton = new Button(toggleButtonStyle);
        soundsToggleButton.setSize(soundsToggleButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            soundsToggleButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the triangle button style
        Button.ButtonStyle triangleButtonStyle = new Button.ButtonStyle();
        triangleButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("TriangleButton"));
        triangleButtonStyle.down = new TextureRegionDrawable(menuItemAtlas.findRegion("TriangleButtonDown"));

        //create the previous difficulty button
        previousDifficultyButton = new Button(triangleButtonStyle);
        previousDifficultyButton.setSize(previousDifficultyButton.getWidth() * GlobalVariables.WORLD_SCALE ,
                                            previousDifficultyButton.getHeight() * GlobalVariables.WORLD_SCALE);
                                    //this is to turn the button other way round
        previousDifficultyButton.setTransform(true);
        previousDifficultyButton.setOrigin(previousDifficultyButton.getWidth() / 2f,
                                                previousDifficultyButton.getHeight() / 2f);
        previousDifficultyButton.setScaleX(-1);

        //create the next difficulty button
        nextDifficultyButton = new Button(triangleButtonStyle);
        nextDifficultyButton.setSize(nextDifficultyButton.getWidth() * GlobalVariables.WORLD_SCALE ,
                                            nextDifficultyButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the check button style
        Button.ButtonStyle checkButtonStyle = new Button.ButtonStyle();
        checkButtonStyle.up = new TextureRegionDrawable(menuItemAtlas.findRegion("CheckButtonOff"));
        checkButtonStyle.checked = new TextureRegionDrawable(menuItemAtlas.findRegion("CheckButtonOn"));

        //create the full screen check button
        fullScreenCheckButton = new Button(checkButtonStyle);
        fullScreenCheckButton.setSize(fullScreenCheckButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            fullScreenCheckButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //create the blood check button
        bloodCheckButton = new Button(checkButtonStyle);
        bloodCheckButton.setSize(bloodCheckButton.getWidth() * GlobalVariables.WORLD_SCALE ,
            bloodCheckButton.getHeight() * GlobalVariables.WORLD_SCALE);

        //add the button listeners
        addButtonListeners();
    }

    private void addButtonListeners() {
        //add the back button listener
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //switch to the main menu screen
                game.setScreen(game.mainMenuScreen);

            }
        });

        //add the music toggle button listener
        musicToggleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //toggle the music setting based on the button's checked state
                game.settingsManager.toggleMusicSetting(musicToggleButton.isChecked());

                //if the music setting is on enable music otherwise disable
                if (game.settingsManager.isMusicSettingOn()) {
                    game.audioManager.enableMusic();
                }else {
                    game.audioManager.disableMusic();
                }
            }
        });

        //add the sounds toggle button listener
        soundsToggleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //toggle the sounds setting based on the button's checked state
                game.settingsManager.toggleSoundSetting(soundsToggleButton.isChecked());

                //if the sounds setting is on enable sounds otherwise disable
                if (game.settingsManager.isSoundsSettingOn()) {
                    game.audioManager.enableSound();
                }else {
                    game.audioManager.disableSound();
                }
            }
        });

        // add the previous difficulty button listener
        previousDifficultyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //go to the previous difficulty settings or go to hard from easy //also making other invisible
                switch (game.settingsManager.getDifficultySetting()) {
                    case EASY:
                        game.settingsManager.setDifficultySetting(GlobalVariables.Difficulty.HARD);
                        easyImage.setVisible(false);
                        mediumImage.setVisible(false);
                        hardImage.setVisible(true);
                        break;

                    case MEDIUM:
                        game.settingsManager.setDifficultySetting(GlobalVariables.Difficulty.EASY);
                        easyImage.setVisible(true);
                        mediumImage.setVisible(false);
                        hardImage.setVisible(false);
                        break;

                    default:
                        game.settingsManager.setDifficultySetting(GlobalVariables.Difficulty.MEDIUM);
                        easyImage.setVisible(false);
                        mediumImage.setVisible(true);
                        hardImage.setVisible(false);
                }
            }
        });

        // add the next difficulty button listener
        nextDifficultyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //go to the next difficulty settings or go to easy from hard //also making other invisible
                switch (game.settingsManager.getDifficultySetting()) {
                    case EASY:
                        game.settingsManager.setDifficultySetting(GlobalVariables.Difficulty.MEDIUM);
                        easyImage.setVisible(false);
                        mediumImage.setVisible(true);
                        hardImage.setVisible(false);
                        break;

                    case MEDIUM:
                        game.settingsManager.setDifficultySetting(GlobalVariables.Difficulty.HARD);
                        easyImage.setVisible(false);
                        mediumImage.setVisible(false);
                        hardImage.setVisible(true);
                        break;

                    default:
                        game.settingsManager.setDifficultySetting(GlobalVariables.Difficulty.EASY);
                        easyImage.setVisible(true);
                        mediumImage.setVisible(false);
                        hardImage.setVisible(false);
                }
            }
        });

        //add the fullscreen check button listener
        fullScreenCheckButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //toggle the full screen setting based on the button checked state
                game.settingsManager.toggleFullScreenSetting(fullScreenCheckButton.isChecked());

                //if the full screen setting is on go to fullscreen otherwise to the window screen
                if (game.settingsManager.isFullScreenSettingOn()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(GlobalVariables.WINDOW_WIDTH , GlobalVariables.WINDOW_HEIGHT);
                }
            }
        });

        // add the blood check button listener
        bloodCheckButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //play click sound
                game.audioManager.playSound(Assets.CLICK_SOUND);

                //toggle the blood setting based on the button's checked state
                game.settingsManager.toggleBloodSetting(bloodCheckButton.isChecked());
            }
        });
    }

    private void createTables() {
//        stage.setDebugAll(true); // for alignment blue/green lines

        //create the main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setRound(false);
        stage.addActor(mainTable);

        //create the banner table
        Table bannerTable = new Table();
        bannerTable.setRound(false);

        //add the back button and the settings image to the banner table
        bannerTable.add(backButton).size(backButton.getWidth() , backButton.getHeight());
        bannerTable.add(settingsImage).size(settingsImage.getWidth() , settingsImage.getHeight());

        //add an empty cell to the banner table the same size as the back button in order to center the settings image
        bannerTable.add().size(backButton.getWidth() , backButton.getHeight());

        // add the banner table to the main table
        mainTable.add(bannerTable);
        mainTable.row().padTop(1f);

        //create the audio table
        Table audioTable = new Table();
        audioTable.setRound(false);

        //create the music table and set its background to the music setting background image
        Table musicTable = new Table();
        musicTable.setRound(false);
        musicTable.setBackground(musicSettingBackgroundImage.getDrawable());
        musicTable.setSize(musicSettingBackgroundImage.getWidth() , musicSettingBackgroundImage.getHeight());

        //add an empty cell for alignment and the music toggle  to the music table
        musicTable.add().width(musicSettingBackgroundImage.getWidth() - musicToggleButton.getWidth() - 2f);
        musicTable.add(musicToggleButton).size(musicToggleButton.getWidth() , musicToggleButton.getHeight());

        //add the music table to the audio table
        audioTable.add(musicTable).size(musicTable.getWidth() , musicTable.getHeight());

        //create the sounds table and set its background to the sounds setting background image
        Table soundsTable = new Table();
        soundsTable.setBackground(soundsSettingBackgroundImage.getDrawable());
        soundsTable.setSize(soundsSettingBackgroundImage.getWidth(), soundsSettingBackgroundImage.getHeight());

        //add an empty cell for alignment and the sounds toggle button to the sounds table
        soundsTable.add().width(soundsSettingBackgroundImage.getWidth() - soundsToggleButton.getWidth() - 2f);
        soundsTable.add(soundsToggleButton).size(soundsToggleButton.getWidth() , soundsToggleButton.getHeight());

        //add the sounds table to the audio table
        audioTable.add(soundsTable).size(soundsTable.getWidth() , soundsTable.getHeight());

        //add the audio table to the main table
        mainTable.add(audioTable);
        mainTable.row().padTop(1f);

        //create the difficulty table and set its background to the difficulty setting background image
        Table difficultyTable = new Table();
        difficultyTable.setRound(false);
        difficultyTable.setBackground(difficultySettingBackgroundImage.getDrawable());
        difficultyTable.setSize(difficultySettingBackgroundImage.getWidth() , difficultySettingBackgroundImage.getHeight());


        //create the difficulty selection table
        Table difficultySelectionTable = new Table();
        difficultySelectionTable.setRound(false);

        //create the difficulty image stack and add the difficulty image to it
        Stack difficultyImageStack = new Stack();
        difficultyImageStack.add(easyImage);
        difficultyImageStack.add(mediumImage);
        difficultyImageStack.add(hardImage);
        difficultyImageStack.setSize(easyImage.getWidth(), easyImage.getHeight());

        //add the difficulty selection buttons and the difficulty image stack to the difficulty selection table
        difficultySelectionTable.add(previousDifficultyButton).size(previousDifficultyButton.getWidth() ,
                                        previousDifficultyButton.getHeight());
        difficultySelectionTable.add(difficultyImageStack).size(difficultyImageStack.getWidth() ,
                                        difficultyImageStack.getHeight()).padLeft(0.5f).padRight(0.5f);

        difficultySelectionTable.add(nextDifficultyButton).size(nextDifficultyButton.getWidth() ,
                                        nextDifficultyButton.getHeight());
        difficultySelectionTable.pack();

        // add an empty cell for alignment and the difficulty selection table to the difficulty table
        difficultyTable.add().width(difficultySettingBackgroundImage.getWidth() -
                                            difficultySelectionTable.getWidth() - 2f);
        difficultyTable.add(difficultySelectionTable).size(difficultySelectionTable.getWidth() ,
                                            difficultySelectionTable.getHeight());

        //add the difficulty table to the main table
        mainTable.add(difficultyTable).size(difficultyTable.getWidth(), difficultyTable.getHeight());
        mainTable.row().padTop(1f);

        // create the bottom table
        Table bottomTable = new Table();
        bottomTable.setRound(false);


        //create the full screen table and set its background to the full screen setting background image
        Table fullScreenTable = new Table();
        fullScreenTable.setRound(false);
        fullScreenTable.setBackground(fullScreenSettingBackgroundImage.getDrawable());
        fullScreenTable.setSize(fullScreenSettingBackgroundImage.getWidth(), fullScreenSettingBackgroundImage.getHeight());

        // add an empty cell for alignment and the full screen check button to the full screen table
        fullScreenTable.add().width(fullScreenSettingBackgroundImage.getWidth() - fullScreenCheckButton.getWidth() - 2f);
        fullScreenTable.add(fullScreenCheckButton).size(fullScreenCheckButton.getWidth(), fullScreenCheckButton.getHeight());

        //add the full screen table to the bottom table
        bottomTable.add(fullScreenTable).size(fullScreenTable.getWidth() , fullScreenTable.getHeight());


        //create the blood table and set its background to the blood setting background image
        Table bloodTable = new Table();
        bloodTable.setRound(false);
        bloodTable.setBackground(bloodSettingBackgroundImage.getDrawable());
        bloodTable.setSize(bloodSettingBackgroundImage.getWidth(), bloodSettingBackgroundImage.getHeight());

        // add an empty cell for alignment and the blood check button to the full screen table
        bloodTable.add().width(bloodSettingBackgroundImage.getWidth() - bloodCheckButton.getWidth() - 2f);
        bloodTable.add(bloodCheckButton).size(bloodCheckButton.getWidth(), bloodCheckButton.getHeight());

        //add the blood table to the bottom table
        bottomTable.add(bloodTable).size(bloodTable.getWidth() , bloodTable.getHeight());

        //add the bottom table to the main table
        mainTable.add(bottomTable);

    }

    @Override
    public void show() {
        //set the stage as input processor
        Gdx.input.setInputProcessor(stage);

        //set the settings widgets to show the current settings

        //for the music and sounds button
        if (game.settingsManager.isMusicSettingOn()) {
            musicToggleButton.setChecked(true);
        }

        if (game.settingsManager.isSoundsSettingOn()) {
            soundsToggleButton.setChecked(true);
        }

        switch (game.settingsManager.getDifficultySetting()) {
            case EASY:
                mediumImage.setVisible(false);
                hardImage.setVisible(false);
                break;
            case MEDIUM:
                easyImage.setVisible(false);
                hardImage.setVisible(false);
                break;
            default:
                easyImage.setVisible(false);
                mediumImage.setVisible(false);
        }

        // for full screen settings and blood settings
        if (game.settingsManager.isFullScreenSettingOn()) {
            fullScreenCheckButton.setChecked(true);
        }

        if (game.settingsManager.isBloodSettingOn()) {
            bloodCheckButton.setChecked(true);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(GlobalVariables.BLUE_BACKGROUND);

        //tell the stage to do actors and draw itself
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //update the stage's viewport with new screen size
        stage.getViewport().update(width, height , true);
    }

    @Override
    public void pause() {
        //pause music
        game.audioManager.pauseMusic();
    }

    @Override
    public void resume() {
        //resume music if it's enabled
        game.audioManager.playMusic();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispose the stage
        stage.dispose();
    }
}
