package com.torpill.asteroids;

import com.torpill.engine.GameEngine;
import com.torpill.engine.IGameLogic;

public class Main {

    public static void main(String[] args) {
        IGameLogic gameLogic = new ThroughAsteroids();
        GameEngine engine = new GameEngine("Through Asteroids", 1280, 720, false, gameLogic);
        engine.run();
    }
}
