package com.torpill.engine.graphics.shaders.bloom;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class BrightFilterShader extends ShaderProgram {
	
	public static final String UNI_TEX_SAMPLER = "tex_sampler";
	
	public BrightFilterShader() throws Exception {
		super("simple", "bright_filter");
	}


	@Override
	public void setup() throws Exception {
		createVertexShader();
		createFragmentShader();
		link();

		createUniform(UNI_TEX_SAMPLER);
	}
}
