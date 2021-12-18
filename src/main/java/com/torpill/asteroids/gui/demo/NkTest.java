package com.torpill.asteroids.gui.demo;

import com.torpill.engine.gui.NuklearScene;
import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class NkTest extends NuklearScene.Gui {

    private final IntBuffer currentValue = BufferUtils.createIntBuffer(1);

    public NkTest() {
        super("Nuklear Test");
    }

    @Override
    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            if (nk_begin(
                    ctx,
                    name,
                    nk_rect(x, y, 300, 200, rect),
                    NK_WINDOW_TITLE | NK_WINDOW_BORDER | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE
            )) {
                nk_layout_row_dynamic(ctx, 32, 1);
                nk_label(ctx, "Slider", NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_BOTTOM);

                int min = 0;
                int max = 20;
                int step = 1;
                nk_layout_row_dynamic(ctx, 18, 1);
                nk_slider_int(ctx, min, currentValue, max, step);

                nk_layout_row_dynamic(ctx, 20, 3);
                nk_label(ctx, String.valueOf(min), NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_TOP);
                nk_label(ctx, String.valueOf(currentValue.get(0)), NK_TEXT_ALIGN_CENTERED | NK_TEXT_ALIGN_TOP);
                nk_label(ctx, String.valueOf(max), NK_TEXT_ALIGN_RIGHT | NK_TEXT_ALIGN_TOP);
            }
            nk_end(ctx);
        }
    }
}
