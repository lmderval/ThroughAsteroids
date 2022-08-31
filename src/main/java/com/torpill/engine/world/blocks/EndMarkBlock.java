package com.torpill.engine.world.blocks;

import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;

public class EndMarkBlock extends HalfSlab {

    public EndMarkBlock(Material material) {
        super(material);
    }

    @Override
    public void update(@NotNull World world, int x, int y, int z) {
        float playerX = world.getPlayer().getPosition().x;
        float playerZ = world.getPlayer().getPosition().z;
        if ((x - playerX) * (x - playerX) + (z - playerZ) * (z - playerZ) < 0.3) {
            world.terminate();
        }
    }
}
