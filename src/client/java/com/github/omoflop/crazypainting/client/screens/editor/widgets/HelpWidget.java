package com.github.omoflop.crazypainting.client.screens.editor.widgets;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.screens.PaintingEditorScreen;
import com.github.omoflop.crazypainting.client.screens.editor.types.EditorWidget;
import com.github.omoflop.crazypainting.client.screens.editor.types.Renderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HelpWidget extends EditorWidget implements Renderable {
    private final TextRenderer textRenderer;

    public HelpWidget() {
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(x + 1, y + 1, x + width - 2, y + height - 2, CrazyPainting.WHITE);
        context.drawBorder(x - 1, y - 1, width + 1, height + 1, CrazyPainting.BLACK);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, PaintingEditorScreen.EDITOR_TEXTURE_ID, x + (16 - width)/2, y + (16 - height)/2, 32, 16, 16, 16, 64, 64, 0xFFFFFFFF);

        if (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
            int lineCount = Integer.parseInt(Text.translatable("gui.crazypainting.painting_editor.tutorial.count").getString());
            int width = 0;
            List<Text> lines = new ArrayList<>();
            for (int i = 1; i <= lineCount; i++) {
                Text curLine = Text.translatable("gui.crazypainting.painting_editor.tutorial.%s".formatted(i));
                if (curLine.getString().endsWith(":")) {
                    if (i > 1) {
                        lines.add(Text.literal(""));
                    }
                    curLine = curLine.copy().withColor(CrazyPainting.YELLOW);
                }
                width = Math.max(width, textRenderer.getWidth(curLine));
                lines.add(curLine);
            }

            int height = textRenderer.fontHeight * lines.size();
            TooltipBackgroundRenderer.render(context, mouseX+8, mouseY+8, width, height, null);

            int i = 0;
            for (Text line : lines) {
                context.drawText(textRenderer, line, mouseX + 8, mouseY + i + 8, 0xFFFFFFFF, false);
                i += textRenderer.fontHeight;
            }
        }
    }
}
