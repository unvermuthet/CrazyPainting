package com.github.omoflop.crazypainting.network.c2s;

import com.github.omoflop.crazypainting.CrazyPainting;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RequestPaintingC2S(int id) implements CustomPayload {
    public static final Id<RequestPaintingC2S> ID = new Id<>(CrazyPainting.id("request_painting"));
    public static final PacketCodec<PacketByteBuf, RequestPaintingC2S> CODEC = PacketCodec.of(RequestPaintingC2S::encode, RequestPaintingC2S::decode);

    private void encode(PacketByteBuf buf) {
        buf.writeInt(id);
    }

    private static RequestPaintingC2S decode(PacketByteBuf buf) {
        return new RequestPaintingC2S(buf.readInt());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
