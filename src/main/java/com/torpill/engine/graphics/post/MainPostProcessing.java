package com.torpill.engine.graphics.post;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.meshes.Mesh2D;
import com.torpill.engine.graphics.shaders.bloom.BrightFilter;
import com.torpill.engine.graphics.shaders.bloom.CombineFilter;
import com.torpill.engine.graphics.shaders.blur.HorizontalBlur;
import com.torpill.engine.graphics.shaders.blur.VerticalBlur;
import com.torpill.engine.graphics.shaders.contrast.ContrastChanger;
import com.torpill.engine.graphics.shaders.main.SuperposeFilter;
import com.torpill.engine.graphics.shaders.pixelate.Pixelate;
import com.torpill.engine.loader.RawLoader;
import org.jetbrains.annotations.NotNull;

public class MainPostProcessing {

	private static final float[] POSITIONS = { 1, 1, -1, 1, -1, -1, 1, -1 };
	private static Mesh2D quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur horizontalBlur;
	private static VerticalBlur verticalBlur;
	private static CombineFilter combineFilter;
	private static SuperposeFilter superposeFilter;
	private static Pixelate pixelate;

	public static void init(@NotNull Window window) throws Exception {
		quad = RawLoader.load2DQuad(POSITIONS);
		contrastChanger = new ContrastChanger(window, window.getWidth(), window.getHeight());
		brightFilter = new BrightFilter(window, window.getWidth() / 2, window.getHeight() / 2);
		horizontalBlur = new HorizontalBlur(window, window.getWidth() / 5, window.getHeight() / 5);
		verticalBlur = new VerticalBlur(window, window.getWidth() / 5, window.getHeight() / 5);
		combineFilter = new CombineFilter(window, window.getWidth(), window.getHeight());
		pixelate = new Pixelate(window, window.getWidth(), window.getHeight());
		superposeFilter = new SuperposeFilter();
	}

	public static void resize(@NotNull Window window) {
		contrastChanger.resizeRenderer(window.getWidth(), window.getHeight());
		brightFilter.resizeRenderer(window.getWidth() / 2, window.getHeight() / 2);
		horizontalBlur.resizeRenderer(window.getWidth() / 5, window.getHeight() / 5);
		verticalBlur.resizeRenderer(window.getWidth() / 5, window.getHeight() / 5);
		combineFilter.resizeRenderer(window.getWidth(), window.getHeight());
		pixelate.resizeRenderer(window.getWidth(), window.getHeight());
	}

	public static void doPostProcessing(@NotNull Window window, int colourTexture, int hudColourTexture){
		start();
		brightFilter.render(colourTexture);
		horizontalBlur.render(brightFilter.getOutputTexture());
		verticalBlur.render(horizontalBlur.getOutputTexture());
		pixelate.render(verticalBlur.getOutputTexture(), window.getWidth(), window.getHeight());
		combineFilter.render(colourTexture, pixelate.getOutputTexture());
		contrastChanger.render(combineFilter.getOutputTexture());

		superposeFilter.render(contrastChanger.getOutputTexture(), hudColourTexture);
		end();
	}
	
	public static void cleanup(){
		contrastChanger.cleanup();
		brightFilter.cleanup();
		horizontalBlur.cleanup();
		verticalBlur.cleanup();
		combineFilter.cleanup();
		pixelate.cleanup();
		superposeFilter.cleanup();
	}
	
	private static void start(){
		quad.preRender();
	}
	
	private static void end(){
		quad.postRender();
	}


}
