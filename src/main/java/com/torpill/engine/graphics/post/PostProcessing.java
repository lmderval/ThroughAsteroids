package com.torpill.engine.graphics.post;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.Mesh2D;
import com.torpill.engine.graphics.blur.HorizontalBlur;
import com.torpill.engine.graphics.blur.VerticalBlur;
import com.torpill.engine.loader.RawLoader;

public class PostProcessing {

	private static final float[] POSITIONS = { 1, 1, -1, 1, -1, -1, 1, -1 };
	private static Mesh2D quad;
	private static ContrastChanger contrastChanger;
	private static HorizontalBlur horizontalBlur;
	private static VerticalBlur verticalBlur;

	public static void init() throws Exception {
		quad = RawLoader.load2DQuad(POSITIONS);
		contrastChanger = new ContrastChanger();
		horizontalBlur = new HorizontalBlur(Window.getWidth() / 2, Window.getHeight() / 2);
		verticalBlur = new VerticalBlur(Window.getWidth() / 2, Window.getHeight() / 2);
	}
	
	public static void doPostProcessing(int colourTexture){
		start();
		horizontalBlur.render(colourTexture);
		verticalBlur.render(horizontalBlur.getOutputTexture());
		contrastChanger.render(verticalBlur.getOutputTexture());
		end();
	}
	
	public static void cleanup(){
		contrastChanger.cleanup();
		horizontalBlur.cleanup();
		verticalBlur.cleanup();
	}
	
	private static void start(){
		quad.preRender();
	}
	
	private static void end(){
		quad.postRender();
	}


}
