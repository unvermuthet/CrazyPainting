package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.client.CanvasTextureManager;
import com.github.omoflop.crazypainting.network.s2c.PaintingUpdateS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PaintingUpdateHandler implements ClientPlayNetworking.PlayPayloadHandler<PaintingUpdateS2C> {
    @Override
    public void receive(PaintingUpdateS2C packet, ClientPlayNetworking.Context context) {
        CanvasTextureManager.receive(packet.id(), packet.data());
    }
}
