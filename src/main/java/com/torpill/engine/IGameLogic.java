package com.torpill.engine;

import com.torpill.engine.gui.Nuklear;
import org.jetbrains.annotations.NotNull;

public interface IGameLogic {

    void init(@NotNull Window window) throws Exception;

    void input(@NotNull Window window, @NotNull MouseInput mouse_input, @NotNull KeyboardInput keyboard_input);

    void update(float interval, @NotNull Window window, @NotNull MouseInput mouse_input, @NotNull KeyboardInput keyboard_input);

    void updateGui(@NotNull Window window, @NotNull Nuklear nk);

    void render(@NotNull Window window);

    void cleanup();
}
