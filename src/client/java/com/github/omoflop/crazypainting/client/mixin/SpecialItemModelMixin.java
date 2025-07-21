package com.github.omoflop.crazypainting.client.mixin;

import com.github.omoflop.crazypainting.client.models.canvas.CanvasSpecialRenderer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialItemModel.class)
public class SpecialItemModelMixin<T> {
    @Shadow @Final private SpecialModelRenderer<T> specialModelType;

    @Shadow @Final private ModelSettings settings;
    @Unique
    private boolean crazypainting$isSpecial;

    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/special/SpecialModelRenderer;getData(Lnet/minecraft/item/ItemStack;)Ljava/lang/Object;"))
    public T crazypainting$grabData(T data) {
        crazypainting$isSpecial = (data instanceof CanvasSpecialRenderer.Data);
        return data;
    }

    @Inject(method = "update", at = @At(value = "RETURN"))
    public void crazypainting$update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, ClientWorld world, LivingEntity user, int seed, CallbackInfo ci) {
        if (crazypainting$isSpecial) {
            state.markAnimated();
            state.setOversizedInGui(true);
        }
    }
}
