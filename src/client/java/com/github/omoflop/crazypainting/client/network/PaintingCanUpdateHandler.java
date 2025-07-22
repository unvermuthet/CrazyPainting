package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.texture.CanvasTextureManager;
import com.github.omoflop.crazypainting.network.s2c.PaintingCanUpdateS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.apache.logging.log4j.Level;

public class PaintingCanUpdateHandler implements ClientPlayNetworking.PlayPayloadHandler<PaintingCanUpdateS2C> {
    @Override
    public void receive(PaintingCanUpdateS2C packet, ClientPlayNetworking.Context context) {
        CanvasTextureManager.markAsUpdatable(packet.id());

        System.out.println("Received painting CAN update packet");
        if (CrazyPainting.SHOW_NETWORK_LOGS)
            CrazyPainting.LOGGER.log(Level.ALL, "Received painting can update packet, for id: %s".formatted(packet.id()));
    }
}
