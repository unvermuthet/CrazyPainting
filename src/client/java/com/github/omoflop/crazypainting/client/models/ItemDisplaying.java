package com.github.omoflop.crazypainting.client.models;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;

public interface ItemDisplaying {
    ItemStack getDisplayItem();
    ItemRenderState getDisplayItemState();
}
