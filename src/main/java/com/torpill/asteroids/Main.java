package com.torpill.asteroids;

import com.torpill.engine.GameEngine;

public class Main {

    private static final ThroughAsteroids INSTANCE = new ThroughAsteroids();

    public static ThroughAsteroids getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        int height = 1080;
        int width = height * 16 / 9;
        GameEngine engine = new GameEngine("Through Asteroids", width, height, false, INSTANCE, "fonts/pocket.ttf");
        engine.run();
    }
}
