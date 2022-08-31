package com.torpill.engine.hud;

import com.torpill.engine.graphics.Transformation;
import com.torpill.engine.graphics.meshes.Mesh;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class HudItem {

    private final Mesh mesh;

    private final Transformation transformation = new Transformation();

    protected final Vector3f position = new Vector3f(0f, 0f, -1f);
    protected float scale = 1f;
    protected final Vector3f rotation = new Vector3f();

    protected final Vector4f color = new Vector4f(1f);

    private final Matrix4f model_mat = new Matrix4f();

    private final boolean is3D;

    public HudItem(@NotNull Mesh mesh) {
        this.mesh = mesh;
        is3D = false;
    }

    public HudItem(@NotNull Mesh mesh, boolean is3D) {
        this.mesh = mesh;
        this.is3D = is3D;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void recalculate() {
        transformation.setWorld(position, scale, rotation, model_mat);
    }

    public Matrix4f getModelMat() {
        return model_mat;
    }

    public void setPosition(float x, float y) {
        position.set(x, y, -1f);
    }

    public void setScale(float scale) {
        this.scale = scale;
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

    public boolean is3D() {
        return is3D;
    }
}
