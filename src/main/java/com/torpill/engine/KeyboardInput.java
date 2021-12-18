package com.torpill.engine;

import com.torpill.engine.gui.Nuklear;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.nuklear.NkContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;

public class KeyboardInput {

    private static final int N_KEYS = 512;

    private final boolean[] keyPressed = new boolean[N_KEYS];
    private final boolean[] keyUsed = new boolean[N_KEYS];
    private final boolean[] keyReleased = new boolean[N_KEYS];

    public void init(@NotNull Window window, @NotNull Nuklear nk) {
        for (int i = 0; i < N_KEYS; i ++)
            keyReleased[i] = keyUsed[i] = true;
        NkContext ctx = nk.getContext();
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window.getWindowHandle(), (windowHandle, key, scancode, action, mods) -> {
            boolean press = action == GLFW_PRESS;
            boolean release = action == GLFW_RELEASE;
            boolean hold = action == GLFW_REPEAT;
            keyPressed[key] = press || hold;
            keyReleased[key] = release;
            if (press)
                keyUsed[key] = false;
            if (release)
                keyUsed[key] = true;
//            System.out.println((press || hold) + " " + release + " " + keyUsed[key]);
            switch (key) {
                case GLFW_KEY_ESCAPE:
                    if (action == GLFW_RELEASE && mods == GLFW_MOD_ALT)
                        glfwSetWindowShouldClose(windowHandle, true);
                    break;
                case GLFW_KEY_DELETE:
                    nk_input_key(ctx, NK_KEY_DEL, press);
                    break;
                case GLFW_KEY_ENTER:
                    nk_input_key(ctx, NK_KEY_ENTER, press);
                    break;
                case GLFW_KEY_TAB:
                    nk_input_key(ctx, NK_KEY_TAB, press);
                    break;
                case GLFW_KEY_BACKSPACE:
                    nk_input_key(ctx, NK_KEY_BACKSPACE, press);
                    break;
                case GLFW_KEY_UP:
                    nk_input_key(ctx, NK_KEY_UP, press);
                    break;
                case GLFW_KEY_DOWN:
                    nk_input_key(ctx, NK_KEY_DOWN, press);
                    break;
                case GLFW_KEY_HOME:
                    nk_input_key(ctx, NK_KEY_TEXT_START, press);
                    nk_input_key(ctx, NK_KEY_SCROLL_START, press);
                    break;
                case GLFW_KEY_END:
                    nk_input_key(ctx, NK_KEY_TEXT_END, press);
                    nk_input_key(ctx, NK_KEY_SCROLL_END, press);
                    break;
                case GLFW_KEY_PAGE_DOWN:
                    nk_input_key(ctx, NK_KEY_SCROLL_DOWN, press);
                    break;
                case GLFW_KEY_PAGE_UP:
                    nk_input_key(ctx, NK_KEY_SCROLL_UP, press);
                    break;
                case GLFW_KEY_LEFT_SHIFT:
                case GLFW_KEY_RIGHT_SHIFT:
                    nk_input_key(ctx, NK_KEY_SHIFT, press);
                    break;
                case GLFW_KEY_LEFT_CONTROL:
                case GLFW_KEY_RIGHT_CONTROL:
                    if (press) {
                        nk_input_key(ctx, NK_KEY_COPY, glfwGetKey(windowHandle, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_PASTE, glfwGetKey(windowHandle, GLFW_KEY_P) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_CUT, glfwGetKey(windowHandle, GLFW_KEY_X) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_TEXT_UNDO, glfwGetKey(windowHandle, GLFW_KEY_Z) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_TEXT_REDO, glfwGetKey(windowHandle, GLFW_KEY_R) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(windowHandle, GLFW_KEY_LEFT) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(windowHandle, GLFW_KEY_RIGHT) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_TEXT_LINE_START, glfwGetKey(windowHandle, GLFW_KEY_B) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_TEXT_LINE_END, glfwGetKey(windowHandle, GLFW_KEY_E) == GLFW_PRESS);
                    } else {
                        nk_input_key(ctx, NK_KEY_LEFT, glfwGetKey(windowHandle, GLFW_KEY_LEFT) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_RIGHT, glfwGetKey(windowHandle, GLFW_KEY_RIGHT) == GLFW_PRESS);
                        nk_input_key(ctx, NK_KEY_COPY, false);
                        nk_input_key(ctx, NK_KEY_PASTE, false);
                        nk_input_key(ctx, NK_KEY_CUT, false);
                        nk_input_key(ctx, NK_KEY_SHIFT, false);
                    }
                    break;
            }
        });
    }

    public boolean isPressed(int keycode) {
        return keyPressed[keycode];
    }

    public boolean use(int keycode) {
        if (!keyUsed[keycode])
            return keyUsed[keycode] = true;
        else
            return false;
    }

    public boolean isReleased(int keycode) {
        return keyReleased[keycode];
    }
}
