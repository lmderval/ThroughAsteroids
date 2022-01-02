package com.torpill.engine.world.entities;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import static com.torpill.engine.loader.MeshCache.SHIP;
import static java.lang.Math.PI;

public class EntityPlayer extends Entity {

    private Vector3f acc = new Vector3f();
    private Vector3f speed = new Vector3f();

    private float roll_speed = 0f;
    private float roll_acc = 0f;

    private Vector3f force = new Vector3f();

    public EntityPlayer() {
        super(SHIP, 800f);
    }

    public void applyForce(@NotNull Vector3f force) {
        this.force.set(force);
    }

    public void roll(float a) {
        roll_acc += a;
    }

    @Override
    public void update() {

        float roll = (float) Math.toRadians(roll_speed);
        while (roll > PI) roll -= PI * 2f;
        roll_speed += roll_acc;
        while (roll_speed > 360f) roll_speed -= 360f;
        roll_acc = 25f * 1.6f / scale * (float) Math.sin(roll * 2f + PI) - 1.0f / mass * roll_speed;
        rotation.z = (float) Math.toDegrees(roll);

        Vector3f friction = new Vector3f(speed).mul(-25f);

        position.add(speed);
        speed.add(acc);
        acc.set(force).add(friction).div(mass);
//        System.out.println(rotation.z + "\t" + roll_speed + "\t" + roll_acc);
    }
}
