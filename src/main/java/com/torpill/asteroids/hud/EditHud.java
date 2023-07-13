package com.torpill.asteroids.hud;

import com.torpill.engine.Window;
import com.torpill.engine.hud.HudBlock;
import com.torpill.engine.hud.HudItem;
import com.torpill.engine.hud.IHud;
import com.torpill.engine.world.blocks.Blocks;
import org.jetbrains.annotations.NotNull;

import static com.torpill.engine.loader.MeshCache.HUD_CROSS;

public class EditHud implements IHud {

    private HudItem cross;
    private HudBlock[] blocks;

    private HudItem[] items;

    private int currentBlock = 0;

    public EditHud() {

    }

    @Override
    public void load() {
        cross = new HudItem(HUD_CROSS);
        cross.setColor(0.2f, 0.2f, 0.2f, 0f);
        blocks = new HudBlock[Blocks.blocks.size()];
        items = new HudItem[1 + blocks.length];
        items[0] = cross;
        Blocks.map.forEach((key, value) -> {
            blocks[value] = new HudBlock(key);
            blocks[value].setColor(0.25f, 0.25f, 0.25f, 1f);
            blocks[value].setScale(0.75f);
        });
        System.arraycopy(blocks, 0, items, 1, blocks.length);
    }

    @Override
    public void resize(@NotNull Window window) {
        for (int i = 0; i < blocks.length; i++) {
            blocks[i].setPosition(7f * (i - blocks.length / 2f) / 5f, -7f);
        }
    }

    @Override
    public HudItem[] getItems() {
        return items;
    }

    public void setCurrentBlock(int currentBlock) {
        blocks[this.currentBlock].setColor(0.25f, 0.25f, 0.25f, 1f);
        blocks[this.currentBlock].setScale(0.75f);
        blocks[currentBlock].setColor(1f, 1f, 1f, 1f);
        blocks[currentBlock].setScale(1f);
        this.currentBlock = currentBlock;
    }
}
