package com.github.omoflop.crazypainting.client.screens.editor.widgets;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.CrazyPaintingClient;
import com.github.omoflop.crazypainting.client.screens.editor.BrushType;
import com.github.omoflop.crazypainting.client.screens.editor.types.EditorState;
import com.github.omoflop.crazypainting.client.screens.editor.types.EditorWidget;
import com.github.omoflop.crazypainting.client.screens.editor.types.MouseListener;
import com.github.omoflop.crazypainting.client.screens.editor.types.Renderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class BrushPickerWidget extends EditorWidget implements Renderable, MouseListener {
    private static final Text BRUSH_SETTINGS_TEXT = Text.translatable("gui.crazypainting.painting_editor.brush_settings");

    private final EditorState state;
    private final TextRenderer textRenderer;
    private boolean leftJustDown;

    public BrushPickerWidget(EditorState state) {
        this.state = state;
        textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawCenteredTextWithShadow(textRenderer, BRUSH_SETTINGS_TEXT, centerX(), top() - textRenderer.fontHeight, CrazyPainting.YELLOW);

        int i = 0;
        for (String category : BrushType.getCategories()) {
            if (drawBrushCategoryButton(context, mouseX, mouseY, category, x, y+i*textRenderer.fontHeight) && leftJustDown && !state.brushCategory.equals(category)) {
                CrazyPaintingClient.click(2);
                state.brushCategory = category;
                leftJustDown = false;
            }

            i++;
        }

        i = 0;
        final int size = 16;
        for (BrushType type : BrushType.getBrushes(state.brushCategory)) {
            if (drawBrushSelectButton(context, mouseX, mouseY, right() - size, y+i*size, size, size, type) && leftJustDown && state.brushType != type) {
                CrazyPaintingClient.click(1.7f);
                leftJustDown = false;
                state.brushType = type;
            }
            i++;
        }

        leftJustDown = false;
    }

    private boolean drawBrushCategoryButton(DrawContext context, int mouseX, int mouseY, String category, int x, int y) {
        Text text = Text.literal(category);

        int width = textRenderer.getWidth(text);
        int height = textRenderer.fontHeight;
        boolean mouseHovered = x <= mouseX && y <= mouseY && x+width > mouseX && y+height > mouseY;

        if (mouseHovered)
            context.fill(x, y, x+width, y+height, CrazyPainting.LIGHT_GRAY);

        int textColor = CrazyPainting.LIME;
        if (state.brushCategory.equals(category)) {
            textColor = CrazyPainting.WHITE;
        }

        context.drawTextWithShadow(textRenderer, text, x, y, textColor);

        return mouseHovered;
    }

    private boolean drawBrushSelectButton(DrawContext context, int mouseX, int mouseY, int x, int y, int width, int height, BrushType brush) {
        boolean mouseHovered = x <= mouseX && y <= mouseY && x+width > mouseX && y+height > mouseY;

        boolean selected = state.brushType == brush;

        int borderColor = selected ? CrazyPainting.WHITE : CrazyPainting.BLACK;
        int innerColor = selected ? CrazyPainting.LIME : CrazyPainting.WHITE;

        if (!selected && mouseHovered) {
            innerColor = CrazyPainting.LIGHT_GRAY;
        }

        context.drawBorder(x, y, width, height, borderColor);
        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, innerColor);

        brush.iteratePatternCentered(width/2, height/2, (px, py, ignored) -> {
            context.fill(x + px, y + py, x + px + 1, y + py + 1, selected ? CrazyPainting.WHITE : CrazyPainting.BLACK);
        });

        return mouseHovered;
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int button) {
        if (button == 0) leftJustDown = true;

        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }
}
