package com.torpill.engine.graphics.shaders.main;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.post.ImageRenderer;
import org.jetbrains.annotations.NotNull;

import static com.torpill.engine.graphics.shaders.bloom.CombineShader.UNI_COLOR_TEX_SAMPLER;
import static com.torpill.engine.graphics.shaders.bloom.CombineShader.UNI_HIGHLIGHT_TEX_SAMPLER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class SuperposeFilter {

    private final ImageRenderer renderer;
    private final SuperposeShader shader;

    public SuperposeFilter() throws Exception {
        renderer = new ImageRenderer();
        shader = new SuperposeShader();
        shader.setup();
    }

    public SuperposeFilter(@NotNull Window window, int width, int height) throws Exception {
        renderer = new ImageRenderer(window, width, height);
        shader = new SuperposeShader();
        shader.setup();
    }

    public void resizeRenderer(int width, int height) {
        renderer.recreateFBO(width, height);
    }

    public void render(int colourTexture, int highlightTexture) {
        shader.bind();
        shader.setUniform(UNI_COLOR_TEX_SAMPLER, 0);
        shader.setUniform(UNI_HIGHLIGHT_TEX_SAMPLER, 1);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, colourTexture);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, highlightTexture);
        renderer.renderQuad();
        shader.unbind();
    }

    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public void cleanup() {
        renderer.cleanup();
        shader.cleanup();
    }

}
