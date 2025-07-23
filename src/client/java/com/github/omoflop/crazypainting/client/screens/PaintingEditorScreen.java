package com.github.omoflop.crazypainting.client.screens;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.screens.editor.BrushType;
import com.github.omoflop.crazypainting.client.texture.CanvasTexture;
import com.github.omoflop.crazypainting.items.PaletteItem;
import com.github.omoflop.crazypainting.network.event.PaintingChangeEvent;
import com.github.omoflop.crazypainting.network.types.ChangeKey;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static com.github.omoflop.crazypainting.CrazyPainting.LOGGER;

@Environment(EnvType.CLIENT)
public class PaintingEditorScreen extends Screen {
    private static Identifier EDITOR_TEXTURE_ID = CrazyPainting.id("textures/gui/editor.png");
    private static final int PALETTE_DISPLAY_SIZE = 12;
    private static int lastSelectedColor = CrazyPainting.BLACK;

    private CanvasTexture canvasTexture = null;

    private final int pixelSize;

    private int selectedColor = -1;
    private String paintingTitle;

    private boolean leftMouseDown;
    private boolean rightMouseDown;

    private ItemStack paletteStack;
    private int[] colors;
    private int canvasId;

    private int updateCooldown = 20;
    private boolean hasChanges = false;
    private final Optional<ChangeKey> key;

    private @Nullable String selectedBrushCategory = "square";
    private @Nullable BrushType selectedBrushType = BrushType.getBrush("square", "1x1");

    public PaintingEditorScreen(PaintingChangeEvent event, CanvasTexture texture) {
        super(Text.translatable("gui.crazypainting.painting_editor.title"));
        this.client = MinecraftClient.getInstance();

        paintingTitle = event.title();
        canvasTexture = texture;
        key = event.change();
        pixelSize = (int)Math.max(1, 10.0f / (Math.max(canvasTexture.width/16, canvasTexture.height/16)));

        if (client == null || client.player == null) {
            close();
            return;
        }
        paletteStack = client.player.getStackInHand(Hand.MAIN_HAND);

    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (canvasTexture == null) return;

        // Variables positioning elements on screen
        int canvasPixelX = (int)((this.width / 2f - canvasTexture.width*pixelSize/2f) / pixelSize);
        int canvasPixelY = (int)((this.height / 2f - canvasTexture.height*pixelSize/2f) / pixelSize);

        int canvasX = canvasPixelX * pixelSize;
        int canvasY = canvasPixelY * pixelSize;

        int mousePixelX = mouseX / pixelSize;
        int mousePixelY = mouseY / pixelSize;

        // Draw title and canvas
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(paintingTitle), width / 2, 64, CrazyPainting.YELLOW);
        drawCanvas(context, canvasX, canvasY);

        boolean usePaintCursor = updateCursor(context, mousePixelX, mousePixelY, canvasPixelX, canvasPixelY);

        // Brush size area
        Text brushText = Text.translatable("gui.crazypainting.painting_editor.brush_settings");
        context.drawText(textRenderer, brushText, width - 100, 64, CrazyPainting.YELLOW, true);
        drawBrushArea(context, width - 100, 64 + textRenderer.fontHeight*2, textRenderer.getWidth(brushText), mouseX, mouseY);
        
        // Color palette area
        Text paletteText = Text.translatable("gui.crazypainting.palette");
        int paletteTextWidth = textRenderer.getWidth(paletteText);
        context.drawText(textRenderer, paletteText, 100, 64, CrazyPainting.YELLOW, true);
        drawColorPalette(context, 100 + paletteTextWidth / 2, 65, paletteTextWidth, mouseX, mouseY);

