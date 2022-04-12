package com.torpill.engine.graphics.shaders.pixelate;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class PixelateShader extends ShaderProgram {

	public static final String UNI_WIDTH = "width";
	public static final String UNI_HEIGHT = "height";
	public static final String UNI_TEX_SAMPLER = "tex_sampler";
	
	protected PixelateShader() throws Exception {
		super("simple", "pixelate");
	}

	@Override
	public void setup() throws Exception {
		createVertexShader();
		createFragmentShader();
		link();

		createUniform(UNI_WIDTH);
		createUniform(UNI_HEIGHT);
		createUniform(UNI_TEX_SAMPLER);
	}
}
