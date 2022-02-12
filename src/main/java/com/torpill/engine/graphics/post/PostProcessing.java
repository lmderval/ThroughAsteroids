package com.torpill.engine.graphics.post;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.meshes.Mesh2D;
import com.torpill.engine.graphics.shaders.bloom.BrightFilter;
import com.torpill.engine.graphics.shaders.bloom.CombineFilter;
import com.torpill.engine.graphics.shaders.blur.HorizontalBlur;
import com.torpill.engine.graphics.shaders.blur.VerticalBlur;
import com.torpill.engine.graphics.shaders.contrast.ContrastChanger;
import com.torpill.engine.loader.RawLoader;

public class PostProcessing {

	private static final float[] POSITIONS = { 1, 1, -1, 1, -1, -1, 1, -1 };
	private static Mesh2D quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur horizontalBlur;
	private static VerticalBlur verticalBlur;
	private static CombineFilter combineFilter;

	public static void init() throws Exception {
		quad = RawLoader.load2DQuad(POSITIONS);
		contrastChanger = new ContrastChanger();
		brightFilter = new BrightFilter(Window.getWidth() / 2, Window.getHeight() / 2);
		horizontalBlur = new HorizontalBlur(Window.getWidth() / 5, Window.getHeight() / 5);
		verticalBlur = new VerticalBlur(Window.getWidth() / 5, Window.getHeight() / 5);
		combineFilter = new CombineFilter();
	}
	
	public static void doPostProcessing(int colourTexture){
		start();
		brightFilter.render(colourTexture);
		horizontalBlur.render(brightFilter.getOutputTexture());
		verticalBlur.render(horizontalBlur.getOutputTexture());
//		contrastChanger.render(verticalBlur.getOutputTexture());
		combineFilter.render(colourTexture, verticalBlur.getOutputTexture());
		end();
	}
	
	public static void cleanup(){
		contrastChanger.cleanup();
		brightFilter.cleanup();
		horizontalBlur.cleanup();
		verticalBlur.cleanup();
		combineFilter.cleanup();
	}
	
	private static void start(){
		quad.preRender();
	}
	
	private static void end(){
		quad.postRender();
	}


}
