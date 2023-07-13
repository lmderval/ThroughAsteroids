package com.torpill.engine.world.blocks;

import com.torpill.engine.Utils;
import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.loader.MeshCache;
import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;

public abstract class Block {

    private static Mesh BLOCK_MESH;

    private final Mesh mesh;

    private final Vector3f offset;
    private final Vector3f dimension;

    public static void loadMesh() throws Exception {
        BLOCK_MESH = MeshCache.getInstance().getStaticMeshes("models/blocks/block.obj", null)[0];
    }

    protected Block(@NotNull Mesh mesh, @Nullable Material material, @NotNull Vector3f offset, @NotNull Vector3f dimension) {
        this.mesh = new Mesh(mesh);
        this.mesh.setMaterial(material);
        this.offset = offset;
        this.dimension = dimension;
    }

    public Block(Material material) {
        this(BLOCK_MESH, material, new Vector3f(0f), new Vector3f(1f));
    }

    public abstract void update(@NotNull World world, int x, int y, int z);

    public Mesh getMesh() {
        return mesh;
    }

    public float offsetX() {
        return offset.x();
    }

    public float offsetY() {
        return offset.y();
    }

    public float offsetZ() {
        return offset.z();
    }

    public float width() {
        return dimension.x();
    }

    public float height() {
        return dimension.y();
    }

    public float depth() {
        return dimension.z();
    }

    public Utils.Box getBox(int i, int j, int k) {
        return new Utils.Box(
                i - width() / 2 + offsetX(), j - height() / 2 + offsetY(), k - depth() / 2 + offsetZ(),
                width(), height(), depth(),
                0f, 0f, 0f
        );
    }

    public Utils.Box getBox(@NotNull Vector3i coord) {
        return getBox(coord.x, coord.y, coord.z);
    }

    public enum Face {
        NULL,
        TOP,
        BOTTOM,
        RIGHT,
        LEFT,
        FRONT,
        BACK
    }
}
