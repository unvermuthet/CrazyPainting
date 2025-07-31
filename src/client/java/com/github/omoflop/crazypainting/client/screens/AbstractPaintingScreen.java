package com.github.omoflop.crazypainting.client.screens;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.screens.editor.types.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPaintingScreen extends Screen {
    private final List<EditorWidget> widgets = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();
    private final List<Tickable> tickables = new ArrayList<>();
    private final List<MouseListener> mouseListeners = new ArrayList<>();
    private final List<KeyListener> keyListeners = new ArrayList<>();

    private int evilHack = 10;

    protected boolean drawDebugBoundaries = false;

    protected AbstractPaintingScreen(Text title) {
        super(title);
    }

    //============----------------============//
    //============ Helper Methods ============//
    //============----------------============//
    protected <T extends EditorWidget> void addWidget(T widget) {
        widgets.add(widget);
        if (widget instanceof Renderable renderable) renderables.add(renderable);
        if (widget instanceof Tickable tickable) tickables.add(tickable);
        if (widget instanceof MouseListener mouseListener) mouseListeners.add(mouseListener);
        if (widget instanceof KeyListener keyListener) keyListeners.add(keyListener);
    }

    protected void removeWidget(EditorWidget widget) {
        widgets.remove(widget);
        if (widget instanceof Renderable renderable) renderables.remove(renderable);
        if (widget instanceof Tickable tickable) tickables.remove(tickable);
        if (widget instanceof MouseListener mouseListener) mouseListeners.remove(mouseListener);
        if (widget instanceof KeyListener keyListener) keyListeners.remove(keyListener);
    }

    protected void recalculateWidgetPositions(int width, int height) {
        for (EditorWidget widget : widgets) {
            widget.calculateSize(width, height);
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        evilHack = 10;
    }

    //============-----------------============//
    //============ Lifetime events ============//
    //============-----------------============//
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (evilHack > 0) {
            recalculateWidgetPositions(width, height);
            evilHack -= 1;
        }
        for (Renderable r : renderables) r.render(context, mouseX, mouseY, deltaTicks);

        super.render(context, mouseX, mouseY, deltaTicks);
        if (drawDebugBoundaries) {
            for (EditorWidget widget : widgets) {
                context.drawBorder(widget.x, widget.y, widget.width, widget.height, CrazyPainting.YELLOW);
            }
        }

    }

    @Override
    public void tick() {
        tickables.forEach(Tickable::tick);
        super.tick();
    }

    //============-----------------============//
    //============ Keyboard Events ============//
    //============-----------------============//
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (KeyListener w : keyListeners) {
            if (w.onKeyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (KeyListener w : keyListeners) {
            if (w.onKeyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (KeyListener w : keyListeners) {
            if (w.onCharTyped(chr, modifiers)) {
                return true;
            }
        }

        return super.charTyped(chr, modifiers);
    }


    //============--------------============//
    //============ Mouse Events ============//
    //============--------------============//
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (MouseListener w : mouseListeners) {
            if (w.onMousePressed(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (MouseListener w : mouseListeners) {
            if (w.onMouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (MouseListener w : mouseListeners) {
            if (w.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
