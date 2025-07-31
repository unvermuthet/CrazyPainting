package com.github.omoflop.crazypainting.mixin;

import com.github.omoflop.crazypainting.content.CrazyComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ShieldDecorationRecipe.class)
public class ShieldDecorationRecipeMixin {

    @Inject(method = "matches(Lnet/minecraft/recipe/input/CraftingRecipeInput;Lnet/minecraft/world/World;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void matchesInjection(CraftingRecipeInput craftingRecipeInput, World arg1, CallbackInfoReturnable<Boolean> cir, boolean bl, boolean bl2, int i, ItemStack itemStack) {
        // Don't allow banners to be added to shields with canvas data!
        if (itemStack.get(CrazyComponents.CANVAS_DATA) != null) {
            cir.setReturnValue(false);
        }
    }

}
