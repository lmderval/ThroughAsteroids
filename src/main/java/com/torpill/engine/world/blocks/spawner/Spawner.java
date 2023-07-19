package com.torpill.engine.world.blocks.spawner;

import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;

public interface Spawner {

    void summonEntity(@NotNull World world, int x, int y, int z);
}
