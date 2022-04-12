package com.torpill.engine.graphics.shaders.bloom;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class RedFilterShader extends ShaderProgram {

    public static final String UNI_TEX_SAMPLER = "tex_sampler";

    public RedFilterShader() throws Exception {
        super("simple", "red_filter");
    }


    @Override
    public void setup() throws Exception {
        createVertexShader();
        createFragmentShader();
        link();

        createUniform(UNI_TEX_SAMPLER);
    }
}