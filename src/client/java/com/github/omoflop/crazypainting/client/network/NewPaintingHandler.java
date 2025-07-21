package com.github.omoflop.crazypainting.client.network;

import com.github.omoflop.crazypainting.client.CanvasTextureManager;
import com.github.omoflop.crazypainting.client.screens.PaintingEditorScreen;
import com.github.omoflop.crazypainting.entities.EaselEntity;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.network.s2c.NewPaintingS2CPacket;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class NewPaintingHandler implements ClientPlayNetworking.PlayPayloadHandler<NewPaintingS2CPacket> {
    @Override
    public void receive(NewPaintingS2CPacket packet, ClientPlayNetworking.Context context) {
        World world = context.player().getWorld();

        // Ensure the entity exists
        Entity entity = world.getEntityById(packet.easelId());
        if (!(entity instanceof EaselEntity easel)) {
            System.out.println("Received invalid easel entity id");
            return;
        }

        // Create a new canvas texture for on the client
        CanvasTextureManager.createNew(packet.paintingId(), packet.size());

        // Create a new stack for the easel to hold because the server should have it be like this anyway
        // so its like just in case
        CanvasItem.setId(easel.getStackInHand(Hand.MAIN_HAND), packet.paintingId());

        // Open the editor screen with the new canvas and id and stuff
        context.client().setScreen(new PaintingEditorScreen(easel));
    }
}