package com.torpill.engine.hud;

import com.torpill.engine.Window;
import org.jetbrains.annotations.NotNull;

public interface IHud {

    void load();

    void resize(@NotNull Window window);

    HudItem[] getItems();

    default void cleanup() {
        for (HudItem item : getItems()) {
            item.getMesh().cleanup();
        }
    }
}