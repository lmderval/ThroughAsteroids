package com.torpill.engine;

import com.torpill.engine.gui.Nuklear;
import com.torpill.engine.loader.MeshCache;
import com.torpill.engine.loader.TextureCache;
import com.torpill.engine.world.Block;
import org.jetbrains.annotations.NotNull;

import static java.lang.Thread.sleep;

public class GameEngine {

    private static final int TARGET_UPS = 30;
    private static final int TARGET_FPS = 120;

    private final Window window;
    private final MouseInput mouse_input = new MouseInput();
    private final IGameLogic game_logic;
    private final Nuklear nk;

    private final Timer timer = new Timer();

    public GameEngine(@NotNull String windowTitle, int width, int height, boolean vsync, @NotNull IGameLogic game_logic, @NotNull String nkFont) {
        window = new Window(windowTitle, width, height, vsync);
        this.game_logic = game_logic;
        nk = new Nuklear(nkFont);
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
        window.init(nk);
        nk.setupContext();
        mouse_input.init(window, nk);
        Block.loadMesh();
        game_logic.init();
    }

    private void loop() {
        float elapsedTime;
        float accumulator = 0.0f;
        float interval = 1f / TARGET_UPS;
        int frames = 0;
        double fps_counter = timer.getTime();
        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;
            input();
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }
            render();
            frames ++;
            double diff = timer.getTime() - fps_counter;
            if (diff > 1.0) {
                System.out.println((int) (frames / diff) + " FPS");
                frames = 0;
                fps_counter = timer.getTime();
            }
            if (!window.vsync())
                sync();
        }
    }

    private void input() {
        mouse_input.input(window);
        game_logic.input(window, mouse_input);
    }

    private void update(float interval) {
        game_logic.update(interval, window, mouse_input);
    }

    private void render() {
        game_logic.render(window);
        nk.input(window);
        game_logic.updateGui(window, nk);
        nk.render();
        window.update();
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime);
    }

    private void cleanup() {
        nk.cleanup();
        game_logic.cleanup();
        TextureCache.getInstance().cleanup();
        MeshCache.getInstance().cleanup();
        window.cleanup();
    }
}
