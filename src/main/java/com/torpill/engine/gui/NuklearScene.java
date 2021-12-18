package com.torpill.engine.gui;

import com.torpill.engine.Window;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.nuklear.NkContext;

public interface NuklearScene {

    void update(@NotNull Window window, @NotNull Nuklear nk);

    abstract class Gui {

        protected final String name;

        protected Gui(String name) {
            this.name = name;
        }

        public abstract void layout(NkContext ctx, int x, int y);
    }
}
