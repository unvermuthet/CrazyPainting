package com.github.omoflop.crazypainting.network.s2c;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record NewPaintingS2CPacket(int easelId, int paintingId, PaintingSize size) implements CustomPayload {
    public static final Id<NewPaintingS2CPacket> ID = new Id<>(CrazyPainting.id("new_painting"));
    public static final PacketCodec<PacketByteBuf, NewPaintingS2CPacket> CODEC = PacketCodec.of(NewPaintingS2CPacket::encode, NewPaintingS2CPacket::decode);

    private void encode(PacketByteBuf buf) {
        buf.writeInt(easelId);
        buf.writeInt(paintingId);
        size.writeTo(buf);
    }

    private static NewPaintingS2CPacket decode(PacketByteBuf buf) {
        return new NewPaintingS2CPacket(buf.readInt(), buf.readInt(), PaintingSize.readFrom(buf));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
