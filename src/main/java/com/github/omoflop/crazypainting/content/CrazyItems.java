package com.github.omoflop.crazypainting.content;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.Identifiable;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.items.EaselItem;
import com.github.omoflop.crazypainting.items.PaletteItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrazyItems {
    public static final List<CanvasItem> allCanvases = new ArrayList<>();
    private static HashMap<Identifier, Item> deferredRegistry = new HashMap<>();

    public static final EaselItem EASEL_ITEM = register(new EaselItem("easel"));
    public static final PaletteItem PALETTE_ITEM = register(new PaletteItem("palette"));

    public static final CanvasItem SMALL_CANVAS_ITEM    = registerCanvas("small_canvas", 1, 1);
    public static final CanvasItem LONG_CANVAS_ITEM     = registerCanvas("long_canvas", 2, 1);
    public static final CanvasItem TALL_CANVAS_ITEM     = registerCanvas("tall_canvas", 1, 2);
    public static final CanvasItem BIG_CANVAS_ITEM      = registerCanvas("big_canvas", 2, 2);
    public static final CanvasItem LARGE_CANVAS_ITEM    = registerCanvas("large_canvas", 3, 3);
    public static final CanvasItem LONGER_CANVAS_ITEM    = registerCanvas("longer_canvas", 3, 1);
    public static final CanvasItem TALLER_CANVAS_ITEM    = registerCanvas("taller_canvas", 1, 3);

    public static void register() {
        for (Map.Entry<Identifier, Item> entry : deferredRegistry.entrySet()) {
            Registry.register(Registries.ITEM, entry.getKey(), entry.getValue());
        }
        deferredRegistry.clear();
        deferredRegistry = null;

        ItemGroup group = FabricItemGroup.builder()
                .icon(PaletteItem::createFullPalette)
                .displayName(Text.of("Crazy Painting"))
                .entries(((displayContext, entries) -> {
                    entries.add(EASEL_ITEM);
                    entries.add(PALETTE_ITEM);
                    for (CanvasItem canvas : allCanvases) {
                        entries.add(new ItemStack(canvas));
                    }
                }))
                .build();
        Registry.register(Registries.ITEM_GROUP, CrazyPainting.id("main"), group);
    }

    private static <T extends Item & Identifiable> T register(T item) {
        deferredRegistry.put(item.getId(), item);
        return item;
    }

    private static CanvasItem registerCanvas(String name, int width, int height) {
        CanvasItem item = register(new CanvasItem(name, (byte) width, (byte) height));
        allCanvases.add(item);

        return item;
    }
}
