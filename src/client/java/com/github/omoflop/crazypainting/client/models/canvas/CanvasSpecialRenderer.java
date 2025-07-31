package com.github.omoflop.crazypainting.client.models.canvas;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.models.CanvasRenderer;
import com.github.omoflop.crazypainting.client.texture.CanvasTexture;
import com.github.omoflop.crazypainting.client.texture.CanvasTextureManager;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class CanvasSpecialRenderer implements SpecialModelRenderer<CanvasSpecialRenderer.Data> {
    public static final Identifier SPECIAL_ID = CrazyPainting.id("canvas");
    @Override
    public void render(@Nullable Data data, ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        CanvasRenderer.render(data, displayContext, matrices, vertexConsumers, light);
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {

    }

    @Override
    public @Nullable Data getData(ItemStack stack) {
        if (!(stack.getItem() instanceof CanvasItem item)) return Data.EMPTY;

        return new Data(item.width, item.height, CanvasItem.getCanvasId(stack), CanvasItem.getGlow(stack));
    }

    public record Data(int width, int height, int canvasId, boolean glow) {
        public static final Data EMPTY = new Data(1, 1, -1, false);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<CanvasSpecialRenderer.Unbaked> CODEC = MapCodec.unit(new CanvasSpecialRenderer.Unbaked());


        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new CanvasSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> getCodec() {
            return CODEC;
        }
    }
}
