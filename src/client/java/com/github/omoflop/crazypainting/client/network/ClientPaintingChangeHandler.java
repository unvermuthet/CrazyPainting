package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.network.s2c.PaintingChangeEventS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientPaintingChangeHandler implements ClientPlayNetworking.PlayPayloadHandler<PaintingChangeEventS2C> {
    @Override
    public void receive(PaintingChangeEventS2C packet, ClientPlayNetworking.Context context) {

    }
}
