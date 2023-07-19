package com.torpill.engine.world.blocks.spawner;

import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.world.World;
import com.torpill.engine.world.blocks.StandardBlock;
import com.torpill.engine.world.entities.EntityTower;
import org.jetbrains.annotations.NotNull;

public class TowerSpawner extends StandardBlock implements Spawner {

    public TowerSpawner(Material material) {
        super(material);
    }

    @Override
    public void summonEntity(@NotNull World world, int x, int y, int z) {
        EntityTower tower = new EntityTower();
        tower.setPosition(x, y + 1, z);
        world.addEntity(tower);
    }
}
