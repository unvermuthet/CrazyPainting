package com.github.omoflop.crazypainting.client.screens;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.CanvasTexture;
import com.github.omoflop.crazypainting.client.CanvasTextureManager;
import com.github.omoflop.crazypainting.entities.EaselEntity;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.items.PaletteItem;
import com.github.omoflop.crazypainting.network.old.c2s.SendPaintingC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PaintingEditorScreen extends Screen {
    private static Identifier EDITOR_TEXTURE_ID = CrazyPainting.id("textures/gui/editor.png");
    private static final int PALETTE_DISPLAY_SIZE = 12;
    private static int lastSelectedColor;

    private final EaselEntity easel;
    private CanvasTexture canvasTexture = null;

    private int pixelSize = 1;

    private int selectedColor = -1;
    private String canvasName;

    private boolean leftMouseDown;
    private boolean rightMouseDown;

    private ItemStack paletteStack;
    private int[] colors;
    private int canvasId;

    public PaintingEditorScreen(EaselEntity easel) {
        super(Text.translatable("gui.crazypainting.painting_editor.title"));
        this.client = MinecraftClient.getInstance();
        this.easel = easel;

        ItemStack stack = easel.getEquippedStack(EquipmentSlot.MAINHAND);
        canvasId = CanvasItem.getCanvasId(stack);


        Text customName = stack.getCustomName();
        if (customName == null) {
            canvasName = "Untitled Painting";
        } else {
            canvasName = customName.getString();
        }

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


        int canvasPixelX = (int)((this.width / 2f - canvasTexture.width*pixelSize/2f) / pixelSize);
        int canvasPixelY = (int)((this.height / 2f - canvasTexture.height*pixelSize/2f) / pixelSize);

        int canvasX = canvasPixelX * pixelSize;
        int canvasY = canvasPixelY * pixelSize;

        // Draw Title
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(canvasName), width / 2, 64, CrazyPainting.YELLOW);

        for(int i = 0; i < canvasTexture.pixels.length; i++) {
            int drawX = canvasX + (i % canvasTexture.width) * pixelSize;
            int drawY = canvasY + (i / canvasTexture.width) * pixelSize;
            context.fill(drawX, drawY, drawX + pixelSize, drawY + pixelSize, canvasTexture.pixels[i]);
        }

        //context.fill(canvasX, canvasY, canvasX + canvasWidthPixels*pixelSize, canvasY + canvasHeightPixels*pixelSize, CrazyPainting.WHITE);

        int mousePixelX = mouseX / pixelSize;
        int mousePixelY = mouseY / pixelSize;

        // Draw pixel cursor
        boolean cursorHidden = false;
        if (mousePixelX >= canvasPixelX && mousePixelY >= canvasPixelY && mousePixelX < canvasPixelX + canvasTexture.width && mousePixelY < canvasPixelY + canvasTexture.height) {
            context.drawBorder(mousePixelX * pixelSize - 1, mousePixelY * pixelSize - 1, pixelSize + 2, pixelSize + 2, selectedColor);

            if (selectedColor != -1 && leftMouseDown) {
                setPixel(mousePixelX - canvasPixelX, mousePixelY - canvasPixelY, selectedColor);
            }
            if (rightMouseDown) {
                setPixel(mousePixelX - canvasPixelX, mousePixelY - canvasPixelY, CrazyPainting.WHITE);
            }

            if (selectedColor != -1) {
                GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
                cursorHidden = true;
            }
        } else {
            GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            cursorHidden = false;
        }

        // Brush size area
        context.drawText(textRenderer, Text.translatable("gui.crazypainting.painting_editor.brush_size"), width - 100, 64, CrazyPainting.YELLOW, true);

        // Color palette area
        context.drawText(textRenderer, Text.translatable("gui.crazypainting.palette"), 100, 64, CrazyPainting.YELLOW, true);
        int textWidth = textRenderer.getWidth(Text.translatable("gui.crazypainting.palette"));

        if (colors == null) {
            context.drawWrappedTextWithShadow(textRenderer, Text.translatable("gui.crazypainting.palette.empty"), 100 - textWidth, 64 + textRenderer.fontHeight * 2, textWidth*3, CrazyPainting.RED);
            return;
        }
        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            int drawX = 100 + textWidth/2 + (i % 2) * (PALETTE_DISPLAY_SIZE+1) - PALETTE_DISPLAY_SIZE;
            int drawY = 65 + textRenderer.fontHeight + (i / 2) * (PALETTE_DISPLAY_SIZE+1);
            boolean hovered = drawPaletteColor(context, color, drawX, drawY, mouseX, mouseY);
            if (leftMouseDown && hovered) {
                if (selectedColor != color) {
                    client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
                }
                selectedColor = color;
                lastSelectedColor = color;
            }
        }

        if (cursorHidden && selectedColor != -1) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 16, 0, 16, 16, 64, 64, 0xFFFFFFFF);
            if (selectedColor == CrazyPainting.TRANSPARENT) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 0, 16, 16, 16, 64, 64, 0xFFFFFFFF);
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITOR_TEXTURE_ID, mouseX, mouseY, 0, 0, 16, 16, 64, 64, selectedColor);
            }
        }

    }

    public boolean drawPaletteColor(DrawContext context, int color, int x, int y, int mouseX, int mouseY) {
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

        if (canvasTexture != null && (button == 0 || button == 1)) {
            canvasTexture.updateTexture();
            ClientPlayNetworking.send(new SendPaintingC2SPacket(easel.getId(), CanvasTextureManager.getVersion(canvasId), false, canvasTexture.pixels));
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void setPixel(int x, int y, int color) {
        canvasTexture.pixels[x + y * canvasTexture.width] = color;
    }

    public int getPixel(int x, int y) {
        return canvasTexture.pixels[x + y * canvasTexture.width];
    }

    @Override
    public void tick() {
        super.tick();

        var colors = PaletteItem.getColors(paletteStack);
        if (!colors.isEmpty()) {
            this.colors = colors.stream().mapToInt(Integer::intValue).toArray();
            if (colors.contains(lastSelectedColor)) {
                selectedColor = lastSelectedColor;
            } else {
                selectedColor = this.colors[0];
            }
        }

        if (canvasTexture == null) {
            var optional = CanvasTextureManager.request(canvasId);

            if (optional.isPresent()) {
                canvasTexture = optional.get();
                pixelSize = (int)Math.max(1, 10.0f / (Math.max(canvasTexture.width/16, canvasTexture.height/16)));
            }
        }


    }

    @Override
    public void close() {
        super.close();
        client.mouse.lockCursor();
    }
}
