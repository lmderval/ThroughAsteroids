package com.torpill.engine.world;

public class World {

    private final int width;
    private final int height;
    private final int depth;

    private final int chunk_width;
    private final int chunk_depth;

    private final Chunk[] chunks;

    public World(int width, int height, int depth, int chunk_width, int chunk_depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.chunk_width = chunk_width;
        this.chunk_depth = chunk_depth;
        chunks = new Chunk[width * depth];
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

    public Chunk provideChunk(int x, int z) {
        Chunk chunk = new Chunk(chunk_width, height, chunk_depth);
        chunks[x + z * width] = chunk;
        return chunk;
    }

    public Chunk getChunk(int x, int z) {
        Chunk chunk = chunks[x + z * width];
        if (chunk == null) {
            chunk = provideChunk(x, z);
        }
        return chunk;
    }

    public void setBlock(int x, int y, int z, Block block) {
        getChunk(x / chunk_width, z / chunk_depth).setBlock(x % chunk_width, y, z % chunk_depth, block);
    }

    public Block getBlock(int x, int y, int z) {
        return getChunk(x / chunk_width, z / chunk_depth).getBlock(x % chunk_width, y, z % chunk_depth);
    }
}
