package com.torpill.engine.world.entities;

import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Intersectionf;
import org.joml.Matrix3x2f;
import org.joml.Vector3f;

import static com.torpill.engine.loader.MeshCache.SHIP;
import static java.lang.Math.PI;

public class PhysicsEntity extends Entity {

    private float roll_speed = 0f;
    private float roll_acc = 0f;

    private final Vector3f force = new Vector3f();

    public PhysicsEntity() {
        super(SHIP, 800f, new Matrix3x2f(0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f));
    }

    public void applyForce(@NotNull Vector3f force) {
        this.force.set(force);
    }

    public void roll(float a) {
        roll_acc += a;
    }

    @Override
    public void update(@NotNull World world) {
        float roll = (float) Math.toRadians(roll_speed);
        while (roll > PI) roll -= PI * 2f;
        roll_speed += roll_acc;
        while (roll_speed > 360f) roll_speed -= 360f;
        roll_acc = 25f * 1.6f / 2.0f * (float) Math.sin(roll * 2f + PI) - 1.0f / mass * roll_speed;
        rotation.z = (float) Math.toDegrees(roll);

        Vector3f friction = new Vector3f(speed).mul(-25f);

        float dx = speed.x;
        float dy = speed.y;
        float dz = speed.z;

        position.add(speed);
        speed.add(acc);
        acc.set(force).add(friction).div(mass);
        collision(world, dx, dy, dz);
    }

    protected boolean collision(@NotNull World world, float dx, float dy, float dz) {
        int bx = (int) Math.floor(position.x + 0.5f);
        int by = (int) Math.floor(position.y + 0.5f);
        int bz = (int) Math.floor(position.z + 0.5f);
        boolean collision = false;
        for (int i = (int) -Math.ceil(box.m00 * scale); i <= (int) Math.ceil(box.m01 * scale); i++) {
            for (int j = (int) -Math.ceil(box.m10 * scale); j <= (int) Math.ceil(box.m11 * scale); j++) {
                for (int k = (int) -Math.ceil(box.m20 * scale); k <= (int) Math.ceil(box.m21 * scale); k++) {
                    if ( /* i * i + j * j + k * k <= 1 && */ world.getBlock(bx + i, by + j, bz + k) != null) {
                        if (Intersectionf.testAabAab(
                                position.x - box.m00 * scale, position.y - box.m10 * scale, position.z - box.m20 * scale,
                                position.x + box.m01 * scale, position.y + box.m11 * scale, position.z + box.m21 * scale,
                                bx + i - 0.5f, by + j - 0.5f, bz + k - 0.5f,
                                bx + i + 0.5f, by + j + 0.5f, bz + k + 0.5f
                        )) {
                            float ax = acc.x;
                            float ay = acc.y;
                            float az = acc.z;
                            float sx = speed.x;
                            float sy = speed.y;
                            float sz = speed.z;
                            float ddx = 0f;
                            float ddy = 0f;
                            float ddz = 0f;
                            if (i > 0) {
                                ddx = i - 0.5f - box.m01 * scale;
                                ax = sx = 0f;
                            } else if (i < 0) {
                                ddx = i + 0.5f + box.m00 * scale;
                                ax = sx = 0f;
                            }
                            if (j > 0) {
                                ddy = j - 0.5f - box.m11 * scale;
                                ay = sy = 0f;
                            } else if (j < 0) {
                                ddy = i + 0.5f + box.m10 * scale;
                                ay = sy = 0f;
                            }
                            if (k > 0) {
                                ddz = k - 0.5f - box.m21 * scale;
                                az = sz = 0f;
                            } else if (k < 0) {
                                ddz = k + 0.5f + box.m20 * scale;
                                az = sz = 0f;
                            }
                            acc.set(ax, ay, az);
                            speed.set(sx, sy, sz);
                            position.add(ddx, ddy, ddz);
                            collision = true;
                        }
                    }
                }
            }
        }
        return collision;
    }
}
