package com.torpill.engine.world.entities.projectiles;

import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.world.World;
import com.torpill.engine.world.entities.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

public class EntityProjectile extends Entity {

    private final float speed;

    public EntityProjectile(@NotNull Mesh mesh, float mass, @NotNull Matrix3x2f box, @NotNull Entity thrower, float speed) {
        super(mesh, mass, box);
        double angle = thrower.getRotation().y;
        rotation.set(0f, angle, 0f);
        this.speed = speed;
    }

    @Override
    public void update(@NotNull World world) {
        forward(speed);
        if (collision(world)) {
            setAlive(false);
        }
    }
}
