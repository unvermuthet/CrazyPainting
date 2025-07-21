package com.github.omoflop.crazypainting.network.types;

import com.github.omoflop.crazypainting.content.CrazyNetworking;
import net.minecraft.network.PacketByteBuf;

import java.security.SecureRandom;

public record ChangeId(int id, byte[] key) {
    public void writeTo(PacketByteBuf buf) {
        buf.writeInt(id);
        CrazyNetworking.writeByteArray(buf, key);
    }

    public static ChangeId readFrom(PacketByteBuf buf) {
        return new ChangeId(buf.readInt(), CrazyNetworking.readByteArray(buf));
    }

    public static ChangeId create(int id) {
        byte[] key = new byte[16];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(key);
        return new ChangeId(id, key);
    }
}
