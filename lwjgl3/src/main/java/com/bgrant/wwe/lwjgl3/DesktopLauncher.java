package com.bgrant.wwe.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bgrant.wwe.SFS;
import com.bgrant.wwe.resources.GlobalVariables;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setTitle("Wrestle Mania");
        configuration.setWindowedMode(GlobalVariables.WINDOW_WIDTH, GlobalVariables.WINDOW_HEIGHT);
        new Lwjgl3Application(new SFS(), configuration);
    }
}
