package com.torpill.engine.graphics;

import com.torpill.engine.Block;
import com.torpill.engine.Entity;
import com.torpill.engine.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private final Transformation transformation = new Transformation();

    private final Matrix4f perspective_mat = new Matrix4f();

    private ShaderProgram main;

    private static final String UNI_PROJECTION = "projection_mat";
    private static final String UNI_WORLD = "world_mat";
    private static final String UNI_TEX_SAMPLER = "tex_sampler";

    public void init() throws Exception {
        setupMain();
    }

    private void setupMain() throws Exception {
        main = new ShaderProgram("main");
        main.createVertexShader();
        main.createFragmentShader();
        main.link();
        main.createUniform(UNI_PROJECTION);
        main.createUniform(UNI_WORLD);
        main.createUniform(UNI_TEX_SAMPLER);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    public void preRender(@NotNull Window window) {
        clear();
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        transformation.setPerspective(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR, perspective_mat);

        main.bind();

        main.setUniform(UNI_PROJECTION, perspective_mat);
        main.setUniform(UNI_TEX_SAMPLER, 0);
    }

    public void renderEntities(@NotNull Entity[] entities, @NotNull Camera camera) {
        for (Entity entity : entities) {
            entity.recalculate(camera.getViewMat());
            main.setUniform(UNI_WORLD, entity.getModelViewMat());
            entity.getMesh().render();
        }
    }

    public void postRender() {
        main.unbind();
    }

    public void cleanup() {
        main.cleanup();
    }
}
