package com.github.omoflop.crazypainting.client.screens.editor.types;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.ColorHelper;
import com.github.omoflop.crazypainting.client.screens.editor.BrushType;
import com.github.omoflop.crazypainting.content.CrazySounds;
import org.jetbrains.annotations.Nullable;

import static com.github.omoflop.crazypainting.client.CrazyPaintingClient.play;

public class EditorState {
    private static @Nullable EditorState last;

    public EditorState() {
        if (last != null) {
            primaryColor = last.primaryColor;
            secondaryColor = last.secondaryColor;
            opacity = last.opacity;
            brushCategory = last.brushCategory;
            brushType = last.brushType;
            colorPickerActive = last.colorPickerActive;
        }

        last = this;
    }

    public int primaryColor;
    public int secondaryColor;
    public float opacity = 1f;

    public String brushCategory = "square";
    public BrushType brushType = BrushType.getBrush(brushCategory, "1x1");
    public boolean colorPickerActive;

    public boolean shortcutsEnabled = true;

    public void setColorPicker(boolean b) {
        if (colorPickerActive == b) return;
        play(CrazySounds.COLOR_PICKER_USE, b ? 1 : 0.85f);
        colorPickerActive = b;
    }

    public int getPrimaryColor() {
        return primaryColor == CrazyPainting.TRANSPARENT ? primaryColor : ColorHelper.setOpacity(primaryColor, opacity);
    }

    public int getSecondaryColor() {
        return secondaryColor == CrazyPainting.TRANSPARENT ? secondaryColor : ColorHelper.setOpacity(secondaryColor, opacity);
    }
}
