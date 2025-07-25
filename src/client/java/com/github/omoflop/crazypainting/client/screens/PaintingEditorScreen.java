package com.github.omoflop.crazypainting.client.screens;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.ColorHelper;
import com.github.omoflop.crazypainting.client.screens.editor.BrushManager;
import com.github.omoflop.crazypainting.client.screens.editor.BrushType;
import com.github.omoflop.crazypainting.client.screens.editor.NetSync;
import com.github.omoflop.crazypainting.client.texture.CanvasTexture;
import com.github.omoflop.crazypainting.network.event.PaintingChangeEvent;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class PaintingEditorScreen extends Screen {
    public static final Identifier EDITOR_TEXTURE_ID = CrazyPainting.id("textures/gui/editor.png");

    private static final Text PALETTE_TEXT = Text.translatable("gui.crazypainting.palette");
    private static final Text PALETTE_EMPTY_TEXT = Text.translatable("gui.crazypainting.palette.empty");
    private static final Text TITLE_PLACEHOLDER_TEXT = Text.translatable("gui.crazypainting.painting_editor.title");
    private static final Text BRUSH_SETTINGS_TEXT = Text.translatable("gui.crazypainting.painting_editor.brush_settings");
    private static final Text OPACITY_TEXT = Text.translatable("gui.crazypainting.painting_editor.opacity");
    private static final Text SIGN_TEXT = Text.translatable("gui.crazypainting.painting_editor.sign");

    private static final int PALETTE_DISPLAY_SIZE = 12;

    private final CanvasTexture canvasTexture;
    private final NetSync netSync;
    private final BrushManager brushManager;

    private final ClientPlayerEntity player;

    private final int pixelSize;
    private final int easelEntityId;

    private boolean leftMouseDown;
    private boolean rightMouseDown;

    private final TextFieldWidget titleTextField;
    private final ButtonWidget signButton;

    public PaintingEditorScreen(PaintingChangeEvent event, CanvasTexture texture, ClientPlayerEntity player, int easelEntityId) {
        super(TITLE_PLACEHOLDER_TEXT);
        this.easelEntityId = easelEntityId;
        this.client = MinecraftClient.getInstance();
        this.player = player;
        assert client != null;

        canvasTexture = texture;
        pixelSize = (int)Math.max(1, 10.0f / (Math.max(canvasTexture.width/16, canvasTexture.height/16)));

        titleTextField = new TextFieldWidget(client.textRenderer, width/2 - 75, 30, 140, this.client.textRenderer.fontHeight + 5, TITLE_PLACEHOLDER_TEXT);
        titleTextField.setMaxLength(50);
        titleTextField.setVisible(true);
        titleTextField.setEditableColor(-1);
        titleTextField.setText(event.title());
        titleTextField.setPlaceholder(TITLE_PLACEHOLDER_TEXT);

        signButton = ButtonWidget.builder(SIGN_TEXT, this::sign).dimensions(width/2 - 140, height - 30, 280, this.client.textRenderer.fontHeight*2).build();

        AtomicBoolean hasChanges = new AtomicBoolean(false);
        netSync = new NetSync(canvasTexture, event.change().get(), titleTextField::getText, hasChanges, easelEntityId);
        brushManager = new BrushManager(player.getStackInHand(Hand.MAIN_HAND), canvasTexture, pixelSize, hasChanges);
    }

    private void sign(ButtonWidget ignored) {
        PaintingData data = canvasTexture.toData();
        if (data == null) return;

        netSync.signAndClose(data);
        close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        titleTextField.setX(width/2 - 75);
        titleTextField.render(context, mouseX, mouseY, deltaTicks);

        signButton.setX(width/2 - 140);
        signButton.setY(height - 30);
        signButton.render(context, mouseX, mouseY, deltaTicks);

        // Variables positioning elements on screen
        int canvasPixelX = (int)((this.width / 2f - canvasTexture.width*pixelSize/2f) / pixelSize);
        int canvasPixelY = (int)((this.height / 2f - canvasTexture.height*pixelSize/2f) / pixelSize);

        int canvasX = canvasPixelX * pixelSize;
        int canvasY = canvasPixelY * pixelSize;

        int mousePixelX = mouseX / pixelSize;
        int mousePixelY = mouseY / pixelSize;

        drawCanvas(context, canvasX, canvasY);

        boolean usePaintCursor = brushManager.updateCursor(context, mousePixelX, mousePixelY, canvasPixelX, canvasPixelY, leftMouseDown, rightMouseDown);

        // Brush size area
        context.drawText(textRenderer, BRUSH_SETTINGS_TEXT, width - 100, 64, CrazyPainting.YELLOW, true);
        drawBrushArea(context, width - 100, 64 + textRenderer.fontHeight*2, textRenderer.getWidth(BRUSH_SETTINGS_TEXT), mouseX, mouseY);
        
        // Color palette area
        int paletteTextWidth = textRenderer.getWidth(PALETTE_TEXT);
        context.drawText(textRenderer, PALETTE_TEXT, 100, 64, CrazyPainting.YELLOW, true);
        drawColorPalette(context, 100 + paletteTextWidth / 2, 65, paletteTextWidth, mouseX, mouseY);

        if (usePaintCursor) brushManager.drawCursor(context, mouseX, mouseY);
    }

    private void drawBrushArea(DrawContext context, int x, int y, int textWidth, int mouseX, int mouseY) {
        int i = 0;
        for (String category : BrushType.getCategories()) {
            boolean selected = category.equals(brushManager.category);
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
                brushManager.category = category;
            }

            if (selected) {
                int yOffset = 0;
                var brushes = BrushType.getBrushes(category);

                for (BrushType type : brushes) {
                    boolean hovered = drawBrushButton(context, type, x + textWidth - 12, y + yOffset, mouseX, mouseY);
                    if (hovered) {
                        context.drawTooltip(Text.literal(type.name), mouseX, mouseY);
                        if (leftMouseDown && brushManager.brush != type) {
                            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.75f, 1f);
                            brushManager.brush = type;
                        }
                    }

                    yOffset += 12;
                }
            }

            i++;
        }
    }

    private boolean drawBrushButton(DrawContext context, BrushType type, int x, int y, int mouseX, int mouseY) {
        boolean selected = type == brushManager.brush;
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



    private void drawColorPalette(DrawContext context, int x, int y, int textWidth, int mouseX, int mouseY) {
        if (brushManager.colors.length == 0) {
            context.drawWrappedTextWithShadow(textRenderer, PALETTE_EMPTY_TEXT, 100 - textWidth, 64 + textRenderer.fontHeight * 2, textWidth*3, CrazyPainting.RED);
            return;
        }

        for (int i = 0; i < brushManager.colors.length; i++) {
            int color = brushManager.colors[i];
            int drawX = x + (i % 2) * (PALETTE_DISPLAY_SIZE+1) - PALETTE_DISPLAY_SIZE;
            int drawY = y + textRenderer.fontHeight + (i / 2) * (PALETTE_DISPLAY_SIZE+1);
            boolean hovered = drawPaletteColor(context, color, drawX, drawY, mouseX, mouseY);
            boolean primary = leftMouseDown;

            if (hovered && (leftMouseDown || rightMouseDown)) {
                if (primary && brushManager.color != color) {
                    brushManager.selectColor(color, true);
                } else if (!primary && brushManager.color2 != color) {
                    brushManager.selectColor(color, false);
                }
            }
        }

        for (int i = 1; i < 5; i++) {
            float opacity = 1.25f - (i / 4f);
            boolean hovered = drawOpacityButton(context, x + PALETTE_DISPLAY_SIZE*2, y + textRenderer.fontHeight + (PALETTE_DISPLAY_SIZE+1)*i, opacity, mouseX, mouseY);
            if (hovered) {
                context.drawTooltip(textRenderer, Text.literal(""+opacity), mouseX, mouseY);
            }

            if (hovered && leftMouseDown && brushManager.opacity != opacity) {
                brushManager.opacity = opacity;
                player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.75f, 1.3f);

            }
        }
    }

    private void drawCanvas(DrawContext context, int canvasX, int canvasY) {
        for(int i = 0; i < canvasTexture.pixels.length; i++) {
            int drawX = canvasX + (i % canvasTexture.width) * pixelSize;
            int drawY = canvasY + (i / canvasTexture.width) * pixelSize;
            context.fill(drawX, drawY, drawX + pixelSize, drawY + pixelSize, canvasTexture.pixels[i]);
        }
    }

    private boolean drawPaletteColor(DrawContext context, int color, int x, int y, int mouseX, int mouseY) {
        int borderColor = CrazyPainting.BLACK;
        if (color == brushManager.color) borderColor = CrazyPainting.WHITE;
        else if (color == brushManager.color2) borderColor = CrazyPainting.LIGHT_GRAY;

        context.fill(x, y, x + PALETTE_DISPLAY_SIZE, y + PALETTE_DISPLAY_SIZE, color);
        context.drawBorder(x - 1, y - 1, PALETTE_DISPLAY_SIZE + 1, PALETTE_DISPLAY_SIZE + 1, borderColor);

        return mouseX >= x &&
                mouseY >= y &&
                mouseX < x + PALETTE_DISPLAY_SIZE &&
                mouseY < y + PALETTE_DISPLAY_SIZE;
    }

    private boolean drawOpacityButton(DrawContext context, int x, int y, float opacity, int mouseX, int mouseY) {
        context.fill(x + 1, y + 1, x + PALETTE_DISPLAY_SIZE - 2, y + PALETTE_DISPLAY_SIZE - 2, ColorHelper.setOpacity(brushManager.color, opacity));
        context.drawBorder(x - 1, y - 1, PALETTE_DISPLAY_SIZE + 1, PALETTE_DISPLAY_SIZE + 1, brushManager.opacity == opacity ? CrazyPainting.WHITE : CrazyPainting.BLACK);

        return mouseX >= x &&
                mouseY >= y &&
                mouseX < x + PALETTE_DISPLAY_SIZE &&
                mouseY < y + PALETTE_DISPLAY_SIZE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) leftMouseDown = true;
        else if (button == 1) rightMouseDown = true;

        boolean hovered = mouseX >= titleTextField.getX() && mouseY < titleTextField.getRight() && mouseY >= titleTextField.getY() && mouseY < titleTextField.getBottom();

        if (hovered || this.titleTextField.mouseClicked(mouseX, mouseY, button)) {
            this.titleTextField.setFocused(true);
            return true;
        }
        titleTextField.setFocused(false);

        if (signButton.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (titleTextField.charTyped(chr, modifiers)) return true;

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (titleTextField.keyPressed(keyCode, scanCode, modifiers)) return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) leftMouseDown = false;
        else if (button == 1) rightMouseDown = false;

        if (signButton.mouseReleased(mouseX, mouseY, button)) return true;
        brushManager.mouseReleased();

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        super.tick();
        netSync.tick();
    }

    @Override
    public void close() {
        netSync.close();
        client.mouse.lockCursor();
        super.close();
    }
}
