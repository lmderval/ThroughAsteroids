package com.torpill.engine.graphics.shaders.main;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class SuperposeShader extends ShaderProgram {

	public static final String UNI_COLOR_TEX_SAMPLER = "color_tex_sampler";
	public static final String UNI_HIGHLIGHT_TEX_SAMPLER = "highlight_tex_sampler";
	
	public SuperposeShader() throws Exception {
		super("simple", "superpose");
	}

	@Override
	public void setup() throws Exception {
		createVertexShader();
		createFragmentShader();
		link();

		createUniform(UNI_COLOR_TEX_SAMPLER);
		createUniform(UNI_HIGHLIGHT_TEX_SAMPLER);
	}
}
