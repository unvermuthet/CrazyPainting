package com.github.omoflop.crazypainting.client.mixin;

import com.github.omoflop.crazypainting.client.models.CanvasRenderer;
import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ShieldModelRenderer.class)
public class ShieldModelRendererMixin {

    @Inject(method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IIZ)V", at = @At(value = "TAIL"))
    private void render(ComponentMap componentMap, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl, CallbackInfo ci) {
        CanvasDataComponent data = componentMap.get(CrazyComponents.CANVAS_DATA);
        if (data == null) return;

        Optional<Identifier> textureId = CanvasRenderer.tryGetCanvasId(data.id());
        if (textureId.isEmpty()) return;

        matrixStack.push();
        boolean glow = itemDisplayContext == ItemDisplayContext.GUI || data.glow();

        CanvasRenderer.prepareForShield(matrixStack);
        CanvasRenderer.renderVertices(vertexConsumerProvider, matrixStack, false, textureId.get(), 1, 2, glow, i);
        matrixStack.pop();
    }

}
