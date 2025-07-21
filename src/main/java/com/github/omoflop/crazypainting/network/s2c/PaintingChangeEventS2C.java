package com.github.omoflop.crazypainting.network.s2c;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.ChangeId;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PaintingChangeEventS2C(ChangeId change, PaintingData data) implements CustomPayload {
    public static final Id<PaintingChangeEventS2C> ID = new Id<>(CrazyPainting.id("painting_change_s2c"));
    public static final PacketCodec<PacketByteBuf, PaintingChangeEventS2C> CODEC = PacketCodec.of(PaintingChangeEventS2C::encode, PaintingChangeEventS2C::decode);

    private void encode(PacketByteBuf buf) {
        change.writeTo(buf);
        data.writeTo(buf);
    }

    private static PaintingChangeEventS2C decode(PacketByteBuf buf) {
        return new PaintingChangeEventS2C(ChangeId.readFrom(buf), PaintingData.readFrom(buf));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
