package com.github.omoflop.crazypainting.network.s2c;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.PaintingId;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record UpdateEaselCanvasIdS2C(int entityId, PaintingId id) implements CustomPayload {
    public static final Id<UpdateEaselCanvasIdS2C> ID = new Id<>(CrazyPainting.id("update_easel_canvas_id"));
    public static final PacketCodec<PacketByteBuf, UpdateEaselCanvasIdS2C> CODEC = PacketCodec.of(UpdateEaselCanvasIdS2C::encode, UpdateEaselCanvasIdS2C::decode);

    private void encode(PacketByteBuf buf) {
        buf.writeInt(entityId);
        id.writeTo(buf);
    }

    private static UpdateEaselCanvasIdS2C decode(PacketByteBuf buf) {
        return new UpdateEaselCanvasIdS2C(buf.readInt(), PaintingId.readFrom(buf));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
