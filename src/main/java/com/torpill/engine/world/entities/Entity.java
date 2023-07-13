package com.torpill.engine.world.entities;

import com.torpill.engine.Utils;
import com.torpill.engine.graphics.Transformation;
import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.world.World;
import com.torpill.engine.world.blocks.Block;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;

public abstract class Entity {

    protected final float mass;
    protected final Vector3f acc = new Vector3f();
    protected final Vector3f speed = new Vector3f();
    protected final Vector3f position = new Vector3f();
    protected final Vector3f rotation = new Vector3f();
    protected final Matrix3x2f box = new Matrix3x2f(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    private final Mesh mesh;
    private final Transformation transformation = new Transformation();
    private final Vector4f color = new Vector4f(1f);
    private final Matrix4f mv_mat = new Matrix4f();
    private final Matrix4f mv_mat_pos = new Matrix4f();
    protected boolean alive;
    protected float scale = 1f;

    public Entity(@NotNull Mesh mesh, float mass) {
        alive = true;
        this.mesh = mesh;
        this.mass = mass;
    }

    public Entity(@NotNull Mesh mesh, float mass, @NotNull Matrix3x2f box) {
        this(mesh, mass);
        this.box.set(box);
    }

    public abstract void update(@NotNull World world);

    protected boolean collision(@NotNull World world) {
        int bx = (int) Math.floor(position.x + 0.5f);
        int by = (int) Math.floor(position.y + 0.5f);
        int bz = (int) Math.floor(position.z + 0.5f);
        Block b;
        for (int i = (int) -Math.ceil(box.m00); i <= (int) Math.ceil(box.m01); i++) {
            for (int j = (int) -Math.ceil(box.m10); j <= (int) Math.ceil(box.m11); j++) {
                for (int k = (int) -Math.ceil(box.m20); k <= (int) Math.ceil(box.m21); k++) {
                    if ((b = world.getBlock(bx + i, by + j, bz + k)) != null) {
                        float mx = bx + i - 0.5f + b.offsetX();
                        float my = by + j - 0.5f + b.offsetY();
                        float mz = bz + k - 0.5f + b.offsetZ();
                        if (Intersectionf.testAabAab(
                                position.x - box.m00 / 2, position.y - box.m10 / 2, position.z - box.m20 / 2,
                                position.x + box.m01 / 2, position.y + box.m11 / 2, position.z + box.m21 / 2,
                                mx, my, mz,
                                mx + b.width(), my + b.height(), mz + b.depth()
                        )) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected float sweptAABB(@NotNull World world, float[] normal) {
        Utils.Box b1 = getBox();
        float minx = b1.x;
        float maxx = b1.x + b1.w;
        float miny = b1.y;
        float maxy = b1.y + b1.h;
        float minz = b1.z;
        float maxz = b1.z + b1.d;
        if (speed.x > 0f) {
            maxx += speed.x * 2f;
        } else {
            minx += speed.x * 2f;
        }
        if (speed.y > 0f) {
            maxy += speed.y * 2f;
        } else {
            miny += speed.y * 2f;
        }
        if (speed.z > 0f) {
            maxz += speed.z * 2f;
        } else {
            minz += speed.z * 2f;
        }
//        System.out.println("b1.y:" + b1.y);
        float collisionTime = 1.0f;
        Utils.Box b2;
        normal[0] = 0.0f;
        normal[2] = 0.0f;
        // find all blocks that may collide
        for (int i = (int) Math.ceil(minx); i < (int) Math.ceil(maxx); i++) {
            for (int j = (int) Math.ceil(miny); j < (int) Math.ceil(maxy); j++) {
                for (int k = (int) Math.ceil(minz); k < (int) Math.ceil(maxz); k++) {
                    Block b = world.getBlock(i, j, k);
                    if (b != null) {
                        // find collision time
                        b2 = b.getBox(i, j, k);
//                        System.out.println("b2.y:" + b2.y);

                        // find the distance between the objects on the near and far sides for both x, y and z
                        float xmin = b2.x - (b1.x + b1.w);
                        float xmax = (b2.x + b2.w) - b1.x;
                        float ymin = b2.y - (b1.y + b1.h);
                        float ymax = (b2.y + b2.h) - b1.y;
                        float zmin = b2.z - (b1.z + b1.d);
                        float zmax = (b2.z + b2.d) - b1.z;

//                        System.out.println("ymin:" + ymin);
//                        System.out.println("ymax:" + ymax);

                        // find time of collision and time of leaving for each axis (if statement is to prevent divide by zero)
                        float xEntry = Float.NEGATIVE_INFINITY;
                        float xExit = Float.POSITIVE_INFINITY;
                        float yEntry = Float.NEGATIVE_INFINITY;
                        float yExit = Float.POSITIVE_INFINITY;
                        float zEntry = Float.NEGATIVE_INFINITY;
                        float zExit = Float.POSITIVE_INFINITY;

                        if (b1.vx != 0.0f) {
                            float t1 = xmin / b1.vx;
                            float t2 = xmax / b1.vx;
                            xEntry = Math.min(t1, t2);
                            xExit = Math.max(t1, t2);
                        } else if (xmin >= 0 || xmax <= 0) {
                            break;
                        }
                        if (b1.vy != 0.0f) {
                            float t1 = ymin / b1.vy;
                            float t2 = ymax / b1.vy;
                            yEntry = Math.min(t1, t2);
                            yExit = Math.max(t1, t2);
                        } else if (ymin >= 0 || ymax <= 0) {
                            break;
                        }
                        if (b1.vz != 0.0f) {
                            float t1 = zmin / b1.vz;
                            float t2 = zmax / b1.vz;
                            zEntry = Math.min(t1, t2);
                            zExit = Math.max(t1, t2);
                        } else if (zmin >= 0 || zmax <= 0) {
                            break;
                        }

                        // find the earliest/latest times of collision
                        float lastEntry = Math.max(Math.max(xEntry, yEntry), zEntry);
                        float firstExit = Math.min(Math.min(xExit, yExit), zExit);

//                        System.out.println("lastEntry:" + lastEntry);
//                        System.out.println("firstExit:" + firstExit);

                        // if there was a collision
                        if (firstExit > lastEntry && firstExit > 0f && lastEntry < 1f) {
                            // calculate normal of collided surface
                            if (collisionTime > lastEntry) {
                                if (xEntry > yEntry && xEntry > zEntry) {
                                    if (xmin < 0f) {
                                        normal[0] = 1f;
                                    } else {
                                        normal[0] = -1f;
                                    }
                                    normal[1] = 0f;
                                    normal[2] = 0f;
                                } else if (yEntry > xEntry && yEntry > zEntry) {
                                    normal[0] = 0f;
                                    if (ymin < 0f) {
                                        normal[1] = 1f;
                                    } else {
                                        normal[1] = -1f;
                                    }
                                    normal[2] = 0f;
                                } else {
                                    normal[0] = 0f;
                                    normal[1] = 0f;
                                    if (zmin < 0f) {
                                        normal[2] = 1f;
                                    } else {
                                        normal[2] = -1f;
                                    }
                                }

                                // return the time of collision
                                collisionTime = lastEntry;
                            }
                        }
                    }
                }
            }
        }

        return collisionTime;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(@NotNull Vector3f position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    public void forward(float dr) {
        position.add((float) Math.cos(Math.toRadians(rotation.y)) * dr, 0f, (float) -Math.sin(Math.toRadians(rotation.y)) * dr);
    }

    public void drift(float dr) {
        position.add((float) -Math.sin(Math.toRadians(rotation.y)) * dr, 0f, (float) -Math.cos(Math.toRadians(rotation.y)) * dr);
    }

    public void rotate(float dt) {
        rotation.add(0f, dt, 0f);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }

    public Vector4f getColor() {
        return color;
    }

    public void rotate(float rx, float ry, float rz) {
        rotation.add(rx, ry, rz);
    }

    public void recalculate(@NotNull Matrix4f view_mat) {
        transformation.setModelView(position, scale, rotation, view_mat, mv_mat);
    }

    public void recalculatePositionOnly(@NotNull Matrix4f view_mat) {
        transformation.setModelView(position, 1f, new Vector3f(0f), view_mat, mv_mat_pos);
    }

    public Matrix4f getModelViewMat() {
        return mv_mat;
    }

    public Matrix4f getModelViewMatPositionOnly() {
        return mv_mat_pos;
    }

    public Matrix3x2f getBoxMatrix3x2f() {
        return box;
    }

    public Utils.Box getBox() {
        return new Utils.Box(
                -box.m00 + position.x, -box.m10 + position.y, -box.m20 + position.z,
                box.m00 + box.m01, box.m10 + box.m11, box.m20 + box.m21,
                speed.x, speed.y, speed.z
        );
    }
}
