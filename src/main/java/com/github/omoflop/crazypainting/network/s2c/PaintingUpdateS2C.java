package com.github.omoflop.crazypainting.network.s2c;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PaintingUpdateS2C(int id, PaintingData data) implements CustomPayload {
    public static final Id<PaintingUpdateS2C> ID = new Id<>(CrazyPainting.id("painting_update"));
    public static final PacketCodec<PacketByteBuf, PaintingUpdateS2C> CODEC = PacketCodec.of(PaintingUpdateS2C::encode, PaintingUpdateS2C::decode);

    private void encode(PacketByteBuf buf) {
        buf.writeInt(id);
        data.writeTo(buf);
    }

    private static PaintingUpdateS2C decode(PacketByteBuf buf) {
        return new PaintingUpdateS2C(buf.readInt(), PaintingData.readFrom(buf));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
