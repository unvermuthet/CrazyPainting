package com.github.omoflop.crazypainting.client.screens.editor.widgets;

import com.github.omoflop.crazypainting.client.screens.editor.NetSync;
import com.github.omoflop.crazypainting.client.screens.editor.types.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SignWidget extends EditorWidget implements KeyListener, MouseListener, Tickable, Renderable {
    private static final Text SIGN_TEXT = Text.translatable("gui.crazypainting.painting_editor.sign");

    private final TextFieldWidget textField;
    private final ButtonWidget button;
    private EditorState state;

    public SignWidget(EditorState state, NetSync netSync) {
        this.state = state;
        this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 32, 32, Text.empty());
        this.button = ButtonWidget.builder(SIGN_TEXT, (ignored) -> netSync.signAndClose(textField.getText())).dimensions(0, 0, 32, 32).build();
        button.active = false;

        textField.setMaxLength(32);
        textField.setVisible(true);
        textField.setEditableColor(-1);
        textField.setText("Untitled Painting");
        textField.setCursorToStart(false);
    }

    @Override
    public void calculateSize(int screenWidth, int screenHeight) {
        super.calculateSize(screenWidth, screenHeight);
        textField.setX(x);
        textField.setWidth(width);
        textField.setY(y);
        textField.setHeight(height / 2);

        button.setX(x);
        button.setWidth(width);
        button.setY(y + height/2);
        button.setHeight(height/2);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return textField.isFocused() && textField.charTyped(chr, modifiers);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return textField.isFocused() && textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        return textField.isFocused() && textField.keyReleased(keyCode, scanCode, modifiers);

    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int button) {
        if (textField.mouseClicked(mouseX, mouseY, button)) {
            textField.setFocused(true);
            state.shortcutsEnabled = false;
            return true;
        } else {
            state.shortcutsEnabled = true;
            textField.setFocused(false);
        }
        if (this.button.mouseClicked(mouseX, mouseY, button)) return true;

        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (textField.mouseReleased(mouseX, mouseY, button)) return true;
        if (this.button.mouseReleased(mouseX, mouseY, button)) return true;

        return false;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public void tick() {
        button.active = !textField.getText().equals("Untitled Painting");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        button.render(context, mouseX, mouseY, deltaTicks);
        textField.render(context, mouseX, mouseY, deltaTicks);
    }
}
