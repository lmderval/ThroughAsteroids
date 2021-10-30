package com.torpill.asteroids.gui;

import com.torpill.asteroids.gui.demo.Calculator;
import com.torpill.asteroids.gui.demo.Demo;
import com.torpill.asteroids.gui.demo.NkTest;
import com.torpill.engine.Window;
import com.torpill.engine.gui.Nuklear;
import com.torpill.engine.gui.NuklearScene;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkColorf;

public class NkMainScene implements NuklearScene {

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

        demo.layout(nk.getContext(), 50, 50);
        calc.layout(nk.getContext(), 300, 50);
        nkTest.layout(nk.getContext(), 500, 50);
    }
}
