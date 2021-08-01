package com.torpill.engine;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d prev_pos = new Vector2d(-1, -1);
    private final Vector2d curr_pos = new Vector2d();
    private final Vector2f display_vec = new Vector2f();

    private boolean in_window = false;

    private boolean left_pressed = false;
    private boolean right_pressed = false;

    public void init(@NotNull Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, x_pos, y_pos) -> {
            curr_pos.x = x_pos;
            curr_pos.y = y_pos;
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            in_window = entered;
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            left_pressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            right_pressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
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
}