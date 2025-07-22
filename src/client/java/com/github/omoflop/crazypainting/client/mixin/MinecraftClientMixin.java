package com.github.omoflop.crazypainting.client.mixin;

import com.github.omoflop.crazypainting.client.texture.CanvasTextureManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        CanvasTextureManager.unloadAll();
    }

}
