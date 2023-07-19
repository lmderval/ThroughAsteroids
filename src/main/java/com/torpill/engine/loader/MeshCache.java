package com.torpill.engine.loader;

import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.graphics.meshes.Mesh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MeshCache {

    private static MeshCache INSTANCE;

    public static Mesh SHIP;
    public static Mesh TOWER;
    public static Mesh LASER;

    public static Mesh HUD_CROSS;

    public static void load() throws Exception {
        SHIP = MeshCache.getInstance().getStaticMeshes("models/entities/ship.obj", "textures/entities")[0];
        SHIP.getMaterial().setTexture(TextureCache.getInstance().getTexture("textures/entities/ship.png"));
        SHIP.getMaterial().setLightMap(TextureCache.getInstance().getTexture("textures/lights/entities/ship.png"));

        TOWER = MeshCache.getInstance().getStaticMeshes("models/entities/tower.obj", "textures/entities")[0];
        TOWER.getMaterial().setTexture(TextureCache.getInstance().getTexture("textures/entities/tower.png"));
        TOWER.getMaterial().setLightMap(TextureCache.getInstance().getTexture("textures/lights/entities/tower.png"));

        LASER = MeshCache.getInstance().getStaticMeshes("models/entities/laser.obj", null)[0];
        Material laserMat = new Material(0f, 0.4f);
        LASER.setMaterial(laserMat);

        HUD_CROSS = MeshCache.getInstance().getStaticMeshes("models/hud/cross.obj", null)[0];
        Material crossMat = new Material(0f);
        crossMat.setTexture(TextureCache.getInstance().getTexture("textures/hud/cross.png"));
        HUD_CROSS.setMaterial(crossMat);
    }

    private final Map<String, Mesh[]> meshes_map = new HashMap<>();

    public static MeshCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MeshCache();
        }
        return INSTANCE;
    }

    public Mesh[] getStaticMeshes(@NotNull String path) throws Exception {
        return getStaticMeshes(path, null);
    }

    public Mesh[] getStaticMeshes(@NotNull String path, @Nullable String textures_dir) throws Exception {
        Mesh[] mesh = meshes_map.get(path);
        if (mesh == null) {
            mesh = StaticMeshesLoader.load(path, textures_dir);
            meshes_map.put(path, mesh);
        }
        return mesh;
    }

    public void cleanup() {
        meshes_map.forEach((path, meshes) -> {
            for (Mesh mesh : meshes) {
                mesh.cleanup();
            }
        });
    }
}
