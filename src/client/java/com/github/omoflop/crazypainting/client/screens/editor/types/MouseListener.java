package com.github.omoflop.crazypainting.client.screens.editor.types;

public interface MouseListener {
    boolean onMousePressed(double mouseX, double mouseY, int button);

    boolean onMouseReleased(double mouseX, double mouseY, int button);

    boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
}
