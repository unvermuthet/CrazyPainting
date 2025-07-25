package com.github.omoflop.crazypainting;

import com.github.omoflop.crazypainting.content.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class CrazyPainting implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("crazy_painting");
    public static final boolean SHOW_NETWORK_LOGS = true;

    public static final Item GLOW_ITEM = Items.GLOW_INK_SAC;
    public static final Item UNGLOW_ITEM = Items.INK_SAC;

    public static final int WHITE = 0xfff9fffe;
    public static final int LIGHT_GRAY = 0xff9d9d97;
    public static final int GRAY = 0xff474f52;
    public static final int BLACK = 0xff1d1d21;
    public static final int BROWN = 0xff835432;
    public static final int RED = 0xffb02e26;
    public static final int ORANGE = 0xfff9801d;
    public static final int YELLOW = 0xfffed83d;
    public static final int LIME = 0xff80c71f;
    public static final int GREEN = 0xff5e7c16;
    public static final int CYAN = 0xff169c9c;
    public static final int LIGHT_BLUE = 0xff3ab3da;
    public static final int BLUE = 0xff3c44aa;
    public static final int PURPLE = 0xff8932b8;
    public static final int MAGENTA = 0xffc74ebd;
    public static final int PINK = 0xfff38baa;
    public static final int TRANSPARENT = 0x00000000;

    public static final HashMap<Item, Integer> VANILLA_COLORS = new HashMap<>() {{
        put(Items.WHITE_DYE, WHITE);
        put(Items.LIGHT_GRAY_DYE, LIGHT_GRAY);
        put(Items.GRAY_DYE, GRAY);
        put(Items.BLACK_DYE, BLACK);
        put(Items.BROWN_DYE, BROWN);
        put(Items.RED_DYE, RED);
        put(Items.ORANGE_DYE, ORANGE);
        put(Items.YELLOW_DYE, YELLOW);
        put(Items.LIME_DYE, LIME);
        put(Items.GREEN_DYE, GREEN);
        put(Items.CYAN_DYE, CYAN);
        put(Items.LIGHT_BLUE_DYE, LIGHT_BLUE);
        put(Items.BLUE_DYE, BLUE);
        put(Items.PURPLE_DYE, PURPLE);
        put(Items.MAGENTA_DYE, MAGENTA);
        put(Items.PINK_DYE, PINK);
        put(Items.GLASS_PANE, TRANSPARENT);
    }};

    public static final int[] VANILLA_COLOR_ORDER = new int[] {
            WHITE, LIGHT_GRAY, GRAY, BLACK,
            BROWN, RED, ORANGE,
            YELLOW, LIME, GREEN,
            CYAN, LIGHT_BLUE, BLUE,
            PURPLE, MAGENTA, PINK,
            TRANSPARENT
    };

    @Override
    public void onInitialize() {
        CrazyNetworking.register();
        CrazyComponents.register();
        CrazyItems.register();
        CrazyEntities.register();
        CrazyRecipes.register();
    }

    public static Identifier id(String path) {
        return Identifier.of("crazypainting", path);
    }
}
