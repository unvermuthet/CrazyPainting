package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.client.screens.PaintingEditorScreen;
import com.github.omoflop.crazypainting.client.screens.PaintingViewerScreen;
import com.github.omoflop.crazypainting.entities.EaselEntity;
import com.github.omoflop.crazypainting.network.s2c.OpenPaintingS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;

public class OpenPaintingHandler implements ClientPlayNetworking.PlayPayloadHandler<OpenPaintingS2CPacket> {
    @Override
    public void receive(OpenPaintingS2CPacket packet, ClientPlayNetworking.Context handler) {
        var client = handler.client();

        assert client.world != null;
        Entity entity = client.world.getEntityById(packet.easelId());
        if (!(entity instanceof EaselEntity easel)) return;

        if (packet.edit()) {
            client.setScreen(new PaintingEditorScreen(easel));
            return;
        }

        client.setScreen(new PaintingViewerScreen(easel));
    }
}