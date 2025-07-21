package com.github.omoflop.crazypainting.components;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public record PaletteColorsComponent(List<Integer> colors) implements TooltipAppender {

    public static final Codec<PaletteColorsComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.list(Codec.INT).fieldOf("colors").forGetter(PaletteColorsComponent::colors)
    ).apply(builder, PaletteColorsComponent::new));

    public static PaletteColorsComponent empty() {
        return new PaletteColorsComponent(new ArrayList<>());
    }

    public static void sort(ArrayList<Integer> palette) {
        List<Integer> colors = Arrays.stream(CrazyPainting.VANILLA_COLOR_ORDER).boxed().toList();
        palette.sort((a, b) -> {
            int aIndex = 10000;
            int bIndex = 10000;
            if (colors.contains(a)) {
                aIndex = colors.indexOf(a);
                bIndex = colors.indexOf(b);
            }
            return aIndex - bIndex;
        });
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        PaletteColorsComponent data = components.get(CrazyComponents.PALETTE_COLORS);
        assert data != null;


        if (data.colors == null || data.colors.isEmpty()) {
            textConsumer.accept(Text.translatable("item.crazypainting.palette.tooltip.empty").formatted(Formatting.GRAY, Formatting.ITALIC));
            return;
        }

        MutableText text = (Text.translatable("item.crazypainting.palette.tooltip.colors").append(": ").formatted(Formatting.BLUE));
        for (int i = 0; i < data.colors.size(); i++) {
            int color = data.colors.get(i);
            if (color == CrazyPainting.TRANSPARENT) {
                text.append(Text.translatable("item.crazypainting.palette.tooltip.color_entry.transparent").formatted(Formatting.WHITE));
            } else {
                text.append(Text.translatable("item.crazypainting.palette.tooltip.color_entry").withColor(data.colors.get(i)));

            }
        }
        textConsumer.accept(text);
    }
}
