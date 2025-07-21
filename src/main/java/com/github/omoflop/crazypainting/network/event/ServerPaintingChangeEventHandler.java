package com.github.omoflop.crazypainting.network.event;

import com.github.omoflop.crazypainting.state.CanvasManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;

public class ServerPaintingChangeEventHandler implements ServerPlayNetworking.PlayPayloadHandler<PaintingChangeEvent> {
    @Override
    public void receive(PaintingChangeEvent packet, ServerPlayNetworking.Context ctx) {
        ServerPlayerEntity player = ctx.player();
        ServerWorld world = player.getWorld();
        MinecraftServer server = Objects.requireNonNull(player.getServer());
        CanvasManager state = CanvasManager.getServerState(server);
    }
}