package com.torpill.engine.world.entities;

import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Intersectionf;
import org.joml.Matrix3x2f;

import static com.torpill.engine.loader.MeshCache.SHIP;

public class EntityPlayer extends Entity {

    private int direction = 0;

    public EntityPlayer() {
        super(SHIP, 800f, new Matrix3x2f(0.5f, 0.7f, 0.5f, 0.5f, 0.5f, 0.5f));
        setScale(0.85f);
    }

    @Override
    public void update(@NotNull World world) {
        if (alive) {
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
    }

    public void control(int direction) {
        if (this.direction == 0) this.direction = direction;
    }

    public void resetControl() {
        direction = 0;
    }

    private boolean collision(@NotNull World world) {
        int bx = (int) Math.floor(position.x + 0.5f);
        int by = (int) Math.floor(position.y + 0.5f);
        int bz = (int) Math.floor(position.z + 0.5f);
        for (int i = (int) -Math.ceil(box.m00 * scale); i <= (int) Math.ceil(box.m01 * scale); i++) {
            for (int j = (int) -Math.ceil(box.m10 * scale); j <= (int) Math.ceil(box.m11 * scale); j++) {
                for (int k = (int) -Math.ceil(box.m20 * scale); k <= (int) Math.ceil(box.m21 * scale); k++) {
                    if (world.getBlock(bx + i, by + j, bz + k) != null) {
                        if (Intersectionf.testAabAab(
                                position.x - box.m00 * scale, position.y - box.m10 * scale, position.z - box.m20 * scale,
                                position.x + box.m01 * scale, position.y + box.m11 * scale, position.z + box.m21 * scale,
                                bx + i - 0.5f, by + j - 0.5f, bz + k - 0.5f,
                                bx + i + 0.5f, by + j + 0.5f, bz + k + 0.5f
                        )) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void explode() {
        alive = false;
    }
}
