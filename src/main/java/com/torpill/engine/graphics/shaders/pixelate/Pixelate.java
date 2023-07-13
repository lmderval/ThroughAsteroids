package com.torpill.engine.graphics.shaders.pixelate;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.post.ImageRenderer;

import static com.torpill.engine.graphics.shaders.pixelate.PixelateShader.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Pixelate {

    private final ImageRenderer renderer;
    private final PixelateShader shader;

    public Pixelate() throws Exception {
        renderer = new ImageRenderer();
        shader = new PixelateShader();
        shader.setup();
    }

    public Pixelate(Window window, int width, int height) throws Exception {
        renderer = new ImageRenderer(window, width, height);
        shader = new PixelateShader();
        shader.setup();
    }

    public void resizeRenderer(int width, int height) {
        renderer.recreateFBO(width, height);
    }

    public void render(int texture, int width, int height) {
        shader.bind();
        shader.setUniform(UNI_TEX_SAMPLER, 0);
        shader.setUniform(UNI_WIDTH, width);
        shader.setUniform(UNI_HEIGHT, height);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.unbind();
    }

    public int getOutputTexture() {
        return renderer.getOutputTexture();
    }

    public void cleanup() {
        renderer.cleanup();
        shader.cleanup();
    }

}
