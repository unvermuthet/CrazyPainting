package com.github.omoflop.crazypainting.network.c2s.handlers;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.github.omoflop.crazypainting.entities.EaselEntity;
import com.github.omoflop.crazypainting.network.ChangeRecord;
import com.github.omoflop.crazypainting.network.c2s.SignPaintingC2S;
import com.github.omoflop.crazypainting.state.CanvasManager;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class SignPaintingServerHandler implements ServerPlayNetworking.PlayPayloadHandler<SignPaintingC2S> {
    @Override
    public void receive(SignPaintingC2S packet, ServerPlayNetworking.Context ctx) {
        ServerPlayerEntity player = ctx.player();
        UUID uuid = player.getUuid();

        // Ensure the player may sign this painting

        ChangeRecord change = CanvasManager.CHANGE_IDS.get(uuid);

        // Check if the server even has a change key for this player
        if (change == null) {
            CrazyPainting.debug("Server does not have a change key initialized for this player.");
            return;
        }

        // Check if the change key sent by the player matches the one stored on the server
        if (!change.key().equals(packet.changeKey())) {
            CrazyPainting.debug("Change key does not match server key. Client sent: %s, Server has: %s", packet.changeKey(), change.key());
            return;
        }

        if (!change.id().equals(packet.id())) {
            CrazyPainting.debug("Received two different ids. Client sent: %s, Server has: %s", packet.id(), change.id());
            return;
        }

        Entity entity = player.getWorld().getEntityById(packet.easelEntityId());
        if (!(entity instanceof EaselEntity easel)) {
            CrazyPainting.debug("Received invalid entity id for sign painting packet");
            return;
        }

        ItemStack displayStack = easel.getDisplayStack();
        CanvasDataComponent data = displayStack.get(CrazyComponents.CANVAS_DATA);
        if (data == null) {
            CrazyPainting.debug("Tried to sign a canvas with no data");
            return;
        }

        if (!data.signedBy().isEmpty()) {
            CrazyPainting.debug("Tried to sign a canvas that is already signed");
            return;
        }        

        displayStack.set(CrazyComponents.CANVAS_DATA, data.withSignedBy(player.getNameForScoreboard()).withTitle(packet.title()).withGeneration((byte)0));
        easel.setDisplayStack(displayStack);
    }
}