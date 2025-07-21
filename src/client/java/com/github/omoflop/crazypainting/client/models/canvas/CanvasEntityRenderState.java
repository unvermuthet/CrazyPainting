package com.github.omoflop.crazypainting.client.models.canvas;

import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CanvasEntityRenderState extends EntityRenderState {
    public Direction facing = Direction.NORTH;
    public byte rotation;
    public boolean glow;
    public ItemStack displayItem = ItemStack.EMPTY;
    public ItemRenderState displayItemState = new ItemRenderState();
}
