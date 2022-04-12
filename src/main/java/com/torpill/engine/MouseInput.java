package com.torpill.engine;

import com.torpill.engine.gui.Nuklear;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.nuklear.Nuklear.nk_input_button;
import static org.lwjgl.system.MemoryStack.stackPush;

public class MouseInput {

    private final Vector2d prev_pos = new Vector2d(-1, -1);
    private final Vector2d curr_pos = new Vector2d();
    private final Vector2f display_vec = new Vector2f();

    private boolean in_window = false;

    private boolean left_pressed = false;
    private boolean right_pressed = false;

    private int scroll = 0;

    public void init(@NotNull Window window, @NotNull Nuklear nk) {
        NkContext ctx = nk.getContext();
        glfwSetScrollCallback(window.getWindowHandle(), (windowHandle, xoffset, yoffset) -> {
            try (MemoryStack stack = stackPush()) {
                NkVec2 scroll = NkVec2.mallocStack(stack)
                        .x((float) xoffset)
                        .y((float) yoffset);
                nk_input_scroll(ctx, scroll);
            }
        });
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, x_pos, y_pos) -> {
            curr_pos.x = x_pos;
            curr_pos.y = y_pos;
            nk_input_motion(ctx, (int) x_pos, (int) y_pos);
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> in_window = entered);
        glfwSetScrollCallback(window.getWindowHandle(), (windowHandle, x_offset, y_offset) -> {
            scroll += y_offset;
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            boolean pressed = action == GLFW_PRESS;
            left_pressed = button == GLFW_MOUSE_BUTTON_LEFT && pressed;
            right_pressed = button == GLFW_MOUSE_BUTTON_RIGHT && pressed;
            try (MemoryStack stack = stackPush()) {
                DoubleBuffer cx = stack.mallocDouble(1);
                DoubleBuffer cy = stack.mallocDouble(1);

                glfwGetCursorPos(windowHandle, cx, cy);

                int x = (int) cx.get(0);
                int y = (int) cy.get(0);

                int nkButton;
                switch (button) {
                    case GLFW_MOUSE_BUTTON_RIGHT:
                        nkButton = NK_BUTTON_RIGHT;
                        break;
                    case GLFW_MOUSE_BUTTON_MIDDLE:
                        nkButton = NK_BUTTON_MIDDLE;
                        break;
                    default:
                        nkButton = NK_BUTTON_LEFT;
                }
                nk_input_button(ctx, nkButton, x, y, pressed);
            }
        });
    }

    public Vector2f getDisplayVec() {
        return display_vec;
    }

    public void input(@NotNull Window window) {
        display_vec.x = 0;
        display_vec.y = 0;
        if (in_window) {
            double dx = curr_pos.x - prev_pos.x;
            double dy = curr_pos.y - prev_pos.y;
            boolean rx = dx != 0;
            boolean ry = dy != 0;
            if (rx) {
                display_vec.y = (float) dx;
            }
            if (ry) {
                display_vec.x = (float) dy;
            }
        }
        prev_pos.x = curr_pos.x;
        prev_pos.y = curr_pos.y;
    }

    public boolean isLeftButtonPressed() {
        return left_pressed;
    }

    public boolean isRightButtonPressed() {
        return right_pressed;
    }

    public int getScroll() {
        return scroll;
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
    }
}