package com.torpill.engine.hud;

import com.torpill.engine.world.blocks.Block;
import org.jetbrains.annotations.NotNull;

public class HudBlock extends HudItem {

    public HudBlock(@NotNull Block block) {
        super(block.getMesh(), true);
        rotation.set(30f, -45f, 0f);
    }
}
