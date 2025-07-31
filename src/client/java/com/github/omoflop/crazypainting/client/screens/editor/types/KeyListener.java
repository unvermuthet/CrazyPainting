package com.github.omoflop.crazypainting.client.screens.editor.types;

public interface KeyListener {
    boolean onCharTyped(char chr, int modifiers);
    boolean onKeyPressed(int keyCode, int scanCode, int modifiers);
    boolean onKeyReleased(int keyCode, int scanCode, int modifiers);
}
