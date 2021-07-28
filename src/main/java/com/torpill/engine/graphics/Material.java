package com.torpill.engine.graphics;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class Material {

    public static final Vector4f DEFAULT_COLOR = new Vector4f();

    private final Vector4f ambient;
    private final Vector4f diffuse;
    private final Vector4f specular;
    private final float reflectance;

    private @Nullable Texture texture;

    public Material() {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, 0f);
    }

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float reflectance) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.reflectance = reflectance;
    }

    public Vector4f getAmbient() {
        return ambient;
    }

    public Vector4f getDiffuse() {
        return diffuse;
    }

    public Vector4f getSpecular() {
        return specular;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setTexture(@Nullable Texture texture) {
        this.texture = texture;
    }

    public @Nullable Texture getTexture() {
        return texture;
    }
}
