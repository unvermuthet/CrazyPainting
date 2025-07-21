package com.github.omoflop.crazypainting.client.screens;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.entities.EaselEntity;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class PaintingViewerScreen extends Screen {
    private final int[] pixels;
    private final PaintingSize size;
    private final int pixelSize;

    public PaintingViewerScreen(PaintingData data) {
        super(Text.translatable("gui.crazypainting.painting_viewer.title"));
        try {
            size = data.size();
            pixelSize = (int)Math.max(1, 10.0f / (Math.max(size.width(), size.height())));


            pixels = data.readPixelArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        int widthPixels = size.width() * 16;
        int heightPixels = size.height() * 16;

        int canvasPixelX = (int)((this.width / 2f - widthPixels*pixelSize/2f) / pixelSize);
        int canvasPixelY = (int)((this.height / 2f - heightPixels*pixelSize/2f) / pixelSize);

        int canvasX = canvasPixelX * pixelSize;
        int canvasY = canvasPixelY * pixelSize;

        // Draw Title
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("I didnt sync the name lol"), width / 2, 64, CrazyPainting.YELLOW);

        for(int i = 0; i < pixels.length; i++) {
            int drawX = canvasX + (i % widthPixels) * pixelSize;
            int drawY = canvasY + (i / widthPixels) * pixelSize;
            context.fill(drawX, drawY, drawX + pixelSize, drawY + pixelSize, pixels[i]);
        }
    }
}
