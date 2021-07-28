package com.torpill.engine.loader;

import com.torpill.engine.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private static TextureCache INSTANCE;

    private final Map<String, Texture> textures_map = new HashMap<>();

    public static TextureCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextureCache();
        }
        return INSTANCE;
    }

    public Texture getTexture(String path) throws Exception {
        Texture texture = textures_map.get(path);
        if (texture == null) {
            texture = new Texture(path);
            textures_map.put(path, texture);
        }
        return texture;
    }

    public void cleanup() {
        textures_map.forEach((path, texture) -> {
            texture.cleanup();
        });
    }
}
