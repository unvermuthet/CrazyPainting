package com.github.omoflop.crazypainting.network.event;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.ChangeId;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Optional;

public record PaintingChangeEvent(Optional<ChangeId> change, PaintingData data) implements CustomPayload {
    public static final Id<PaintingChangeEvent> ID = new Id<>(CrazyPainting.id("painting_change"));
    public static final PacketCodec<PacketByteBuf, PaintingChangeEvent> CODEC = PacketCodec.of(PaintingChangeEvent::encode, PaintingChangeEvent::decode);

    private void encode(PacketByteBuf buf) {
        buf.writeBoolean(change.isPresent());
        change.ifPresent(changeId -> changeId.writeTo(buf));

        data.writeTo(buf);

    }

    private static PaintingChangeEvent decode(PacketByteBuf buf) {
        boolean changeIsPresent = buf.readBoolean();

        return new PaintingChangeEvent(
                changeIsPresent ? Optional.of(ChangeId.readFrom(buf)) : Optional.empty(),
                PaintingData.readFrom(buf)
        );
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
