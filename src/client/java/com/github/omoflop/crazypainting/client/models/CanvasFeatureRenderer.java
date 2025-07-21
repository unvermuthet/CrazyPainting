package com.github.omoflop.crazypainting.client.models;

import com.github.omoflop.crazypainting.items.CanvasItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class CanvasFeatureRenderer<S extends EntityRenderState & ItemDisplaying, M extends EntityModel<S>> extends FeatureRenderer<S, M> {
    public CanvasFeatureRenderer(FeatureRendererContext<S, M> ctx) {
        super(ctx);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S state, float f, float g) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(5.5f));
        matrixStack.translate(0, -0.8, -.110);
        matrixStack.scale(1.25f, 1.25f, 1.25f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        if (state.getDisplayItem().getItem() instanceof CanvasItem) {
            matrixStack.scale(2, 2, .66f);
        }

        state.getDisplayItemState().render(matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}
