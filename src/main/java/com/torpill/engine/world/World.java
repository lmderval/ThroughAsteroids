package com.torpill.engine.world;

import com.torpill.engine.world.blocks.Block;
import com.torpill.engine.world.blocks.Blocks;
import com.torpill.engine.world.entities.Entity;
import com.torpill.engine.world.entities.EntityPlayer;
import com.torpill.engine.world.entities.PhysicsEntity;
import com.torpill.engine.world.entities.Shooter;
import com.torpill.engine.world.entities.projectiles.EntityProjectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private boolean terminated = false;

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

    public void load(@NotNull String dir) {
        File directory = Path.of(dir + "/data").toFile();
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles((dir1, name) -> name.endsWith(".dat")))) {
                String name = file.getName();
                Pattern p = Pattern.compile("^x(\\d*)z(\\d*)\\.dat$");
                Matcher m = p.matcher(name);
                if (m.find()) {
                    loadChunk(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), file);
                }
            }
        }
    }

    private void loadChunk(int chunkX, int chunkZ, @NotNull File file) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            Chunk chunk = provideChunk(chunkX, chunkZ);
            if (chunk != null) {
                byte[] data;
                while ((data = in.readNBytes(16)).length > 0) {
                    int x = data[0] << 24 | data[1] << 16 | data[2] << 8 | data[3];
                    int y = data[4] << 24 | data[5] << 16 | data[6] << 8 | data[7];
                    int z = data[8] << 24 | data[9] << 16 | data[10] << 8 | data[11];
                    int b = data[12] << 24 | data[13] << 16 | data[14] << 8 | data[15];
                    chunk.setBlock(x, y, z, Blocks.blocks.get(b));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(@NotNull String dir) {
        for (int chunkX = 0; chunkX < width; chunkX++) {
            for (int chunkZ = 0; chunkZ < depth; chunkZ++) {
                if (getChunkIfProvided(chunkX, chunkZ) != null) {
                    saveChunk(chunkX, chunkZ, dir);
                }
            }
        }
    }


    private void saveChunk(int chunkX, int chunkZ, @NotNull String dir) {
        Path filepath = Path.of(dir + "/data/x" + chunkX + "z" + chunkZ + ".dat");
        File file = filepath.toFile();
        File directory = Path.of(dir + "/data").toFile();
        if (!directory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdirs();
        }
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            Chunk chunk = getChunkIfProvided(chunkX, chunkZ);
            for (int i = 0; i < chunk_width; i++) {
                for (int j = 0; j < height; j++) {
                    for (int k = 0; k < chunk_depth; k++) {
                        @SuppressWarnings("ConstantConditions") Block block = chunk.getBlock(i, j, k);
                        if (block != null) {
                            out.writeInt(i);
                            out.writeInt(j);
                            out.writeInt(k);
                            out.writeInt(Blocks.map.get(block));
                        }
                    }
                }
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        Chunk chunk;
        List<EntityProjectile> projectiles = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Shooter && ((Shooter) entity).canShoot()) {
                projectiles.addAll(((Shooter) entity).shoot(this));
            }
        }
        entities.addAll(projectiles);
        for (Entity entity : entities) {
            if (entity.isAlive()) {
                entity.update(this);
            }
        }
        removeAll(entity -> entity != player && !entity.isAlive());
        for (int chunkX = 0; chunkX < width; chunkX++) {
            for (int chunkZ = 0; chunkZ < depth; chunkZ++) {
                if ((chunk = getChunkIfProvided(chunkX, chunkZ)) != null) {
                    chunk.update(this, chunkX, chunkZ);
                }
            }
        }
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

    public @Nullable Chunk getChunkIfProvided(int x, int z) {
        if (x < 0 || x >= width || z < 0 || z >= depth) {
            return null;
        }
        return chunks[x + z * width];
    }

    public void setBlock(int x, int y, int z, Block block) {
        Chunk chunk = getChunk(x / chunk_width, z / chunk_depth);
        if (chunk != null) {
            chunk.setBlock(x % chunk_width, y, z % chunk_depth, block);
        }
    }

    public @Nullable Block getBlock(int x, int y, int z) {
        Chunk chunk = getChunkIfProvided(x / chunk_width, z / chunk_depth);
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

    public void removeAll(@NotNull Predicate<? super Entity> filter) {
        entities.removeIf(filter);
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

    public void start() {
        terminated = false;
    }

    public void terminate() {
        terminated = true;
    }

    public boolean isTerminated() {
        return terminated;
    }
}
