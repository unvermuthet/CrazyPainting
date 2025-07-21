package com.github.omoflop.crazypainting.client.itemproperties;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
 import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


@Environment(EnvType.CLIENT)
public record PaletteHasColorsProperty(byte atLeast) implements BooleanProperty {
    public static final Identifier ID = CrazyPainting.id("palette_has_colors");
    public static final MapCodec<PaletteHasColorsProperty> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.BYTE.optionalFieldOf("at_least", (byte) 0).forGetter(PaletteHasColorsProperty::atLeast)
                    )
                    .apply(instance, PaletteHasColorsProperty::new)
    );

    @Override
    public MapCodec<? extends BooleanProperty> getCodec() {
        return CODEC;
    }

    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        if (!stack.hasChangedComponent(CrazyComponents.PALETTE_COLORS)) return atLeast == 0;

        return atLeast <= Objects.requireNonNull(stack.get(CrazyComponents.PALETTE_COLORS)).colors().size();
    }
}
