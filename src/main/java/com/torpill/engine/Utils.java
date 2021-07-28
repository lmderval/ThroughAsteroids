package com.torpill.engine;

import java.io.*;
import java.lang.reflect.Array;
import java.util.List;

public class Utils {

    public static String loadSource(String path) {
        InputStream is = Utils.class.getResourceAsStream(path);
        assert is != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(line -> {
            sb.append(line).append("\n");
        });
        return sb.toString();
    }

    public static float[] listToArrayFloat(List<Float> list) {
        final float[] array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static int[] listToArrayInt(List<Integer> list) {
        final int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
