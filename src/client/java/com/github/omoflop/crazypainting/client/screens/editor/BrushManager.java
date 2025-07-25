package com.github.omoflop.crazypainting.client.screens.editor;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.ColorHelper;
import com.github.omoflop.crazypainting.client.texture.CanvasTexture;
import com.github.omoflop.crazypainting.items.PaletteItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.omoflop.crazypainting.client.screens.PaintingEditorScreen.EDITOR_TEXTURE_ID;

public class BrushManager {
    private static int lastSelectedColor;
    private static int lastSelectedColor2;

    private final MinecraftClient client;
    private final CanvasTexture canvasTexture;
    private final int pixelSize;
    private final AtomicBoolean hasChanges;

    public final int[] colors;

    public int color;
    public int color2;

    public @Nullable String category = "square";
    public @Nullable BrushType brush = BrushType.getBrush("square", "1x1");
    public float opacity = 1f;

    private int lastMousePixelX = -1;
    private int lastMousePixelY = -1;

    public BrushManager(ItemStack paletteStack, CanvasTexture canvasTexture, int pixelSize, AtomicBoolean hasChanges) {
        var colors = PaletteItem.getColors(paletteStack);
        client = MinecraftClient.getInstance();
        this.colors = colors.stream().mapToInt(Integer::intValue).toArray();
        this.canvasTexture = canvasTexture;
        this.pixelSize = pixelSize;
        this.hasChanges = hasChanges;

        if (this.colors.length > 0) {
            if (colors.contains(lastSelectedColor)) {
                color = lastSelectedColor;
            } else {
                color = this.colors[0];
            }

            if (colors.contains(lastSelectedColor2)) {
                color2 = lastSelectedColor2;
            } else {
                color2 = CrazyPainting.WHITE;
            }
        }
    }

    public int getColorWithOpacity() {
        return color == CrazyPainting.TRANSPARENT ? color : ColorHelper.setOpacity(color, opacity);
    }

    public int getColor2WithOpacity() {
        return color2 == CrazyPainting.TRANSPARENT ? color2 : ColorHelper.setOpacity(color2, opacity);
    }

    public void drawCursor(DrawContext context, int mouseX, int mouseY) {
        if (color == -1) return;

        // Draw main brush handle
        context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 16, 0, 16, 16, 64, 64, 0xFFFFFFFF);

        // Draw brush part of the brush where the paint goes But if its transparent use a special texture
        if (color == CrazyPainting.TRANSPARENT) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 0, 16, 16, 16, 64, 64, 0xFFFFFFFF);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 0, 0, 16, 16, 64, 64, color);
        }
    }

    public boolean updateCursor(DrawContext context, int mousePixelX, int mousePixelY, int canvasPixelX, int canvasPixelY, boolean leftMouseDown, boolean rightMouseDown) {
        boolean cursorHidden = false;
        if (mousePixelX >= canvasPixelX && mousePixelY >= canvasPixelY && mousePixelX < canvasPixelX + canvasTexture.width && mousePixelY < canvasPixelY + canvasTexture.height) {
            if (brush != null) {
                brush.iteratePatternCentered(mousePixelX, mousePixelY, (px, py, alpha) -> {

                    int drawX = px * pixelSize;
                    int drawY = py * pixelSize;
                    context.drawBorder(drawX, drawY, pixelSize, pixelSize, color);
                });

                if (color != -1 && leftMouseDown) {
                    useBrush(mousePixelX - canvasPixelX, mousePixelY - canvasPixelY, getColorWithOpacity());
                }
                if (color2 != -1 && rightMouseDown) {
                    useBrush(mousePixelX - canvasPixelX, mousePixelY - canvasPixelY, getColor2WithOpacity());
                }
            }


            if (color != -1) {
                GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
                cursorHidden = true;
            }
        } else {
            GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }

        return cursorHidden;
    }

    public void selectColor(int color, boolean primary) {
        if (primary) {
            this.color = color;
            lastSelectedColor = color;
        } else {
            this.color2 = color;
            lastSelectedColor2 = color2;
        }

        client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.75f, primary ? 1f : 1.2f);
    }

    public void useBrush(int x, int y, int color) {
        if (brush == null) return;

        if (lastMousePixelX == x && lastMousePixelY == y) return;

        lastMousePixelX = x;
        lastMousePixelY = y;

        brush.iteratePatternCentered(x, y, (px, py, alpha) -> {
            if (!canvasTexture.isPixelInBounds(px, py)) return;
            int i = px + py * canvasTexture.width;
            canvasTexture.pixels[i] = color == CrazyPainting.TRANSPARENT ? color : ColorHelper.mix(canvasTexture.pixels[i], color);
        });

        hasChanges.set(true);
    }

    public void mouseReleased() {
        lastMousePixelX = -1;
        lastMousePixelY = -1;
    }
}
