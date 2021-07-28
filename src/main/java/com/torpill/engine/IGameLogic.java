package com.torpill.engine;

import org.jetbrains.annotations.NotNull;

public interface IGameLogic {

    void init() throws Exception;

    void input(@NotNull Window window, @NotNull MouseInput mouse_input);

    void update(float interval, @NotNull MouseInput mouse_input);

    void render(@NotNull Window window);

    void cleanup();
}
