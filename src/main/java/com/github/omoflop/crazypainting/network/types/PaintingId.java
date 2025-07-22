package com.github.omoflop.crazypainting.network.types;

import net.minecraft.network.PacketByteBuf;

import java.util.Objects;

public record PaintingId(int value) {
    public void writeTo(PacketByteBuf buf) {
        buf.writeInt(value);
    }

    public static PaintingId readFrom(PacketByteBuf buf) {
        return new PaintingId(buf.readInt());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaintingId that = (PaintingId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
