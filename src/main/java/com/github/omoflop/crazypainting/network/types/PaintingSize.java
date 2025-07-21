package com.github.omoflop.crazypainting.network.types;

import com.github.omoflop.crazypainting.items.CanvasItem;
import net.minecraft.network.PacketByteBuf;

public record PaintingSize(byte width, byte height) {

    public static PaintingSize from(CanvasItem canvasItem) {
        return new PaintingSize(canvasItem.width, canvasItem.height);
    }

    public void writeTo(PacketByteBuf buf) {
        buf.writeByte(width);
        buf.writeByte(height);
    }

    public static PaintingSize readFrom(PacketByteBuf buf) {
        return new PaintingSize(buf.readByte(), buf.readByte());
    }
}