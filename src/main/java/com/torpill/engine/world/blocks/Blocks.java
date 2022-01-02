package com.torpill.engine.world.blocks;

import com.torpill.engine.graphics.Material;
import com.torpill.engine.loader.TextureCache;

public class Blocks {

    public static Block GRASS;
    public static Block DIRT;

    public static void load() throws Exception {
        Material grass_material = new Material();
        grass_material.setTexture(TextureCache.getInstance().getTexture("textures/blocks/grass.png"));
        GRASS = new Block(grass_material);

        Material dirt_material = new Material();
        dirt_material.setTexture(TextureCache.getInstance().getTexture("textures/blocks/dirt.png"));
        DIRT = new Block(dirt_material);
    }
}
