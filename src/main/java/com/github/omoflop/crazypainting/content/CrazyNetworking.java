package com.github.omoflop.crazypainting.content;

import com.github.omoflop.crazypainting.network.types.PaintingData;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import com.github.omoflop.crazypainting.network.c2s.PaintingChangeEventC2S;
import com.github.omoflop.crazypainting.network.c2s.RequestPaintingC2S;
import com.github.omoflop.crazypainting.network.c2s.handlers.PaintingChangeEventServerHandler;
import com.github.omoflop.crazypainting.network.c2s.handlers.RequestPaintingServerHandler;
import com.github.omoflop.crazypainting.network.s2c.NewPaintingS2CPacket;
import com.github.omoflop.crazypainting.network.s2c.OpenPaintingS2CPacket;
import com.github.omoflop.crazypainting.network.s2c.PaintingChangeEventS2C;
import com.github.omoflop.crazypainting.network.s2c.PaintingUpdateS2C;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CrazyNetworking {
    public static void register() {
        // Server -> Client
        PayloadTypeRegistry.playS2C().register(NewPaintingS2CPacket.ID, NewPaintingS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenPaintingS2CPacket.ID, OpenPaintingS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(PaintingChangeEventS2C.ID, PaintingChangeEventS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(PaintingUpdateS2C.ID, PaintingUpdateS2C.CODEC);

        // Client -> Server
        PayloadTypeRegistry.playC2S().register(PaintingChangeEventC2S.ID, PaintingChangeEventC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestPaintingC2S.ID, RequestPaintingC2S.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PaintingChangeEventC2S.ID, new PaintingChangeEventServerHandler());
        ServerPlayNetworking.registerGlobalReceiver(RequestPaintingC2S.ID, new RequestPaintingServerHandler());
    }

    public static void savePainting(Path savePath, int id, PaintingData data, PaintingSize size) throws IOException {
        Files.createDirectory(savePath);
        File pngFile = Files.createFile(savePath.resolve("painting_" + id + ".png")).toFile();

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data.data()));
        ImageIO.write(image, "PNG", pngFile);
    }

    public static byte[] loadPainting(Path rootPath, int id) throws IOException {
        return Files.readAllBytes(rootPath.resolve("painting_" + id + ".png"));
    }
}
