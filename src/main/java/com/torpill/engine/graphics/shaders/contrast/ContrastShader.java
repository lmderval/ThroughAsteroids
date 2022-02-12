package com.torpill.engine.graphics.shaders.contrast;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	public static final String UNI_TEX_SAMPLER = "tex_sampler";

	public ContrastShader() throws Exception {
		super("contrast");
	}

	@Override
	public void setup() throws Exception {
		createVertexShader();
		createFragmentShader();
		link();

		createUniform(UNI_TEX_SAMPLER);
	}
}
