package com.torpill.engine;

import com.torpill.engine.gui.Nuklear;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.nk_input_unicode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    // The window handle
    private long window;

    private final String title;
    private int framebufferWidth, framebufferHeight;
    private int width, height;
    private final boolean vsync;

    private boolean resized = false;

    public Window(String title, int width, int height, boolean vsync) {
        this.title = title;
        framebufferWidth = width;
        framebufferHeight = height;
        this.vsync = vsync;
    }

    public void init(@NotNull Nuklear nk) throws Exception {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(framebufferWidth, framebufferHeight, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        if (vsync) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        NkContext ctx = nk.init(window);


        glfwSetCharCallback(window, (window, codepoint) -> nk_input_unicode(ctx, codepoint));

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidmode != null;
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Setup resize callback
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            framebufferWidth = width;
            framebufferHeight = height;
            Window.this.setResized(true);
        });

        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });

        // Make the window visible
        glfwShowWindow(window);
        glCullFace(GL_BACK);

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    public void update() {
        glfwSwapBuffers(window); // swap the color buffers
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    public void cleanup() {
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public boolean vsync() {
        return vsync;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isResized() {
        return resized;
    }

    public int getFramebufferWidth() {
        return framebufferWidth;
    }

    public int getFramebufferHeight() {
        return framebufferHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void showCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public void hideCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    public void disableCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public long getWindowHandle() {
        return window;
    }

    public void setClearColor(float red, float green, float blue, float alpha) {
        glClearColor(red, green, blue, alpha);
    }

    public void setClearColor(int red, int green, int blue, int alpha) {
        glClearColor(red / 255f, green / 255f, blue / 255f, alpha / 255f);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void setShouldClose(boolean value) {
        glfwSetWindowShouldClose(window, value);
    }

    public void setInputMode(int mode, int value) {
        glfwSetInputMode(window, mode, value);
    }

    public void setCursorPos(float x, float y) {
        glfwSetCursorPos(window, x, y);
    }
}
