package com.torpill.nuklear;

import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

class NkTest {

    private final String name = "Nuklear Test";
    private final int flags = NK_WINDOW_TITLE | NK_WINDOW_BORDER | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_CLOSABLE;
    private NkRect rect;

    private IntBuffer currentValue = BufferUtils.createIntBuffer(1);

    void layout(NkContext ctx, int x, int y) {
        if (rect == null) {
            rect = NkRect.create();
            nk_rect(x, y, 300, 200, rect);
            nk_begin(ctx, name, rect, flags);
            nk_end(ctx);
        } else if (!nk_window_is_closed(ctx, name)) {
            if (nk_begin(ctx, name, rect, flags)) {
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
