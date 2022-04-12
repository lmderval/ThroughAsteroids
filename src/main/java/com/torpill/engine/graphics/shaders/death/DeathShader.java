package com.torpill.engine.graphics.shaders.death;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class DeathShader extends ShaderProgram {

    public static final String UNI_TEX_SAMPLER = "tex_sampler";

    protected DeathShader() throws Exception {
        super("simple", "death");
    }

    @Override
    public void setup() throws Exception {
        createVertexShader();
        createFragmentShader();
        link();

        createUniform(UNI_TEX_SAMPLER);
    }
}
