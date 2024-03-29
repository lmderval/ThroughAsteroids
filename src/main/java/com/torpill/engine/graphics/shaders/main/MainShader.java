package com.torpill.engine.graphics.shaders.main;

import com.torpill.engine.graphics.shaders.ShaderProgram;

public class MainShader extends ShaderProgram {

    public static final float SPECULAR_POWER = 10f;

    public static final String UNI_PROJECTION = "projection_mat";
    public static final String UNI_MODEL_VIEW = "mv_mat";
    public static final String UNI_TEX_SAMPLER = "tex_sampler";
    public static final String UNI_LIGHT_MAP_SAMPLER = "light_map_sampler";
    public static final String UNI_AMBIENT_LIGHT = "ambient_light";
    public static final String UNI_SPECULAR_POWER = "specular_power";
    public static final String UNI_MATERIAL = "material";
    public static final String UNI_COLOR = "color";
    public static final String UNI_SELECTED = "selected";
    public static final String UNI_POINT_LIGHT = "point_light";
    public static final String UNI_SPOT_LIGHT = "spot_light";
    public static final String UNI_DIRECTIONAL_LIGHT = "directional_light";

    public MainShader() throws Exception {
        super("main");
    }

    @Override
    public void setup() throws Exception {
        createVertexShader();
        createFragmentShader();
        link();

        createUniform(UNI_PROJECTION);
        createUniform(UNI_MODEL_VIEW);
        createUniform(UNI_TEX_SAMPLER);
        createUniform(UNI_LIGHT_MAP_SAMPLER);
        createUniform(UNI_AMBIENT_LIGHT);
        createUniform(UNI_SPECULAR_POWER);
        createUniform(UNI_COLOR);
        createUniform(UNI_SELECTED);

        createPointLightUniform(UNI_POINT_LIGHT);
        createSpotLightUniform(UNI_SPOT_LIGHT);
        createDirectionalLightUniform(UNI_DIRECTIONAL_LIGHT);

        createMaterialUniform(UNI_MATERIAL);
    }
}
