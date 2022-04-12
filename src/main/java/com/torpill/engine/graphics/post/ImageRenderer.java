package com.torpill.engine.graphics.post;

import com.torpill.engine.Window;
import org.jetbrains.annotations.NotNull;

import static com.torpill.engine.graphics.post.FBO.NONE;
import static org.lwjgl.opengl.GL11.*;

public class ImageRenderer {

	private FBO fbo;

	public ImageRenderer(@NotNull Window window, int width, int height) {
		fbo = new FBO(window, width, height, NONE);
	}

	public ImageRenderer() {
	}

	public void recreateFBO(int width, int height) {
		fbo.recreate(width, height, NONE);
	}

	public void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		glClear(GL_COLOR_BUFFER_BIT);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	public int getOutputTexture() {
		return fbo.getColourTexture();
	}

	public void cleanup() {
		if (fbo != null) {
			fbo.cleanup();
		}
	}
}
