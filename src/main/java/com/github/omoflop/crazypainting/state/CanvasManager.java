package com.github.omoflop.crazypainting.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;

public class CanvasManager extends PersistentState {
    public static final Codec<CanvasManager> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("next_id").forGetter((self) -> self.nextId)
    ).apply(builder, CanvasManager::new));

    private Int2ObjectMap<Integer> paintingVersionMap = new Int2ObjectOpenHashMap<>();
    private int nextId = -1;

    public CanvasManager(int nextId) {
        this.nextId = nextId;
    }

    public int getNextId() {
        nextId += 1;
        return nextId;
    }

    public boolean validatePaintingVersion(int canvasId, int newVersion) {
        int currentVersion = paintingVersionMap.getOrDefault((Integer)canvasId, (Integer)(-1));

        if (newVersion > currentVersion) {
            paintingVersionMap.put((Integer) canvasId, (Integer) newVersion);
            return true;
        }
        return false;
    }

    public static CanvasManager createNew() {
        return new CanvasManager(-1);
    }

    public static CanvasManager getServerState(MinecraftServer server) {
        PersistentStateManager manager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        PersistentStateType<CanvasManager> type = new PersistentStateType<>("crazypainting:canvas_manager", CanvasManager::createNew, CODEC, null);
        CanvasManager canvasManager = manager.getOrCreate(type);
        canvasManager.markDirty();
        return canvasManager;
    }

}
