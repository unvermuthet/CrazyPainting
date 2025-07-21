package com.github.omoflop.crazypainting.client.models.canvas;

import com.github.omoflop.crazypainting.entities.CanvasEntity;
import com.github.omoflop.crazypainting.items.CanvasItem;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class CanvasEntityRenderer extends EntityRenderer<CanvasEntity, CanvasEntityRenderState> {
    private final ItemModelManager itemModelResolver;

    public CanvasEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        itemModelResolver = context.getItemModelManager();

    }

    @Override
    public CanvasEntityRenderState createRenderState() {
        return new CanvasEntityRenderState();
    }

    @Override
    public void updateRenderState(CanvasEntity entity, CanvasEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.facing = entity.getHorizontalFacing();
        ItemStack canvasItem = entity.getDataTracker().get(CanvasEntity.CANVAS_ITEM);
        if (!(canvasItem.getItem() instanceof CanvasItem)) return;
        state.rotation = entity.getDataTracker().get(CanvasEntity.ROTATION);
        state.glow = CanvasItem.getGlow(canvasItem);
        state.displayItem = canvasItem;
        itemModelResolver.updateForNonLivingEntity(state.displayItemState, state.displayItem, ItemDisplayContext.FIXED, entity);

    }

    @Override
    public void render(CanvasEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!(state.displayItem.getItem() instanceof CanvasItem canvas)) return;


        matrices.push();
        float f;
        float g;
        if (state.facing.getAxis().isHorizontal()) {
            f = 0.0f;
            g = 180.0f - state.facing.getPositiveHorizontalDegrees();
        } else {
            f = -90 * state.facing.getDirection().offset();
            g = 180.0f;
        }
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
        matrices.translate(0,0,1.2/16f);

        // undo ui shrink lol
        int biggest = Math.max(canvas.width, canvas.height);
        matrices.scale(biggest, biggest, 1);

        // Rotate!
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.rotation*90));

        state.displayItemState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();

        super.render(state, matrices, vertexConsumers, light);
    }
}
