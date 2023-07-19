package com.torpill.engine.world;

import com.torpill.engine.world.blocks.Block;
import com.torpill.engine.world.blocks.spawner.Spawner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Chunk {

    private final int width;
    private final int height;
    private final int depth;
    private final Block[] blocks;

    public Chunk(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        blocks = new Block[width * height * depth];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public void setBlock(int x, int y, int z, @Nullable Block block) {
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) {
            return;
        }
        blocks[x + y * width + z * width * height] = block;
    }

    public @Nullable Block getBlock(int x, int y, int z) {
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) {
            return null;
        }
        return blocks[x + y * width + z * width * height];
    }

    public void update(@NotNull World world, int chunkX, int chunkZ) {
        Block block;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    if ((block = blocks[x + y * width + z * width * height]) != null) {
                        block.update(world, x + chunkX * width, y, z + chunkZ * depth);
                    }
                }
            }
        }
    }

    public void summonEntities(@NotNull World world, int chunkX, int chunkZ) {
        Block block;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    if ((block = blocks[x + y * width + z * width * height]) instanceof Spawner) {
                        ((Spawner) block).summonEntity(world, x + chunkX * width, y, z + chunkZ * depth);
                    }
                }
            }
        }
    }
}
