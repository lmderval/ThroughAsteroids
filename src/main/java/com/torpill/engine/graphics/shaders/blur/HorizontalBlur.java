package com.torpill.engine.graphics.shaders.blur;

import com.torpill.engine.graphics.post.ImageRenderer;

import static com.torpill.engine.graphics.shaders.blur.HorizontalBlurShader.UNI_TARGET_WIDTH;
import static com.torpill.engine.graphics.shaders.main.MainShader.UNI_TEX_SAMPLER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class HorizontalBlur {
	
	private final ImageRenderer renderer;
	private final HorizontalBlurShader shader;

	private final float targetFboWidth;
	
	public HorizontalBlur(int targetFboWidth, int targetFboHeight) throws Exception {
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
		shader = new HorizontalBlurShader();
		shader.setup();
		this.targetFboWidth = targetFboWidth;
	}
	
	public void render(int texture){
		shader.bind();
		shader.setUniform(UNI_TEX_SAMPLER, 0);
		shader.setUniform(UNI_TARGET_WIDTH, targetFboWidth);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		shader.unbind();
	}
	
	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}
	
	public void cleanup(){
		renderer.cleanup();
		shader.cleanup();
	}

}
