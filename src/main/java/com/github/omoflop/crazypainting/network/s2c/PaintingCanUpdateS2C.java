package com.github.omoflop.crazypainting.network.s2c;

import com.github.omoflop.crazypainting.CrazyPainting;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PaintingCanUpdateS2C(int id) implements CustomPayload {
    public static final Id<PaintingCanUpdateS2C> ID = new Id<>(CrazyPainting.id("painting_can_update"));
    public static final PacketCodec<PacketByteBuf, PaintingCanUpdateS2C> CODEC = PacketCodec.of(PaintingCanUpdateS2C::encode, PaintingCanUpdateS2C::decode);

    private void encode(PacketByteBuf buf) {
        buf.writeInt(id);
    }

    private static PaintingCanUpdateS2C decode(PacketByteBuf buf) {
        return new PaintingCanUpdateS2C(buf.readInt());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}