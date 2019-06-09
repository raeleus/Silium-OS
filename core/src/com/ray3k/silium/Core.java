package com.ray3k.silium;

import com.badlogic.gdx.Game;

public class Core extends Game {
    public final static String VERSION = "1.0";
    @Override
    public void create () {
        setScreen(new GameScreen());
    }
}
