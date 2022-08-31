package com.torpill.engine.world.entities;

import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.graphics.Transformation;
import com.torpill.engine.world.World;
import com.torpill.engine.world.blocks.Block;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;

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

    private final Vector4f color = new Vector4f(1f);

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

    protected boolean collision(@NotNull World world) {
        int bx = (int) Math.floor(position.x + 0.5f);
        int by = (int) Math.floor(position.y + 0.5f);
        int bz = (int) Math.floor(position.z + 0.5f);
        Block b;
        for (int i = (int) -Math.ceil(box.m00 * scale); i <= (int) Math.ceil(box.m01 * scale); i++) {
            for (int j = (int) -Math.ceil(box.m10 * scale); j <= (int) Math.ceil(box.m11 * scale); j++) {
                for (int k = (int) -Math.ceil(box.m20 * scale); k <= (int) Math.ceil(box.m21 * scale); k++) {
                    if ((b = world.getBlock(bx + i, by + j, bz + k)) != null) {
                        float mx = bx + i - 0.5f + b.offsetX();
                        float my = by + j - 0.5f + b.offsetY();
                        float mz = bz + k - 0.5f + b.offsetZ();
                        if (Intersectionf.testAabAab(
                                position.x - box.m00 * scale, position.y - box.m10 * scale, position.z - box.m20 * scale,
                                position.x + box.m01 * scale, position.y + box.m11 * scale, position.z + box.m21 * scale,
                                mx, my, mz,
                                mx + b.width(), my + b.height(), mz + b.depth()
                        )) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(@NotNull Vector3f position) {
        this.position.set(position);
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

    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }

    public Vector4f getColor() {
        return color;
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
