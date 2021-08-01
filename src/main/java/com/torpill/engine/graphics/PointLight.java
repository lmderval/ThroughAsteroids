package com.torpill.engine.graphics;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PointLight {

    private final Vector3f color;
    private final Vector3f position;
    private float intensity;
    private final Attenuation attenuation;

    public PointLight(@NotNull PointLight light) {
        color = new Vector3f(light.color);
        position = new Vector3f(light.position);
        intensity = light.intensity;
        attenuation = new Attenuation(light.attenuation);
    }

    public PointLight(@NotNull Vector3f color, @NotNull Vector3f position, float intensity, @NotNull Attenuation attenuation) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.attenuation = attenuation;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(float red, float green, float blue) {
        color.set(red, green, blue);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public static class Attenuation {

        private final float exponent;
        private final float linear;
        private final float constant;

        public Attenuation(Attenuation attenuation) {
            exponent = attenuation.exponent;
            linear = attenuation.linear;
            constant = attenuation.constant;
        }

        public Attenuation(float exponent, float linear, float constant) {
            this.exponent = exponent;
            this.linear = linear;
            this.constant = constant;
        }

        public float getExponent() {
            return exponent;
        }

        public float getLinear() {
            return linear;
        }

        public float getConstant() {
            return constant;
        }
    }
}
