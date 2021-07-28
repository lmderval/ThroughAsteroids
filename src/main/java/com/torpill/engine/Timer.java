package com.torpill.engine;

import static java.lang.System.nanoTime;

class Timer {

    private double lastLoopTime = 0.0;

    public void init() {
        lastLoopTime = getTime();
    }

    public double getTime(){
        return nanoTime() / 1_000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    public double getLastLoopTime() {
        return lastLoopTime;
    }
}