package com.torpill.engine.world.entities;

import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.graphics.Transformation;
import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Intersectionf;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Entity {

    private final Mesh mesh;

    protected final float mass;

    protected boolean alive;

    protected final Vector3f acc = new Vector3f();
    protected final Vector3f speed = new Vector3f();

    private final Transformation transformation = new Transformation();

    protected final Vector3f position = new Vector3f();
    protected float scale = 1f;
    protected final Vector3f rotation = new Vector3f();
    protected final Matrix3x2f box = new Matrix3x2f(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);

    private final Matrix4f model_view_mat = new Matrix4f();

    public Entity(@NotNull Mesh mesh, float mass) {
        alive = true;
        this.mesh = mesh;
        this.mass = mass;
    }

    public Entity(@NotNull Mesh mesh, float mass, @NotNull Matrix3x2f box) {
        this(mesh, mass);
        this.box.set(box);
    }

    public abstract void update(@NotNull World world);

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

    public void forward(float dr) {
        position.add((float) Math.sin(Math.toRadians(rotation.y)) * dr, 0f, (float) Math.cos(Math.toRadians(rotation.y)) * dr);
    }

    public void drift(float dr) {
        position.add((float) Math.cos(Math.toRadians(rotation.y)) * dr, 0f, (float) -Math.sin(Math.toRadians(rotation.y)) * dr);
    }

    public void rotate(float dt) {
        rotation.add(0f, dt, 0f);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
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
