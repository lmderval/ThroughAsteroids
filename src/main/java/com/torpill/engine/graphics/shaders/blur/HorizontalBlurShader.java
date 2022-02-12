package com.torpill.engine.graphics.shaders.blur;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class HorizontalBlurShader extends ShaderProgram {

	public static final String UNI_TARGET_WIDTH = "target_width";
	public static final String UNI_TEX_SAMPLER = "tex_sampler";
	
	protected HorizontalBlurShader() throws Exception {
		super("horizontal_blur", "blur");
	}

	@Override
	public void setup() throws Exception {
		createVertexShader();
		createFragmentShader();
		link();

		createUniform(UNI_TARGET_WIDTH);
		createUniform(UNI_TEX_SAMPLER);
	}
}
