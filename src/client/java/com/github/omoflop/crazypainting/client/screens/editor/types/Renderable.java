package com.github.omoflop.crazypainting.client.screens.editor.types;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public interface Renderable {
    void render(DrawContext context, int mouseX, int mouseY, float deltaTicks);
}
