package com.bgrant.wwe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.bgrant.wwe.objects.Fighter;
import com.bgrant.wwe.objects.FighterChoice;
import com.bgrant.wwe.resources.Assets;
import com.bgrant.wwe.resources.AudioManager;
import com.bgrant.wwe.resources.SettingsManager;
import com.bgrant.wwe.screens.GameScreen;
import com.bgrant.wwe.screens.LoadingScreen;
import com.bgrant.wwe.screens.MainMenuScreen;
import com.bgrant.wwe.screens.SettingsScreen;

import java.util.ArrayList;

public class SFS extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public Assets assets;
    public AudioManager audioManager;
    public SettingsManager settingsManager;

    //screens
    public GameScreen gameScreen;
    public MainMenuScreen mainMenuScreen;
    public SettingsScreen settingsScreen;

    //fighters
    public Fighter player, opponent;
    public final ArrayList<FighterChoice> fighterChoiceList = new ArrayList<>();

    public LoadingScreen loadingScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assets = new Assets();

        //initialise the loading screen and switch to it
        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);
    }

    public void assetsLoaded() {

        //initialize the settings manager and load all the settings
        settingsManager = new SettingsManager();
        settingsManager.loadSettings();

        //initialize the audio manager
        audioManager = new AudioManager(assets.manager);

        //update the audio settings in the audio manager

        //enable/disable music
        if (settingsManager.isMusicSettingOn()) {
            audioManager.enableMusic();
        }else {
            audioManager.disableMusic();
        }

        //enable/disable sound
        if (settingsManager.isSoundsSettingOn()) {
            audioManager.enableSound();
        }else {
            audioManager.disableSound();
        }

        //if the full screen setting is on , go to full screen  //easy way to default this as full screen is put a (!) in front of settingsManager in the if statement
        if (settingsManager.isFullScreenSettingOn()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
//        if you want to set the default as full screen and settings to window screen add this else in the if
//        else {
//            Gdx.graphics.setWindowedMode(// add the height and width of the window)
//        }

        //load the fighter choice list
        loadFighterChoiceList();

        //initialize the fighters
        player = new Fighter(this, fighterChoiceList.get(0).getName(), fighterChoiceList.get(0).getColor());
        opponent = new Fighter(this, fighterChoiceList.get(1).getName(), fighterChoiceList.get(1).getColor());

        //initialize the game screen
        gameScreen = new GameScreen(this);

        //initialize the settings screen
        settingsScreen = new SettingsScreen(this);

        //initialize  the main menu screen and switch to it
        mainMenuScreen = new MainMenuScreen(this);
        setScreen(mainMenuScreen);
    }

    private void loadFighterChoiceList() {
        //load the fighter choice list from assets
        Json json = new Json();
        JsonValue fighterChoices = new JsonReader().parse(Gdx.files.internal("data/fighter_choices.json"));

        for (int i = 0; i < fighterChoices.size; i++) {
            fighterChoiceList.add(json.fromJson(FighterChoice.class , String.valueOf(fighterChoices.get(i))));
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        shapeRenderer.dispose();
    }

}
