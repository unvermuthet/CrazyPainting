package com.github.omoflop.crazypainting.client.screens;

import com.github.omoflop.crazypainting.entities.EaselEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PaintingViewerScreen extends Screen {
    public PaintingViewerScreen(EaselEntity easel) {
        super(Text.translatable("gui.crazypainting.painting_viewer.title"));
    }
}
