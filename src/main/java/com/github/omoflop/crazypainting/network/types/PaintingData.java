package com.github.omoflop.crazypainting.network.types;

import net.minecraft.network.PacketByteBuf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public record PaintingData(byte[] data, PaintingSize size) {
    public void writeTo(PacketByteBuf buf) {
        buf.writeBytes(data);
        size.writeTo(buf);
    }

    public static PaintingData readFrom(PacketByteBuf buf) {
        return new PaintingData(
                buf.readByteArray(),
                PaintingSize.readFrom(buf)
        );
    }

    public BufferedImage readImage() throws IOException {
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    public int[] readPixelArray() throws IOException {
        BufferedImage img = readImage();

        int[] pixels = new int[(size.width() * 16) * (size.height() * 16)];
        return img.getRGB(0, 0, size.width() * 16, size.height() * 16, pixels, 0, size.width() * 16);
    }
}
