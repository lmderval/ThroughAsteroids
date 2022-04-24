package com.torpill.asteroids.gui.pause.game;

import com.torpill.engine.Window;
import com.torpill.engine.gui.Nuklear;
import com.torpill.engine.gui.NuklearScene;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;

public class NkGamePauseScene implements NuklearScene {

    private final GamePauseGui pause = new GamePauseGui();

    @Override
    public void update(@NotNull Window window, @NotNull Nuklear nk) {
        NkContext ctx = nk.getContext();

        NkColor white = NkColor.create().set(
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF
        );
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
        NkColor normal = NkColor.create().set(
                (byte) 0x32,
                (byte) 0x32,
                (byte) 0x32,
                (byte) 0xFF
        );
        NkColor hover = NkColor.create().set(
                (byte) 0x28,
                (byte) 0x28,
                (byte) 0x28,
                (byte) 0xFF
        );
        NkColor active = NkColor.create().set(
                (byte) 0x23,
                (byte) 0x23,
                (byte) 0x23,
                (byte) 0xFF
        );
        NkColor cyan = NkColor.create().set(
                (byte) 0x00,
                (byte) 0xA5,
                (byte) 0xA5,
                (byte) 0xFF
        );

        ctx.style().window().fixed_background().data().color().set(grey);
        ctx.style().window().header().label_active().set(white);
        ctx.style().window().header().active().data().color().set(cyan);
        ctx.style().window().header().label_padding().set(15f, 8f);
        ctx.style().window().header().padding().set(0f, 8f);
        ctx.style().window().border(4f);
        ctx.style().window().border_color(dark);
        ctx.style().window().padding().set(20f, 35f);
        ctx.style().window().spacing().set(10f, 15f);

        ctx.style().button().border(4f);
        ctx.style().button().text_normal().set(white);
        ctx.style().button().text_hover().set(white);
        ctx.style().button().text_active().set(white);
        ctx.style().button().normal().data().color().set(normal);
        ctx.style().button().hover().data().color().set(hover);
        ctx.style().button().active().data().color().set(active);
        ctx.style().button().border_color(dark);
        ctx.style().button().rounding(0f);

        int x = window.getFramebufferWidth() / 2 - pause.getWidth() / 2;
        int y = window.getFramebufferHeight() / 2 - pause.getHeight() / 2;
        pause.layout(ctx, x, y);
    }
}
