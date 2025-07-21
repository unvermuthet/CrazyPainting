package com.github.omoflop.crazypainting.content;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import com.github.omoflop.crazypainting.components.PaletteColorsComponent;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CrazyComponents {
    public static final ComponentType<PaletteColorsComponent> PALETTE_COLORS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            CrazyPainting.id("palette_colors"),
            ComponentType.<PaletteColorsComponent>builder().codec(PaletteColorsComponent.CODEC).build()
    );
    public static final ComponentType<CanvasDataComponent> CANVAS_DATA = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            CrazyPainting.id("canvas_data"),
            ComponentType.<CanvasDataComponent>builder().codec(CanvasDataComponent.CODEC).build()
    );

    public static void register() {
        ComponentTooltipAppenderRegistry.addBefore(DataComponentTypes.LORE, CANVAS_DATA);
        ComponentTooltipAppenderRegistry.addAfter(DataComponentTypes.ATTRIBUTE_MODIFIERS, PALETTE_COLORS);
    }
}
