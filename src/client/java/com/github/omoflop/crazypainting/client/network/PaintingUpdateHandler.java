package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.CanvasTextureManager;
import com.github.omoflop.crazypainting.network.s2c.PaintingUpdateS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.apache.logging.log4j.Level;

public class PaintingUpdateHandler implements ClientPlayNetworking.PlayPayloadHandler<PaintingUpdateS2C> {
    @Override
    public void receive(PaintingUpdateS2C packet, ClientPlayNetworking.Context context) {
        CanvasTextureManager.receive(packet.id(), packet.data());

        System.out.println("Received painting update packet");
        if (CrazyPainting.SHOW_NETWORK_LOGS)
            CrazyPainting.LOGGER.log(Level.ALL, "Received update packet, for id: %s".formatted(packet.id()));
    }
}
