package com.torpill.engine.graphics;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f buff_mat = new Matrix4f();

    public void setPerspective(float fov, float width, float height, float z_near, float z_far, @NotNull Matrix4f perspective) {
        perspective.identity()
                .perspective(fov, width / height, z_near, z_far);
    }

    public void setOrthogonal(float width, float height, float z_near, float z_far, @NotNull Matrix4f perspective) {
        perspective.identity()
                .orthoSymmetric(width, height, z_near, z_far, perspective)
                .scale(height / 720f * 22.1f);
    }

    public void setWorld(@NotNull Vector3f position, float scale, @NotNull Vector3f rotation, @NotNull Matrix4f world) {
        world.identity()
                .translate(position.x * 2f, position.y * 2f, position.z * 2f)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);
    }

    public void setWorld(@NotNull Vector3f position, @NotNull Matrix4f world) {
        world.identity()
                .translate(position.x * 2f, position.y * 2f, position.z * 2f);
    }

    public void setView(@NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Matrix4f view) {
        view.identity()
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .translate(-position.x * 2f, -position.y * 2f, -position.z * 2f);
    }

    public void setModelView(@NotNull Vector3f position, float scale, @NotNull Vector3f rotation, @NotNull Matrix4f view, @NotNull Matrix4f model_view) {
        setWorld(position, scale, rotation, buff_mat);
        model_view.set(view).mul(buff_mat);
    }

    public void setModelView(@NotNull Vector3f position, @NotNull Matrix4f view, @NotNull Matrix4f model_view) {
        setWorld(position, buff_mat);
        model_view.set(view).mul(buff_mat);
    }
}
