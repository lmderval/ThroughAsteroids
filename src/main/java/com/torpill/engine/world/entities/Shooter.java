package com.torpill.engine.world.entities;

import com.torpill.engine.world.World;
import com.torpill.engine.world.entities.projectiles.EntityProjectile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Shooter {

    Collection<EntityProjectile> shoot(@NotNull World world);

    boolean canShoot();
}
