package com.torpill.engine.world.entities.projectiles;

import com.torpill.engine.world.entities.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import static com.torpill.engine.loader.MeshCache.LASER;

public class EntityLaser extends EntityProjectile {

    public EntityLaser(@NotNull Entity thrower) {
        super(LASER, 0f, new Matrix3x2f(0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0.125f), thrower, 0.75f);
    }
}
