package com.torpill.engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    // The window handle
    private long window;

    private String title;
    private int width;
    private int height;
    private boolean vsync;

    private boolean resized = false;

    public Window(String title, int width, int height, boolean vsync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vsync = vsync;
    }

    public void init() {
        // Setup an error callback. The default implementation
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
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

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
            Window.this.width = width;
            Window.this.height = height;
            Window.this.setResized(true);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        if (vsync) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
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

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
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
}
