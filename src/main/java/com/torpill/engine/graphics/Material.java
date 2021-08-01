package com.torpill.engine.graphics;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class Material {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1f);

    private final Vector4f ambient;
    private final Vector4f diffuse;
    private final Vector4f specular;
    private final Vector4f emissive;
    private float reflectance;
    private float emissivity;

    private @Nullable Texture texture;

    public Material() {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, 1f, 0f);
    }

    public Material(float reflectance) {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, reflectance, 0f);
    }

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, Vector4f emissive, float reflectance, float emissivity) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.emissive = emissive;
        this.reflectance = reflectance;
        this.emissivity = emissivity;
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

    public Vector4f getEmissive() {
        return emissive;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public float getEmissivity() {
        return emissivity;
    }

    public void setEmissivity(float emissivity) {
        this.emissivity = emissivity;
    }

    public void setTexture(@Nullable Texture texture) {
        this.texture = texture;
    }

    public @Nullable Texture getTexture() {
        return texture;
    }

    public boolean isTextured() {
        return texture != null;
    }
}
