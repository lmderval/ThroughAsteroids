package com.torpill.engine.graphics.shaders.bloom;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

    public static final String UNI_COLOR_TEX_SAMPLER = "color_tex_sampler";
    public static final String UNI_HIGHLIGHT_TEX_SAMPLER = "highlight_tex_sampler";

    public CombineShader() throws Exception {
        super("simple", "combine");
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
