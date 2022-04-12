package com.torpill.engine.graphics.post;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.meshes.Mesh2D;
import com.torpill.engine.graphics.shaders.bloom.BrightFilter;
import com.torpill.engine.graphics.shaders.bloom.CombineFilter;
import com.torpill.engine.graphics.shaders.bloom.RedFilter;
import com.torpill.engine.graphics.shaders.blur.HorizontalBlur;
import com.torpill.engine.graphics.shaders.blur.VerticalBlur;
import com.torpill.engine.graphics.shaders.contrast.ContrastChanger;
import com.torpill.engine.graphics.shaders.pixelate.Pixelate;
import com.torpill.engine.graphics.shaders.death.DeathFilter;
import com.torpill.engine.loader.RawLoader;
import org.jetbrains.annotations.NotNull;

public class DeathPostProcessing {

    private static final float[] POSITIONS = { 1, 1, -1, 1, -1, -1, 1, -1 };
    private static Mesh2D quad;
    private static ContrastChanger contrastChanger;
    private static RedFilter redFilter;
    private static HorizontalBlur horizontalBlur;
    private static VerticalBlur verticalBlur;
    private static CombineFilter combineFilter;
    private static Pixelate pixelate;
    private static DeathFilter deathFilter;

    public static void init(@NotNull Window window) throws Exception {
        quad = RawLoader.load2DQuad(POSITIONS);
        contrastChanger = new ContrastChanger(window, window.getWidth(), window.getHeight());
        redFilter = new RedFilter(window, window.getWidth() / 2, window.getHeight() / 2);
        horizontalBlur = new HorizontalBlur(window, window.getWidth() / 5, window.getHeight() / 5);
        verticalBlur = new VerticalBlur(window, window.getWidth() / 5, window.getHeight() / 5);
        combineFilter = new CombineFilter(window, window.getWidth(), window.getHeight());
        deathFilter = new DeathFilter(window, window.getWidth(), window.getHeight());
        pixelate = new Pixelate();
    }

    public static void resize(@NotNull Window window) {
        contrastChanger.resizeRenderer(window.getWidth(), window.getHeight());
        redFilter.resizeRenderer(window.getWidth() / 2, window.getHeight() / 2);
        horizontalBlur.resizeRenderer(window.getWidth() / 5, window.getHeight() / 5);
        verticalBlur.resizeRenderer(window.getWidth() / 5, window.getHeight() / 5);
        combineFilter.resizeRenderer(window.getWidth(), window.getHeight());
        deathFilter.resizeRenderer(window.getWidth(), window.getHeight());
    }

    public static void doPostProcessing(@NotNull Window window,int colourTexture){
        start();
        deathFilter.render(colourTexture);
        redFilter.render(deathFilter.getOutputTexture());
        horizontalBlur.render(redFilter.getOutputTexture());
        verticalBlur.render(horizontalBlur.getOutputTexture());
        combineFilter.render(deathFilter.getOutputTexture(), verticalBlur.getOutputTexture());
        contrastChanger.render(combineFilter.getOutputTexture());
        pixelate.render(contrastChanger.getOutputTexture(), window.getWidth(), window.getHeight());
        end();
    }

    public static void cleanup(){
        contrastChanger.cleanup();
        redFilter.cleanup();
        horizontalBlur.cleanup();
        verticalBlur.cleanup();
        combineFilter.cleanup();
        deathFilter.cleanup();
        pixelate.cleanup();
    }

    private static void start(){
        quad.preRender();
    }

    private static void end(){
        quad.postRender();
    }


}
