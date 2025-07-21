package com.github.omoflop.crazypainting.network.s2c;

import com.github.omoflop.crazypainting.CrazyPainting;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record OpenPaintingS2CPacket(int easelId, boolean edit) implements CustomPayload {
    public static final Id<OpenPaintingS2CPacket> ID = new Id<>(CrazyPainting.id("open_painting"));
    public static final PacketCodec<PacketByteBuf, OpenPaintingS2CPacket> CODEC = PacketCodec.of(OpenPaintingS2CPacket::encode, OpenPaintingS2CPacket::decode);
    public static final CustomPayload.Type<PacketByteBuf, OpenPaintingS2CPacket> PAYLOAD_TYPE = new CustomPayload.Type<>(ID, CODEC);

    private void encode(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(easelId);
        packetByteBuf.writeBoolean(edit);
    }

    private static OpenPaintingS2CPacket decode(PacketByteBuf packetByteBuf) {
        return new OpenPaintingS2CPacket(packetByteBuf.readInt(), packetByteBuf.readBoolean());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
