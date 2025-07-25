package com.github.omoflop.crazypainting.components;

import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.Item;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ParsedSelector;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;


public record CanvasDataComponent(int id, boolean glow, String signedBy, String title, byte generation) implements TooltipAppender {
    public static final Codec<CanvasDataComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("id").forGetter(CanvasDataComponent::id),
            Codec.BOOL.fieldOf("glow").forGetter(CanvasDataComponent::glow),
            Codec.STRING.fieldOf("signed_by").forGetter(CanvasDataComponent::signedBy),
            Codec.STRING.fieldOf("title").forGetter(CanvasDataComponent::title),
            Codec.BYTE.fieldOf("generation").forGetter(CanvasDataComponent::generation)
    ).apply(builder, CanvasDataComponent::new));

    public static final CanvasDataComponent DEFAULT = new CanvasDataComponent(-1, false, "", CanvasItem.UNTITLED, (byte) 0);


    public static CanvasDataComponent withId(@Nullable CanvasDataComponent data, int id) {
        if (data == null) data = DEFAULT;
        return new CanvasDataComponent(id, data.glow, data.signedBy, data.title, data.generation);
    }

    public CanvasDataComponent withId(int id) {
        return new CanvasDataComponent(id, glow, signedBy, title, generation);
    }

    public CanvasDataComponent withGlow(boolean glow) {
        return new CanvasDataComponent(id, glow, signedBy, title, generation);
    }

    public CanvasDataComponent withSignedBy(String signedBy) {
        return new CanvasDataComponent(id, glow, signedBy, title, generation);
    }

    public CanvasDataComponent withTitle(String title) {
        return new CanvasDataComponent(id, glow, signedBy, title, generation);
    }

    public CanvasDataComponent withGeneration(byte generation) {
        return new CanvasDataComponent(id, glow, signedBy, title, generation);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        CanvasDataComponent data = components.get(CrazyComponents.CANVAS_DATA);
        assert data != null;
        if (type.isAdvanced()) {
            textConsumer.accept(Text.literal("Canvas ID: " + data.id).formatted(Formatting.GRAY));
        }

        if (data.signedBy != null && !data.signedBy.isEmpty()) {
            appendSignedByTooltip(data, textConsumer);
        }
    }

    private void appendSignedByTooltip(CanvasDataComponent data, Consumer<Text> textConsumer) {
        var selector = ParsedSelector.parse(data.signedBy);

        var arg = Text.literal("Unknown");
        if (selector.isSuccess()) {
            arg = Text.selector(selector.getOrThrow(), Optional.empty());
        }

        textConsumer.accept(Text.translatable("item.crazypainting.canvas.tooltip.signed", arg).formatted(Formatting.YELLOW));

        if (this.generation >= 0)
            textConsumer.accept(Text.translatable("book.generation." + this.generation).formatted(Formatting.GRAY));
    }
}
