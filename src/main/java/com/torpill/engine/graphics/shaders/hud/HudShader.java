package com.torpill.engine.graphics.shaders.hud;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class HudShader extends ShaderProgram {

    public static final String UNI_PROJECTION = "projection_mat";
    public static final String UNI_MODEL = "m_mat";
    public static final String UNI_IS_3D = "is_3d";
    public static final String UNI_TEX_SAMPLER = "tex_sampler";
    public static final String UNI_COLOR = "color";
    public static final String UNI_LIGHT_DIRECTION = "light_direction";

    public HudShader() throws Exception {
        super("hud");
    }

    @Override
    public void setup() throws Exception {
        createVertexShader();
        createFragmentShader();
        link();

        createUniform(UNI_PROJECTION);
        createUniform(UNI_MODEL);
        createUniform(UNI_IS_3D);
        createUniform(UNI_TEX_SAMPLER);
        createUniform(UNI_COLOR);
        createUniform(UNI_LIGHT_DIRECTION);
    }
}
