package com.torpill.asteroids.gui.pause.edit;

import com.torpill.asteroids.Main;
import com.torpill.engine.gui.NuklearScene;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class EditPauseGui extends NuklearScene.Gui {

    private final int width = 600;
    private final int height = 400;

    public EditPauseGui() {
        super("Pause");
    }

    @Override
    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            NkVec2 vec2 = NkVec2.mallocStack(stack);
            if (nk_begin(
                    ctx,
                    name,
                    nk_rect(x, y, width, height, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR
            )) {
                vec2.x(x);
                vec2.y(y);
                nk_window_set_position(ctx, name, vec2);

                nk_layout_row_dynamic(ctx, 50, 1);
                if (nk_button_label(ctx, "Resume")) {
                    Main.getInstance().resume();
                }

                nk_layout_row_dynamic(ctx, 50, 1);
                if (nk_button_label(ctx, "Play")) {
                    Main.getInstance().play(false);
                }

                nk_layout_row_dynamic(ctx, 50, 1);
                if (nk_button_label(ctx, "Save")) {
                    Main.getInstance().save();
                }

                nk_layout_row_dynamic(ctx, 50, 1);
                if (nk_button_label(ctx, "Quit")) {
                    Main.getInstance().quit();
                }
            }
            nk_end(ctx);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
