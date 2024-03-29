package com.torpill.engine.graphics.shaders.blur;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.post.ImageRenderer;
import org.jetbrains.annotations.NotNull;

import static com.torpill.engine.graphics.shaders.blur.VerticalBlurShader.UNI_TARGET_HEIGHT;
import static com.torpill.engine.graphics.shaders.main.MainShader.UNI_TEX_SAMPLER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class VerticalBlur {

    private final ImageRenderer renderer;
    private final VerticalBlurShader shader;

    private final float targetFboHeight;

    public VerticalBlur(@NotNull Window window, int width, int height) throws Exception {
        renderer = new ImageRenderer(window, width, height);
        shader = new VerticalBlurShader();
        shader.setup();
        this.targetFboHeight = height;
    }

    public void resizeRenderer(int width, int height) {
        renderer.recreateFBO(width, height);
    }

    public void render(int texture) {
        shader.bind();
        shader.setUniform(UNI_TEX_SAMPLER, 0);
        shader.setUniform(UNI_TARGET_HEIGHT, targetFboHeight);
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
