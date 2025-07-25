package com.github.omoflop.crazypainting.network.c2s;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.ChangeKey;
import com.github.omoflop.crazypainting.network.types.PaintingId;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SignPaintingC2S (ChangeKey changeKey, PaintingId id, int easelEntityId, String title) implements CustomPayload {
    public static final Id<SignPaintingC2S> ID = new Id<>(CrazyPainting.id("painting_sign"));
    public static final PacketCodec<PacketByteBuf, SignPaintingC2S> CODEC = PacketCodec.of(SignPaintingC2S::encode, SignPaintingC2S::decode);

    private void encode(PacketByteBuf buf) {
        changeKey.writeTo(buf);
        id.writeTo(buf);
        buf.writeInt(easelEntityId);
        buf.writeString(title, 32);
    }

    private static SignPaintingC2S decode(PacketByteBuf buf) {
        return new SignPaintingC2S(
                ChangeKey.readFrom(buf),
                PaintingId.readFrom(buf),
                buf.readInt(),
                buf.readString(32)
        );
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
