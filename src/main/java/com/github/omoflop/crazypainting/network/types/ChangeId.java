package com.github.omoflop.crazypainting.network.types;

import net.minecraft.network.PacketByteBuf;

public record ChangeId(int id, byte[] key) {
    public void writeTo(PacketByteBuf buf) {
        buf.writeInt(id);
        buf.writeBytes(key);
    }

    public static ChangeId readFrom(PacketByteBuf buf) {
        return new ChangeId(buf.readInt(), buf.readByteArray());
    }
}
