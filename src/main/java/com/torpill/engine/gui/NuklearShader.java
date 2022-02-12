package com.torpill.engine.gui;

import com.torpill.engine.graphics.ShaderProgram;
import org.jetbrains.annotations.NotNull;

public class NuklearShader extends ShaderProgram {

    public static final String UNI_PROJECTION = "projection_mat";
    public static final String UNI_TEX_SAMPLER = "tex_sampler";

    public NuklearShader() throws Exception {
        super("nuklear");
    }

    @Override
    public void setup() throws Exception {
        createVertexShader();
        createFragmentShader();
        link();

        createUniform(UNI_PROJECTION);
        createUniform(UNI_TEX_SAMPLER);
    }
}
