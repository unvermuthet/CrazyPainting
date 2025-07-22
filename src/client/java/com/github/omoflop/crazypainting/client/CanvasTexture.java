package com.github.omoflop.crazypainting.client;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.mixin.TextureManagerAccessor;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import com.github.omoflop.crazypainting.network.types.PaintingId;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class CanvasTexture implements AutoCloseable {
    // Only set when the canvas is editable
    public int[] pixels;

    // Dimensions of the image
    public final int width;
    public final int height;

    // Image data uploaded to the GPU for rendering
    public final NativeImage nativeImage;
    public final NativeImageBackedTexture glTexture;

    // Identifier used for lookups or rendering or something
    public final Identifier textureId;

    // Size and id for networking
    private final PaintingId id;
    private final PaintingSize size;

    private boolean ready = false;
    private boolean editable;
    public int version = -1;
    private boolean registered = false;
    private boolean isClosed = false;


    public CanvasTexture(CanvasItem base, int id) {
        this(base.width, base.height, id, true);
    }

    public CanvasTexture(byte canvasWidth, byte canvasHeight, int id, boolean editable) {
        textureId = CrazyPainting.id("canvas/" + id);

        this.id = new PaintingId(id);
        size = new PaintingSize(canvasWidth, canvasHeight);

        width = (canvasWidth * 16);
        height = (canvasHeight * 16);
        if (editable) {
            pixels = new int[width * height];
            Arrays.fill(pixels, CrazyPainting.WHITE);
        }

        this.editable = editable;
        nativeImage = new NativeImage(width, height, true);
        glTexture = new NativeImageBackedTexture(textureId::toString, nativeImage);
    }

    public boolean isReady() {
        return ready;
    }

    public void markUneditable() {
        editable = false;
        pixels = null;
    }

    public void updateTexture() {
        updateTexture(pixels, -1);
    }

    public void updateTexture(int[] newPixels, int version) {
        if (isClosed) return;

        // Don't bother updating if this is already the most up-to-date version
        // Ignored if version is -1, meaning the client is updating it.
        if (version != -1 && this.version >= version) return;
        this.version = version;

        // If the data is invalid then just ignore it (failsafe)
        if (width * height != newPixels.length) {
            System.out.println("Received invalid painting texture data");
            return;
        }

        if (!registered) {
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, glTexture);
            registered = true;
        }

        // Mark as unready while editing the texture because threads or something IDK
        // Set back to true once this.upload is called
        ready = false;

        // If it's editable, save the new pixel data for editing on this client
        if (editable) pixels = newPixels;

        // Write new received texture data to the GPU :)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width;
                nativeImage.setColorArgb(x, y, newPixels[i]);
            }
        }

        upload();
    }

    private void upload() {
        glTexture.upload();

        // Mark as ready to be rendered once upload is called
        ready = true;
    }

    @Override
    public void close() {
        if (isClosed) return;
        glTexture.close(); // Closes nativeImage automatically

        if (registered) {
            ((TextureManagerAccessor)(MinecraftClient.getInstance().getTextureManager())).textures().remove(textureId);
            registered = false;
        }

        isClosed = true;
    }

    public @Nullable PaintingData toData() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, width, height, pixels, 0, width);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "qoi", out);
            return new PaintingData(out.toByteArray(), size, id);

        } catch (IOException e) {
            return null;
        }
    }
}
