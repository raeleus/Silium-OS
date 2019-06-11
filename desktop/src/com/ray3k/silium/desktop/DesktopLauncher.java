package com.ray3k.silium.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ray3k.silium.Core;
import com.ray3k.silium.CrossPlatformWorker;

public class DesktopLauncher implements CrossPlatformWorker {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 576;
        Core core = new Core();
        core.crossPlatformWorker = new DesktopLauncher();
        new LwjglApplication(core, config);
    }
}
