package com.torpill.engine.world.blocks;

import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;

public class StandardBlock extends Block {

    public StandardBlock(Material material) {
        super(material);
    }

    @Override
    public void update(@NotNull World world, int x, int y, int z) {

    }
}
