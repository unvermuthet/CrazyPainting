package com.github.omoflop.crazypainting.client.screens.editor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;

public final class BrushType {
    private static final HashMap<String, HashMap<String, BrushType>> BRUSH_CATEGORIES = new HashMap<>();


    private boolean[][] pattern;

    private BrushType(boolean[][] pattern) {
        this.pattern = pattern;
    }

    public static void register(String category, String name, String... pattern) {
        if (!BRUSH_CATEGORIES.containsKey(category)) {
            BRUSH_CATEGORIES.put(category, new HashMap<>());
        }

        BRUSH_CATEGORIES.get(category).put(name, new BrushType(pattern));
    }

    static {
        register("Square", "1x1", "",
                "x"
                );
        register("Square", "2x2", "",
                "xx",
                "xx"
                );
        register("Square", "3x3", "",
                "xxx",
                "xxx",
                "xxx"
                );
        register("Square", "4x4", "",
                "xxxx",
                "xxxx",
                "xxxx",
                "xxxx"
                );

        register("Circle", "1x1", "",
                "x"
                );

        register("Circle", "2x2", "",
                "xx",
                "xx"
                );
        register("Circle", "3x3", "",
                " x ",
                "xxx",
                " x "
                );
        register("Circle", "4x4", "",
                " xx ",
                "xxxx",
                "xxxx",
                " xx "
                );
        register("Circle", "5x5", "",
                " xxx ",
                "xxxxx",
                "xxxxx",
                "xxxxx",
                " xxx "
                );
        register("Circle", "6x6", "",
                " xxxx ",
                "xxxxxx",
                "xxxxxx",
                "xxxxxx",
                "xxxxxx",
                " xxxx "
                );
        register("Circle", "7x7", "",
                " xxxx ",
                "xxxxxx",
                "xxxxxx",
                "xxxxxx",
                "xxxxxx",
                " xxxx "
                );
    }
}
