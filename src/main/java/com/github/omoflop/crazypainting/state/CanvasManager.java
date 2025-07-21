package com.github.omoflop.crazypainting.state;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.network.types.ChangeId;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.*;

public class CanvasManager extends PersistentState {
    public static final Codec<CanvasManager> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("next_id").forGetter((self) -> self.nextId)
    ).apply(builder, CanvasManager::new));

    public static final HashMap<UUID, ChangeId> CHANGE_IDS = new HashMap<>();

    private int nextId = -1;

    public CanvasManager(int nextId) {
        this.nextId = nextId;
    }

    public static PaintingData createOrLoad(int canvasId, PaintingSize size, MinecraftServer server) throws IOException {
        Path savePath = server.getSavePath(WorldSavePath.ROOT).resolve("data/crazy_paintings");
        Files.createDirectories(savePath);
        Path paintingPath = savePath.resolve("painting_" + canvasId + ".qoi");

        byte[] bytes = tryRead(paintingPath).orElseGet(() -> createEmptyImage(size));

        return new PaintingData(bytes, size);
    }

    private static Optional<byte[]> tryRead(Path path) {
        try {
            return Optional.of(Files.readAllBytes(path));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static byte[] createEmptyImage(PaintingSize size) {
        BufferedImage img = new BufferedImage(size.width() * 16, size.height() * 16, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[img.getWidth() * img.getHeight()];
        Arrays.fill(pixels, CrazyPainting.WHITE);
        img.setRGB(0, 0, size.width() * 16, size.height() * 16, pixels, 0, size.width()*16);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "qoi", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNextId() {
        nextId += 1;
        return nextId;
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
