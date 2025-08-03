package com.github.omoflop.crazypainting.client.screens;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.screens.editor.widgets.*;
import com.github.omoflop.crazypainting.client.screens.editor.NetSync;
import com.github.omoflop.crazypainting.client.screens.editor.types.EditorState;
import com.github.omoflop.crazypainting.client.texture.CanvasTexture;
import com.github.omoflop.crazypainting.items.PaletteItem;
import com.github.omoflop.crazypainting.network.event.PaintingChangeEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class PaintingEditorScreen extends AbstractPaintingScreen {
    public static final Identifier EDITOR_TEXTURE_ID = CrazyPainting.id("textures/gui/editor.png");

    private static final Text TITLE_PLACEHOLDER_TEXT = Text.translatable("gui.crazypainting.painting_editor.title");

    private final CanvasWidget canvasWidget;
    private final ColorPickerWidget colorPicker;
    private final BrushPickerWidget brushPicker;
    private final HelpWidget help;
    private final SignWidget sign;

    private final NetSync netSync;

    public PaintingEditorScreen(PaintingChangeEvent event, CanvasTexture texture, ClientPlayerEntity player, int easelEntityId) {
        super(TITLE_PLACEHOLDER_TEXT);
        this.client = MinecraftClient.getInstance();
        assert client != null;

        AtomicBoolean hasChanges = new AtomicBoolean(false);
        netSync = new NetSync(texture, event.change().orElseThrow(), this::close, hasChanges, easelEntityId);

        ItemStack paletteStack = player.getEquippedStack(EquipmentSlot.MAINHAND);
        Collection<Integer> colors;
        if (paletteStack.isEmpty() || !(paletteStack.getItem() instanceof PaletteItem)) colors = List.of();
        else colors = PaletteItem.getColors(paletteStack);

        EditorState state = new EditorState();
        canvasWidget = new CanvasWidget(state, texture, hasChanges, colors);
        colorPicker  = new ColorPickerWidget(state, colors);
        brushPicker  = new BrushPickerWidget(state);
        sign = new SignWidget(state, netSync);
        help  = new HelpWidget();
        drawDebugBoundaries = false;
    }

    @Override
    protected void init() {
        super.init();

        addWidget(canvasWidget);
        addWidget(colorPicker);
        addWidget(brushPicker);
        addWidget(help);
        addWidget(sign);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    @Override
    protected void recalculateWidgetPositions(int width, int height) {
        super.recalculateWidgetPositions(width, height);

        canvasWidget.alignXCenter(width);
        canvasWidget.alignYCenter(height);

        final int sideWidth = width / 10;
        final int sideHeight = height / 2;

        colorPicker.alignXBetween(0, canvasWidget.x);
        colorPicker.x = Math.max(colorPicker.x, 0);
        colorPicker.alignYCenter(height);
        colorPicker.width = sideWidth;
        colorPicker.height = sideHeight;

        brushPicker.alignXBetween(canvasWidget.right(), width);
        brushPicker.x = Math.min(brushPicker.x, width - brushPicker.width);
        brushPicker.alignYCenter(height);
        brushPicker.width = sideWidth;
        brushPicker.height = sideHeight;

        help.x = 2;
        help.y = 2;
        help.width = 12;
        help.height = 12;

        sign.width = canvasWidget.width;
        sign.height = 30;
        sign.alignXCenter(width);
        sign.y = canvasWidget.bottom() + 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void tick() {
        netSync.tick();
        super.tick();
    }

    @Override
    public void close() {
        super.close();
        netSync.close();
        MinecraftClient.getInstance().mouse.lockCursor();
    }
}
