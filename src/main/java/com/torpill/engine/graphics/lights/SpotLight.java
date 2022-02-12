package com.torpill.engine.graphics.lights;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SpotLight {

    public static final SpotLight NULL = new SpotLight(new Vector3f(), 0f, PointLight.NULL);

    private final Vector3f direction;
    private float cut_off;
    private final PointLight point;

    public SpotLight(@NotNull SpotLight spot_light) {
        direction = new Vector3f(spot_light.direction);
        cut_off = spot_light.cut_off;
        point = new PointLight(spot_light.point);
    }

    public SpotLight(@NotNull Vector3f direction, float cut_off_angle, @NotNull PointLight point) {
        this.direction = direction;
        setCutOff(cut_off_angle);
        this.point = point;
    }

    public void setDirection(float x, float y, float z) {
        direction.set(x, y, z);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getCutOff() {
        return cut_off;
    }

    public void setCutOff(float cut_off_angle) {
        this.cut_off = (float) Math.cos(Math.toRadians(cut_off_angle));
    }

    public PointLight getPointLight() {
        return point;
    }

    public Vector3f getPosition() {
        return point.getPosition();
    }

    public void setPosition(float x, float y, float z) {
        point.setPosition(x, y, z);
    }
}
