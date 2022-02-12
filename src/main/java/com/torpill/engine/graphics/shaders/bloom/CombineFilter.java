package com.torpill.engine.graphics.shaders.bloom;

import com.torpill.engine.graphics.post.ImageRenderer;

import static com.torpill.engine.graphics.shaders.bloom.CombineShader.UNI_COLOR_TEX_SAMPLER;
import static com.torpill.engine.graphics.shaders.bloom.CombineShader.UNI_HIGHLIGHT_TEX_SAMPLER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class CombineFilter {

    private final ImageRenderer renderer;
    private final CombineShader shader;

    public CombineFilter() throws Exception {
        renderer = new ImageRenderer();
        shader = new CombineShader();
        shader.setup();
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

    public void cleanup() {
        renderer.cleanup();
        shader.cleanup();
    }

}
