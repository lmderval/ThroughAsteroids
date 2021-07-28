package com.torpill.engine;

import org.jetbrains.annotations.NotNull;

import static java.lang.Thread.sleep;

public class GameEngine {

    private static final int TARGET_UPS = 30;
    private static final int TARGET_FPS = 60;

    private final Window window;
    private final MouseInput mouse_input = new MouseInput();
    private final IGameLogic game_logic;

    private final Timer timer = new Timer();

    public GameEngine(@NotNull String windowTitle, int width, int height, boolean vsync, @NotNull IGameLogic game_logic) {
        window = new Window(windowTitle, width, height, vsync);
        this.game_logic = game_logic;
    }

    public void run() {
        try {
            init();
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void init() throws Exception {
        timer.init();
        window.init();
        mouse_input.init(window);
        game_logic.init();
    }

    private void loop() {
        float elapsedTime;
        float accumulator = 0.0f;
        float interval = 1f / TARGET_UPS;
        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;
            input();
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }
            render();
            if (!window.vsync())
                sync();
        }
    }

    private void input() {
        mouse_input.input(window);
        game_logic.input(window, mouse_input);
    }

    private void update(float interval) {
        game_logic.update(interval, mouse_input);
    }

    private void render() {
        game_logic.render(window);
        window.update();
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() - loopSlot;
        while (timer.getTime() < endTime) {
            try {
                sleep(1L);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void cleanup() {
        game_logic.cleanup();
        window.cleanup();
    }
}