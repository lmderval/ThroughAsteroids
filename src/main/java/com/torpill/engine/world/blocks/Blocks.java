package com.torpill.engine.world.blocks;

import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.loader.TextureCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blocks {

    public static Block WALL;
    public static Block WALL_BRICK;
    public static Block WALL_DUST;
    public static Block WALL_EDGE;
    public static Block WALL_ORE;

    public static List<Block> blocks;
    public static Map<Block, Integer> map;

    public static void load() throws Exception {
        blocks = new ArrayList<>();

        Material wallMat = new Material(0.0f);
        wallMat.setTexture(TextureCache.getInstance().getTexture("textures/blocks/wall.png"));
        WALL = new Block(wallMat);

        Material wallBrickMat = new Material(0.0f);
        wallBrickMat.setTexture(TextureCache.getInstance().getTexture("textures/blocks/wall_brick.png"));
        wallBrickMat.setLightMap(TextureCache.getInstance().getTexture("textures/lights/blocks/wall_brick.png"));
        WALL_BRICK = new Block(wallBrickMat);

        Material wallDustMat = new Material(0.0f);
        wallDustMat.setTexture(TextureCache.getInstance().getTexture("textures/blocks/wall_dust.png"));
        WALL_DUST = new Block(wallDustMat);

        Material wallEdgeMat = new Material(0.0f);
        wallEdgeMat.setTexture(TextureCache.getInstance().getTexture("textures/blocks/wall_edge.png"));
        WALL_EDGE = new Block(wallEdgeMat);

        Material wallOreMat = new Material(0.0f);
        wallOreMat.setTexture(TextureCache.getInstance().getTexture("textures/blocks/wall_ore.png"));
        wallOreMat.setLightMap(TextureCache.getInstance().getTexture("textures/lights/blocks/wall_ore.png"));
        WALL_ORE = new Block(wallOreMat);

        blocks.add(WALL);
        blocks.add(WALL_BRICK);
        blocks.add(WALL_DUST);
        blocks.add(WALL_EDGE);
        blocks.add(WALL_ORE);

        genMap();
    }

    public static void genMap() {
        map = new HashMap<>();
        for (int i = 0; i < Blocks.blocks.size(); i ++) {
            map.put(Blocks.blocks.get(i), i);
        }
    }
}
