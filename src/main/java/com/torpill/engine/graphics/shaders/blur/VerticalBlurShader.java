package com.torpill.engine.graphics.shaders.blur;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class VerticalBlurShader extends ShaderProgram {

    public static final String UNI_TARGET_HEIGHT = "target_height";
    public static final String UNI_TEX_SAMPLER = "tex_sampler";

    protected VerticalBlurShader() throws Exception {
        super("vertical_blur", "blur");
    }

    @Override
    public void setup() throws Exception {
        createVertexShader();
        createFragmentShader();
        link();

        createUniform(UNI_TARGET_HEIGHT);
        createUniform(UNI_TEX_SAMPLER);
    }
}
