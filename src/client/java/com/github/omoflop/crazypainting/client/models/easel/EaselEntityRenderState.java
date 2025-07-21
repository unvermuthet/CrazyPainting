package com.github.omoflop.crazypainting.client.models.easel;

import com.github.omoflop.crazypainting.client.models.ItemDisplaying;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;

public class EaselEntityRenderState extends LivingEntityRenderState implements ItemDisplaying {
    public float yaw;
    public float timeSinceLastHit;
    public ItemStack displayItem = ItemStack.EMPTY;
    public ItemRenderState displayItemState = new ItemRenderState();

    @Override
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @Override
    public ItemRenderState getDisplayItemState() {
        return displayItemState;
    }
}
