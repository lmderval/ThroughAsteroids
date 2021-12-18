package com.torpill.asteroids.gui.pause;

import com.torpill.engine.Window;
import com.torpill.engine.gui.Nuklear;
import com.torpill.engine.gui.NuklearScene;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.nuklear.*;

public class NkPauseScene implements NuklearScene {

    private final PauseGui pause = new PauseGui();

    @Override
    public void update(@NotNull Window window, @NotNull Nuklear nk) {
        NkContext ctx = nk.getContext();

        NkColor grey = NkColor.create().set(
                (byte) 0x48,
                (byte) 0x48,
                (byte) 0x48,
                (byte) 0xFF
        );
        NkColor dark = NkColor.create().set(
                (byte) 0x12,
                (byte) 0x12,
                (byte) 0x12,
                (byte) 0xFF
        );
        NkColor cyan = NkColor.create().set(
                (byte) 0x00,
                (byte) 0xA5,
                (byte) 0xA5,
                (byte) 0xFF
        );

        ctx.style().window().fixed_background().data().color().set(grey);
        ctx.style().window().header().active().data().color().set(cyan);
        ctx.style().window().header().label_padding().set(15f, 8f);
        ctx.style().window().header().padding().set(0f, 8f);
        ctx.style().window().border(2f);
        ctx.style().window().border_color(dark);
        ctx.style().window().padding().set(20f, 35f);
        ctx.style().window().spacing().set(10f, 15f);

        ctx.style().button().border(2f);
        ctx.style().button().border_color(dark);
        ctx.style().button().rounding(2f);

        int x = window.getFramebufferWidth() / 2 - pause.getWidth() / 2;
        int y = window.getFramebufferHeight() / 2 - pause.getHeight() / 2;
        pause.layout(ctx, x, y);
    }
}
