package com.torpill.engine.world.blocks;

import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.graphics.meshes.Mesh;
import com.torpill.engine.loader.MeshCache;

public class Block {

    public static Mesh BLOCK_MESH;

    private final Mesh mesh;

    public static void loadMesh() throws Exception {
        BLOCK_MESH = MeshCache.getInstance().getStaticMeshes("models/block/block.obj", null)[0];
    }

    public Block(Material material) {
        mesh = new Mesh(BLOCK_MESH);
        mesh.setMaterial(material);
    }

    public Mesh getMesh() {
        return mesh;
    }
}
