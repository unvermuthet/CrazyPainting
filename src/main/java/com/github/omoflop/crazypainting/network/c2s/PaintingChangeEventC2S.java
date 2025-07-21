package com.github.omoflop.crazypainting.network.c2s;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.ChangeId;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PaintingChangeEventC2S(ChangeId change, PaintingData data) implements CustomPayload {
    public static final Id<PaintingChangeEventC2S> ID = new Id<>(CrazyPainting.id("painting_change_c2s"));
    public static final PacketCodec<PacketByteBuf, PaintingChangeEventC2S> CODEC = PacketCodec.of(PaintingChangeEventC2S::encode, PaintingChangeEventC2S::decode);

    private void encode(PacketByteBuf buf) {
        change.writeTo(buf);
        data.writeTo(buf);
    }

    private static PaintingChangeEventC2S decode(PacketByteBuf buf) {
        return new PaintingChangeEventC2S(ChangeId.readFrom(buf), PaintingData.readFrom(buf));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
