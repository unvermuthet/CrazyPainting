package com.github.omoflop.crazypainting.client;

import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CanvasTextureManager {
    private static final Int2ObjectMap<CanvasTexture> TEXTURE_MAP = new Int2ObjectOpenHashMap<>();
    private static final List<Integer> AWAITED_TEXTURES = new ArrayList<>();

    public static Optional<CanvasTexture> request(int id) {
        if (TEXTURE_MAP.containsKey(id)) return Optional.of(TEXTURE_MAP.get(id));
        if (AWAITED_TEXTURES.contains(id)) return Optional.empty();

        AWAITED_TEXTURES.add(id);
        // Put network request here

        return Optional.ofNullable(TEXTURE_MAP.get(id));
    }

    public static void createNew(int id, CanvasItem canvasType) {
        TEXTURE_MAP.put(id, new CanvasTexture(canvasType, id));
        if (AWAITED_TEXTURES.contains(id))
            AWAITED_TEXTURES.remove(id);
    }

    public static void createNew(int id, PaintingSize size) {
        TEXTURE_MAP.put(id, new CanvasTexture(size.width(), size.height(), id, true));
        if (AWAITED_TEXTURES.contains(id))
            AWAITED_TEXTURES.remove(id);
    }

    public static void unload(int canvasId) {
        if (!TEXTURE_MAP.containsKey(canvasId)) return;
        TEXTURE_MAP.remove(canvasId).close();
    }

    public static void unloadAll() {
        TEXTURE_MAP.values().forEach(CanvasTexture::close);
        AWAITED_TEXTURES.clear();
        TEXTURE_MAP.clear();
    }

    public static CanvasTexture get(int canvasId) {
        return TEXTURE_MAP.get(canvasId);
    }

    private static void updateOrCreate(int canvasId, PaintingData data) {
        CanvasTexture texture;
        if (!TEXTURE_MAP.containsKey(canvasId)) {
            PaintingSize size = data.size();
            texture = new CanvasTexture(size.width(), size.height(), canvasId, true);
            TEXTURE_MAP.put(canvasId, texture);
        } else {
            texture = TEXTURE_MAP.get(canvasId);
        }

        try {
            texture.updateTexture(data.readPixelArray(), -1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getVersion(int canvasId) {
        if (!TEXTURE_MAP.containsKey(canvasId)) return -1;
        return get(canvasId).version;
    }

    public static void receive(int canvasId, PaintingData data) {
        // Remove from awaited textures if it was requested already
        if (AWAITED_TEXTURES.contains(canvasId)) {
            AWAITED_TEXTURES.remove(canvasId);
        }

        updateOrCreate(canvasId, data);
    }
}
