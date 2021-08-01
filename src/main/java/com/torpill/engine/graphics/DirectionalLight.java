package com.torpill.engine.graphics;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class DirectionalLight {

    private final Vector3f color;
    private final Vector3f direction;
    private float intensity;

    public DirectionalLight(@NotNull DirectionalLight light) {
        color = new Vector3f(light.color);
        direction = new Vector3f(light.direction);
        intensity = light.intensity;
    }

    public DirectionalLight(@NotNull Vector3f color, @NotNull Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(float red, float green, float blue) {
        color.set(red, green, blue);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(float x, float y, float z) {
        direction.set(x, y, z);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
