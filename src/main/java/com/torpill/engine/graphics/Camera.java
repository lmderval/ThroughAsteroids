package com.torpill.engine.graphics;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Transformation transformation = new Transformation();

    private final Vector3f position = new Vector3f();
    private final Vector3f rotation = new Vector3f();

    private final Matrix4f view_mat = new Matrix4f();

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setPosition(@NotNull Vector3f position) {
        this.position.set(position);
    }

    public void move(float dx, float dy, float dz) {
        if (dz != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * dz;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * dz;
        }
        if (dx != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * dx;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * dx;
        }
        position.y += dy;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public void setRotation(@NotNull Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public void rotate(float rx, float ry, float rz) {
        rotation.add(rx, ry, rz);
    }

    public void updateViewMat() {
        transformation.setView(position, rotation, view_mat);
    }

    public Matrix4f getViewMat() {
        return view_mat;
    }
}
