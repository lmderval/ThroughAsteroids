package com.torpill.engine.world.entities;

import com.torpill.engine.world.World;
import com.torpill.engine.world.entities.projectiles.EntityLaser;
import com.torpill.engine.world.entities.projectiles.EntityProjectile;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;

import static com.torpill.engine.loader.MeshCache.TOWER;

public class EntityTower extends Entity implements Shooter {

    private int cooldown = 0;
    private boolean inRange = false;
    private float theta;

    public EntityTower() {
        super(TOWER, 10000f, new Matrix3x2f(0.5f, 0.5f, 0.5f, 0.4f, 0.5f, 0.5f));
    }

    @Override
    public void update(@NotNull World world) {
        if (cooldown > 2 || !inRange) {
            Vector3f p = world.getPlayer().getPosition();
            float x = p.x + 3.5f - position.x;
            float z = p.z - position.z;
            float a = (float) Math.sqrt(x * x + z * z);
            float theta = (float) Math.toDegrees(Math.acos(x / a));
            if (z >= 0) {
                theta *= -1;
            }
            setRotation(0f, theta, 0f);
            inRange = a <= 10;
        }
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public boolean canShoot() {
        return cooldown <= 0 && inRange;
    }

    public Collection<EntityProjectile> shoot(@NotNull World world) {
        EntityProjectile laser = new EntityLaser(this, 1f, 0f, 0f);
        laser.forward(0.75f);
        laser.setColor(10f, 0f, 0f, 1f);
        if (--cooldown == -3) {
            cooldown = 15;
        }
        return new ArrayList<>(1) {{
            add(laser);
        }};
    }
}
