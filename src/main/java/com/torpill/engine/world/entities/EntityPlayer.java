package com.torpill.engine.world.entities;

import com.torpill.engine.world.World;
import com.torpill.engine.world.entities.projectiles.EntityLaser;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import java.util.Arrays;

import static com.torpill.engine.loader.MeshCache.SHIP;

public class EntityPlayer extends Entity {

    private int direction = 0;

    public EntityPlayer() {
        super(SHIP, 800f, new Matrix3x2f(0.5f, 0.5f, 0.1f, 0.25f, 0.5f, 0.5f));
        setScale(0.85f);
    }

    @Override
    public void update(@NotNull World world) {
        rotation.x = 20f * direction;
        speed.set(0.5f, 0f, direction);
        float[] normal = new float[3];
        float collisionTime = sweptAABB(world, normal);
//        System.out.println(collisionTime + " " + Arrays.toString(normal));
        position.add(speed.x * collisionTime, 0f, speed.z * collisionTime);
        if (normal[2] != 0f) {
            direction = 0;
        }
        if (normal[0] == -1f) {
            explode();
        }
        float remainingTime = 1.0f - collisionTime;
        float dotProd = (speed.x * normal[2] + speed.z * normal[0]) * remainingTime;
        speed.set(dotProd * normal[2], 0f, dotProd * normal[0]);
        collisionTime = sweptAABB(world, normal);
        position.add(speed.x * collisionTime, 0f, speed.z * collisionTime);
        if (normal[0] == -1f) {
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
        Entity laser = new EntityLaser(this);
        laser.setPosition(position);
        laser.drift(scale * 0.4f);
        laser.setScale(0.2f);
        laser.setColor(0f, 10f, 10f, 1f);
        world.addEntity(laser);
        laser = new EntityLaser(this);
        laser.setPosition(position);
        laser.drift(-scale * 0.4f);
        laser.setScale(0.2f);
        laser.setColor(0f, 10f, 10f, 1f);
        world.addEntity(laser);
    }
}
