package com.torpill.engine.world.entities.projectiles;

import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.world.World;
import com.torpill.engine.world.entities.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

public class EntityProjectile extends Entity {

    public EntityProjectile(@NotNull Mesh mesh, float mass, @NotNull Matrix3x2f box, @NotNull Entity thrower, float speed) {
        super(mesh, mass, box);
        double angle = Math.toRadians(thrower.getRotation().y);
        rotation.set(0f, thrower.getRotation().y, 0f);
        this.speed.set(Math.sin(angle) * speed, 0f, Math.cos(angle) * speed);
    }

    @Override
    public void update(@NotNull World world) {
        position.add(speed);
        speed.add(acc);
        acc.set(0f, 0f, 0f);
        if (collision(world)) {
            setAlive(false);
        }
    }
}
