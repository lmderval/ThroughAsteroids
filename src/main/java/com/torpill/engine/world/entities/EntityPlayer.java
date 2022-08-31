package com.torpill.engine.world.entities;

import com.torpill.engine.world.World;
import com.torpill.engine.world.entities.projectiles.EntityProjectile;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import static com.torpill.engine.loader.MeshCache.LASER;
import static com.torpill.engine.loader.MeshCache.SHIP;

public class EntityPlayer extends Entity {

    private int direction = 0;

    public EntityPlayer() {
        super(SHIP, 800f, new Matrix3x2f(0.5f, 0.7f, 0.1f, 0.2f, 0.5f, 0.5f));
        setScale(0.85f);
    }

    @Override
    public void update(@NotNull World world) {
        rotation.z = 20f * direction;
        position.add(0.5f, 0f, 1f * direction);
        if (collision(world)) {
            position.add(0f, 0f, -1f * direction);
            direction = 0;
        }
        if (collision(world)) {
            explode();
        }
    }

    public void control(int direction) {
        if (this.direction == 0) this.direction = direction;
    }

    public void resetControl() {
        direction = 0;
    }

    private void explode() {
        alive = false;
    }

    public void throwLaser(@NotNull World world) {
        Entity laser = new EntityProjectile(LASER, 0f, new Matrix3x2f(0.5f, 0.7f, 0.1f, 0.2f, 0.5f, 0.5f), this, 0.75f);
        laser.setPosition(position);
        laser.drift(scale * 0.4f);
        laser.setScale(0.2f);
        laser.setColor(0f, 10f, 10f, 1f);
        world.addEntity(laser);
        laser = new EntityProjectile(LASER, 0f, new Matrix3x2f(0.5f, 0.7f, 0.1f, 0.2f, 0.5f, 0.5f), this, 0.75f);
        laser.setPosition(position);
        laser.drift(-scale * 0.4f);
        laser.setScale(0.2f);
        laser.setColor(0f, 10f, 10f, 1f);
        world.addEntity(laser);
    }
}
