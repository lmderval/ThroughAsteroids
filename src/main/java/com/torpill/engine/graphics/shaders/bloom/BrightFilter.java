package com.torpill.engine.graphics.shaders.bloom;

import com.torpill.engine.graphics.post.ImageRenderer;

import static com.torpill.engine.graphics.shaders.bloom.BrightFilterShader.UNI_TEX_SAMPLER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class BrightFilter {

    private final ImageRenderer renderer;
    private final BrightFilterShader shader;

    public BrightFilter(int width, int height) throws Exception {
        renderer = new ImageRenderer(width, height);
        shader = new BrightFilterShader();
        shader.setup();
    }

    public void render(int texture) {
        shader.bind();
		shader.setUniform(UNI_TEX_SAMPLER, 0);
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
