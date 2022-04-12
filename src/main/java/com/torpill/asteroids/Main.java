package com.torpill.asteroids;

import com.torpill.engine.GameEngine;

public class Main {

    private static final ThroughAsteroids INSTANCE = new ThroughAsteroids();

    public static ThroughAsteroids getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        GameEngine engine = new GameEngine("Through Asteroids", 1920, 1080, false, INSTANCE, "fonts/pocket.ttf");
        engine.run();
    }
}