        if (usePaintCursor) drawPaintBrushCursor(context, mouseX, mouseY);
    }

    private void drawBrushArea(DrawContext context, int x, int y, int textWidth, int mouseX, int mouseY) {
        int i = 0;
        for (String category : BrushType.getCategories()) {
            boolean selected = category.equals(selectedBrushCategory);
            Text categoryText = Text.literal(category);
            int categoryTextY = y + i*textRenderer.fontHeight;
            boolean categoryHovered = mouseX >= x && mouseX < x + textRenderer.getWidth(categoryText) && mouseY >= categoryTextY && mouseY < categoryTextY + textRenderer.fontHeight;
            int categoryTextColor = CrazyPainting.LIGHT_GRAY;
            if (selected) {
                categoryTextColor = CrazyPainting.LIME;
            } else if (categoryHovered) {
                categoryTextColor = CrazyPainting.WHITE;
            }

            context.drawText(textRenderer, categoryText, x, categoryTextY, categoryTextColor, false);
            if (categoryHovered && leftMouseDown) {
                selectedBrushCategory = category;
            }

            if (selected) {
                int yOffset = 0;
                var brushes = BrushType.getBrushes(category);

                for (BrushType type : brushes) {
                    boolean hovered = drawBrushButton(context, type, x + textWidth - 12, y + yOffset, mouseX, mouseY);
                    if (hovered) {
                        context.drawTooltip(Text.literal(type.name), mouseX, mouseY);
                        if (leftMouseDown && selectedBrushType != type) {
                            client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.75f, 1f);
                            selectedBrushType = type;
                        }
                    }

                    yOffset += 12;
                }
            }

            i++;
        }
    }

    private boolean drawBrushButton(DrawContext context, BrushType type, int x, int y, int mouseX, int mouseY) {
        boolean selected = type == selectedBrushType;
        context.fill(x, y, x + PALETTE_DISPLAY_SIZE, y + PALETTE_DISPLAY_SIZE, selected ? CrazyPainting.LIME : CrazyPainting.WHITE);
        context.drawBorder(x - 1, y - 1, PALETTE_DISPLAY_SIZE + 1, PALETTE_DISPLAY_SIZE + 1, CrazyPainting.BLACK);

        int width = PALETTE_DISPLAY_SIZE - type.getWidth();
        int height = PALETTE_DISPLAY_SIZE - type.getHeight();
        type.iteratePattern(x + width/2, y + height/2, (x2, y2, opacity) -> {
            context.fill(x2, y2, x2 + 1, y2 + 1, selected ? CrazyPainting.WHITE : CrazyPainting.BLACK);
        });


        return mouseX >= x &&
                mouseY >= y &&
                mouseX < x + PALETTE_DISPLAY_SIZE &&
                mouseY < y + PALETTE_DISPLAY_SIZE;
    }

    private void drawPaintBrushCursor(DrawContext context, int mouseX, int mouseY) {
        if (selectedColor != -1) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 16, 0, 16, 16, 64, 64, 0xFFFFFFFF);
            if (selectedColor == CrazyPainting.TRANSPARENT) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 0, 16, 16, 16, 64, 64, 0xFFFFFFFF);
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 0, 0, 16, 16, 64, 64, selectedColor);
            }
        }
    }

    private void drawColorPalette(DrawContext context, int x, int y, int textWidth, int mouseX, int mouseY) {
        if (colors == null) {
            context.drawWrappedTextWithShadow(textRenderer, Text.translatable("gui.crazypainting.palette.empty"), 100 - textWidth, 64 + textRenderer.fontHeight * 2, textWidth*3, CrazyPainting.RED);
            return;
        }

        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            int drawX = x + (i % 2) * (PALETTE_DISPLAY_SIZE+1) - PALETTE_DISPLAY_SIZE;
            int drawY = y + textRenderer.fontHeight + (i / 2) * (PALETTE_DISPLAY_SIZE+1);
            boolean hovered = drawPaletteColor(context, color, drawX, drawY, mouseX, mouseY);
            if (leftMouseDown && hovered) {
                if (selectedColor != color) {
                    client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.75f, 1);
                }
                selectedColor = color;
                lastSelectedColor = color;
            }
        }
    }

    private boolean updateCursor(DrawContext context, int mousePixelX, int mousePixelY, int canvasPixelX, int canvasPixelY) {
        boolean cursorHidden = false;
        if (mousePixelX >= canvasPixelX && mousePixelY >= canvasPixelY && mousePixelX < canvasPixelX + canvasTexture.width && mousePixelY < canvasPixelY + canvasTexture.height) {
            if (selectedBrushType != null) {
                selectedBrushType.iteratePatternCentered(mousePixelX, mousePixelY, (px, py, alpha) -> {
                    
                    int drawX = px * pixelSize;
                    int drawY = py * pixelSize;
                    int bob = (int) Math.sin(client.getRenderTime()) * 20 + 60;
                    context.fill(drawX, drawY, drawX + pixelSize, drawY + pixelSize, (selectedColor & 0x00FFFFFF) | (0x1000000 * bob));

                });

                if (selectedColor != -1 && leftMouseDown) {
                    setPixel(mousePixelX - canvasPixelX, mousePixelY - canvasPixelY, selectedColor);
                }
                if (rightMouseDown) {
                    setPixel(mousePixelX - canvasPixelX, mousePixelY - canvasPixelY, CrazyPainting.WHITE);
                }
            }


            if (selectedColor != -1) {
                GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
                cursorHidden = true;
            }
        } else {
            GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }

        return cursorHidden;
    }

    private void drawCanvas(DrawContext context, int canvasX, int canvasY) {
        for(int i = 0; i < canvasTexture.pixels.length; i++) {
            int drawX = canvasX + (i % canvasTexture.width) * pixelSize;
            int drawY = canvasY + (i / canvasTexture.width) * pixelSize;
            context.fill(drawX, drawY, drawX + pixelSize, drawY + pixelSize, canvasTexture.pixels[i]);
        }
    }

    private boolean drawPaletteColor(DrawContext context, int color, int x, int y, int mouseX, int mouseY) {
        context.fill(x, y, x + PALETTE_DISPLAY_SIZE, y + PALETTE_DISPLAY_SIZE, color);
        context.drawBorder(x - 1, y - 1, PALETTE_DISPLAY_SIZE + 1, PALETTE_DISPLAY_SIZE + 1, selectedColor == color ? CrazyPainting.WHITE : CrazyPainting.BLACK);

        return mouseX >= x &&
                mouseY >= y &&
                mouseX < x + PALETTE_DISPLAY_SIZE &&
                mouseY < y + PALETTE_DISPLAY_SIZE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) leftMouseDown = true;
        else if (button == 1) rightMouseDown = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) leftMouseDown = false;
        else if (button == 1) rightMouseDown = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void setPixel(int x, int y, int color) {
        if (selectedBrushType == null) return;
        selectedBrushType.iteratePatternCentered(x, y, (px, py, alpha) -> {
            if (!isPixelInBounds(px, py)) return;
            canvasTexture.pixels[px + py * canvasTexture.width] = color;
        });

        hasChanges = true;
    }

    public boolean isPixelInBounds(int x, int y) {
        return x >= 0 && x < canvasTexture.width && y >= 0 && y < canvasTexture.height;
    }

    @Override
    public void tick() {
        super.tick();

        if (hasChanges) {
            updateCooldown -= 1;
            if (updateCooldown <= 0) {
                updateCooldown = 20;
                hasChanges = false;

                PaintingData data = canvasTexture.toData();
                if (data != null) {
                    ClientPlayNetworking.send(new PaintingChangeEvent(key, data, paintingTitle));
                    canvasTexture.updateTexture();
                } else {
                    LOGGER.error("Failed to send painting change update packet");
                }
            }
        }

        var colors = PaletteItem.getColors(paletteStack);
        if (!colors.isEmpty()) {
            this.colors = colors.stream().mapToInt(Integer::intValue).toArray();
            if (colors.contains(lastSelectedColor)) {
                selectedColor = lastSelectedColor;
            } else {
                selectedColor = this.colors[0];
            }
        }
    }

    @Override
    public void close() {
        super.close();

        canvasTexture.updateTexture();
        PaintingData data = canvasTexture.toData();
        if (data != null) ClientPlayNetworking.send(new PaintingChangeEvent(key, data, paintingTitle));

        client.mouse.lockCursor();
    }
}
