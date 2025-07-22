package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.client.CanvasTexture;
import com.github.omoflop.crazypainting.client.CanvasTextureManager;
import com.github.omoflop.crazypainting.client.screens.PaintingEditorScreen;
import com.github.omoflop.crazypainting.client.screens.PaintingViewerScreen;
import com.github.omoflop.crazypainting.network.event.PaintingChangeEvent;
import com.github.omoflop.crazypainting.network.types.ChangeKey;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;

import java.util.Arrays;

import static com.github.omoflop.crazypainting.CrazyPainting.LOGGER;
import static com.github.omoflop.crazypainting.CrazyPainting.SHOW_NETWORK_LOGS;

public class ClientPaintingChangeHandler implements ClientPlayNetworking.PlayPayloadHandler<PaintingChangeEvent> {
    @Override
    public void receive(PaintingChangeEvent packet, ClientPlayNetworking.Context context) {
        if (SHOW_NETWORK_LOGS) {
            LOGGER.log(Level.ALL, "Received painting change");

            LOGGER.log(Level.ALL,"Change present? " + packet.change().isPresent());
            if (packet.change().isPresent()) {
                ChangeKey key = packet.change().get();
                LOGGER.log(Level.ALL,"\t key:" + Arrays.toString(key.key()));
            }

            LOGGER.log(Level.ALL, Arrays.toString(packet.data().data()));
        }

        System.out.println("Received painting change Event");

        if (packet.change().isPresent()) {
            CanvasTexture texture = CanvasTextureManager.receive(packet.data().id().value(), packet.data());
            MinecraftClient.getInstance().setScreen(new PaintingEditorScreen(packet, texture));
        } else {
            MinecraftClient.getInstance().setScreen(new PaintingViewerScreen(packet.data(), packet.title()));
        }
    }
}
