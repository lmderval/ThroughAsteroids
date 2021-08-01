package com.torpill.engine.world;

import com.torpill.engine.graphics.Mesh;
import com.torpill.engine.graphics.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Entity {

    private final Mesh mesh;

    private final Transformation transformation = new Transformation();

    private final Vector3f position = new Vector3f();
    private float scale = 1f;
    private final Vector3f rotation = new Vector3f();

    private final Matrix4f model_view_mat = new Matrix4f();

    public Entity(@NotNull Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public void rotate(float rx, float ry, float rz) {
        rotation.add(rx, ry, rz);
    }

    public void recalculate(@NotNull Matrix4f view_mat) {
        transformation.setModelView(position, scale, rotation, view_mat, model_view_mat);
    }

    public Matrix4f getModelViewMat() {
        return model_view_mat;
    }
}
