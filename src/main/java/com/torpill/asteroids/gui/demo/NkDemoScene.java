package com.torpill.asteroids.gui.demo;

import com.torpill.engine.Window;
import com.torpill.engine.gui.Nuklear;
import com.torpill.engine.gui.NuklearScene;
import org.jetbrains.annotations.NotNull;
import org.joml.Random;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkColorf;

public class NkDemoScene implements NuklearScene {

    private final Random rand = new Random();

    private final Demo demo = new Demo();
    private final Calculator calc = new Calculator();
    private final NkTest nkTest = new NkTest();

    @Override
    public void update(@NotNull Window window, @NotNull Nuklear nk) {
        NkColorf bg = demo.background;
        NkColor grey = NkColor.create().set((byte) 0x38, (byte) 0x38, (byte) 0x38, (byte) 0xFF);

        nk.getContext().style().window().fixed_background().data().color().set(grey);
        nk.getContext().style().window().border(4f);
        nk.getContext().style().window().header().active().data().color().set(
                (byte) (bg.r() * 0xFF),
                (byte) (bg.g() * 0xFF),
                (byte) (bg.b() * 0xFF),
                (byte) (bg.a() * 0xFF)
        );

        int offX = rand.nextInt(9) - 4;
        int offY = rand.nextInt(9) - 4;
        demo.layout(nk.getContext(), window.getFramebufferWidth() - 280 + offX, 50 + offY);
        calc.layout(nk.getContext(), 300, 50);
        nkTest.layout(nk.getContext(), 500, 50);
    }
}
