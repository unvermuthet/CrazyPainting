package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.client.CanvasTextureManager;
import com.github.omoflop.crazypainting.client.screens.PaintingViewerScreen;
import com.github.omoflop.crazypainting.network.event.PaintingChangeEvent;
import com.github.omoflop.crazypainting.network.types.ChangeId;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public class ClientPaintingChangeHandler implements ClientPlayNetworking.PlayPayloadHandler<PaintingChangeEvent> {
    @Override
    public void receive(PaintingChangeEvent packet, ClientPlayNetworking.Context context) {
        System.out.println("Received painting change");
        System.out.println("Change present? " + packet.change().isPresent());
        if (packet.change().isPresent()) {
            ChangeId change = packet.change().get();
            System.out.println("\t id:" + change.id());
            System.out.println("\t key:" + Arrays.toString(change.key()));
        }

        System.out.println(Arrays.toString(packet.data().data()));

        if (packet.change().isPresent()) {
            CanvasTextureManager.receive(packet.change().get().id(), packet.data());
        } else {
            MinecraftClient.getInstance().setScreen(new PaintingViewerScreen(packet.data()));
        }
    }
}
