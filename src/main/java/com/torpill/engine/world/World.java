package com.torpill.engine.world;

import com.torpill.engine.world.blocks.Block;
import com.torpill.engine.world.entities.Entity;
import com.torpill.engine.world.entities.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class World {

    private final int width;
    private final int height;
    private final int depth;

    private final int chunk_width;
    private final int chunk_depth;

    private final Chunk[] chunks;

    private final EntityPlayer player;
    private final List<Entity> entities = new ArrayList<>();

    private final Vector3i selected = new Vector3i(-1);

    public World(int width, int height, int depth, int chunk_width, int chunk_depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.chunk_width = chunk_width;
        this.chunk_depth = chunk_depth;
        chunks = new Chunk[width * depth];
        player = new EntityPlayer();
        addEntity(player);
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

    public int getChunkWidth() {
        return chunk_width;
    }

    public int getChunkDepth() {
        return chunk_depth;
    }

    public @Nullable Chunk provideChunk(int x, int z) {
        if (x < 0 || x >= width || z < 0 || z >= depth) {
            return null;
        }
        Chunk chunk = new Chunk(chunk_width, height, chunk_depth);
        chunks[x + z * width] = chunk;
        return chunk;
    }

    public @Nullable Chunk getChunk(int x, int z) {
        if (x < 0 || x >= width || z < 0 || z >= depth) {
            return null;
        }
        Chunk chunk = chunks[x + z * width];
        if (chunk == null) {
            chunk = provideChunk(x, z);
        }
        return chunk;
    }

    public @Nullable Chunk getChunkAt(float x, float z) {
        return getChunk((int) (x / 16f), (int) (z / 16f));
    }

    public void setBlock(int x, int y, int z, Block block) {
        Chunk chunk = getChunk(x / chunk_width, z / chunk_depth);
        if (chunk != null) {
            chunk.setBlock(x % chunk_width, y, z % chunk_depth, block);
        }
    }

    public @Nullable Block getBlock(int x, int y, int z) {
        Chunk chunk = getChunk(x / chunk_width, z / chunk_depth);
        if (chunk == null) {
            return null;
        }
        return chunk.getBlock(x % chunk_width, y, z % chunk_depth);
    }

    public @NotNull EntityPlayer getPlayer() {
        return player;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setSelected(int x, int y, int z) {
        selected.set(x, y, z);
    }

    public void setSelected(@NotNull Vector3i vec3) {
        setSelected(vec3.x, vec3.y, vec3.z);
    }

    public boolean isSelected(int x, int y, int z) {
        return x == selected.x && y == selected.y && z == selected.z;
    }

    public boolean isSelected(@NotNull Vector3i vec3) {
        return isSelected(vec3.x, vec3.y, vec3.z);
    }
}
