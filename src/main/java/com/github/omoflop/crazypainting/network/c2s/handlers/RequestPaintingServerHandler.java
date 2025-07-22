package com.github.omoflop.crazypainting.network.c2s.handlers;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.c2s.RequestPaintingC2S;
import com.github.omoflop.crazypainting.network.event.PaintingChangeEvent;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import com.github.omoflop.crazypainting.state.CanvasManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Objects;

public class RequestPaintingServerHandler implements ServerPlayNetworking.PlayPayloadHandler<RequestPaintingC2S> {
    @Override
    public void receive(RequestPaintingC2S packet, ServerPlayNetworking.Context ctx) {
        ServerPlayerEntity player = ctx.player();
        MinecraftServer server = Objects.requireNonNull(player.getServer());

        PaintingData data;
        try {
            data = CanvasManager.load(packet.id(), server);
        } catch (IOException e) {
            if (CrazyPainting.SHOW_NETWORK_LOGS) {
                CrazyPainting.LOGGER.log(Level.ALL, "Failed to load painting from disk after player requested it...");
            }
            return;
        }

        ServerPlayNetworking.send(player, new PaintingChangeEvent());

    }
}