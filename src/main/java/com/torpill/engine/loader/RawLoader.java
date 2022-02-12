package com.torpill.engine.loader;

import com.torpill.engine.graphics.meshes.Mesh2D;

public class RawLoader {

    public static Mesh2D load2DQuad(float[] positions) {
        int[] indices = { 0, 1, 2, 2, 3, 0 };
        return new Mesh2D(positions, indices);
    }
}
