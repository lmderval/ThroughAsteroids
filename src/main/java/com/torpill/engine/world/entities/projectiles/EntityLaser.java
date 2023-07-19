package com.torpill.engine.world.entities.projectiles;

import com.torpill.engine.world.entities.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import static com.torpill.engine.loader.MeshCache.LASER;

public class EntityLaser extends EntityProjectile {

    public EntityLaser(@NotNull Entity thrower, float r, float g, float b) {
        super(LASER, 0f, new Matrix3x2f(0.175f, 0.275f, 0.1f, 0.1f, 0.1f, 0.1f), thrower, 0.75f);
        setScale(0.2f);
        setColor(r, g, b, 1f);
    }

    public EntityLaser(@NotNull Entity thrower) {
        this(thrower, 1f, 1f, 1f);
        setScale(0.2f);
    }
}
