package com.github.omoflop.crazypainting.network.c2s.handlers;

import com.github.omoflop.crazypainting.network.c2s.RequestPaintingC2S;
import com.github.omoflop.crazypainting.state.CanvasManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;

public class RequestPaintingServerHandler implements ServerPlayNetworking.PlayPayloadHandler<RequestPaintingC2S> {
    @Override
    public void receive(RequestPaintingC2S packet, ServerPlayNetworking.Context ctx) {
        ServerPlayerEntity player = ctx.player();
        ServerWorld world = player.getWorld();
        MinecraftServer server = Objects.requireNonNull(player.getServer());
        CanvasManager state = CanvasManager.getServerState(server);
    }
}