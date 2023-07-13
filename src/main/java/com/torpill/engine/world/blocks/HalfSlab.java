package com.torpill.engine.world.blocks;

import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.loader.MeshCache;
import org.joml.Vector3f;

public abstract class HalfSlab extends Block {

    private static Mesh BLOCK_MESH;

    public static void loadMesh() throws Exception {
        BLOCK_MESH = MeshCache.getInstance().getStaticMeshes("models/blocks/half_slab.obj", null)[0];
    }

    public HalfSlab(Material material) {
        super(BLOCK_MESH, material, new Vector3f(0f, -0.375f, 0f), new Vector3f(1f, 0.25f, 1f));
    }
}
