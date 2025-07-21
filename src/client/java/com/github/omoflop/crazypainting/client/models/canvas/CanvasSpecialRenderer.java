package com.github.omoflop.crazypainting.client.models.canvas;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.CanvasTexture;
import com.github.omoflop.crazypainting.client.CanvasTextureManager;
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
    public static final Identifier WHITE = Identifier.of("textures/misc/white.png");
    public static final Identifier BACK = Identifier.of("textures/block/oak_planks.png");

    @Override
    public void render(@Nullable Data data, ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        Identifier textureId = WHITE;

        boolean isGui = displayContext == ItemDisplayContext.GUI;
        boolean isGround = displayContext == ItemDisplayContext.GROUND;
        boolean isThirdPerson = displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;


        boolean glow = false;
        if (data != null) {
            glow = data.glow;
            Optional<CanvasTexture> texture = CanvasTextureManager.request(data.canvasId);

            if (texture.isPresent() && texture.get().isReady()) {
                textureId = texture.get().textureId;
            }
        }

        // Make it appear full bright in GUI
        if (isGui) glow = true;
        var consumer = vertexConsumers.getBuffer(glow ? RenderLayer.getEyes(textureId) : RenderLayer.getItemEntityTranslucentCull(textureId));

        int width = data == null ? 1 : data.width;
        int height = data == null ? 1 : data.height;

        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.scale(1/32f,1/32f,1/32f);
        matrices.translate(0, 0, -16);


        if (isGui || displayContext == ItemDisplayContext.FIXED) {
            if (data != null) {
                int longestSide = Math.max(data.width, data.height);
                matrices.translate(0, -32, 0);
                matrices.scale(2, 2, 1);
                matrices.scale(1f/longestSide, 1f/longestSide, 1);
                if (data.width > data.height) {
                    matrices.translate(0, (data.width-data.height)*8, 0);
                } else if (data.width < data.height) {
                    matrices.translate((data.height-data.width)*8, 0, 0);
                }
            }

        } else  {
            matrices.translate(0, 12, 0);
            matrices.translate(16 - width*8, -16 - height*16, 0);
        }

        Matrix4f m = matrices.peek().getPositionMatrix();
        MatrixStack.Entry pose = matrices.peek();

        Vector3f normal = Direction.NORTH.getUnitVector();

        boolean drawBack = isGround || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;

        addVertex(consumer, m, pose, 0,        16*height, -1.5f, 0f, 1f, light, normal);
        addVertex(consumer, m, pose, 16*width, 16*height, -1.5f, 1f, 1f, light, normal);
        addVertex(consumer, m, pose, 16*width, 0,         -1.5f, 1f, 0f, light, normal);
        addVertex(consumer, m, pose, 0,        0,         -1.5f, 0f, 0f, light, normal);

        if (drawBack) {
            VertexConsumer back = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(BACK));
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    addVertex(back, m, pose, x*16,     y*16,       -1.5f, 0f, 0f, light, normal);
                    addVertex(back, m, pose, x*16 + 16, y*16,      -1.5f, 1f, 0f, light, normal);
                    addVertex(back, m, pose, x*16 + 16, y*16 + 16, -1.5f, 1f, 1f, light, normal);
                    addVertex(back, m, pose, x*16,      y*16 + 16, -1.5f, 0f, 1f, light, normal);
                }
            }
        }

        matrices.pop();
    }

    private void addVertex(VertexConsumer consumer, Matrix4f m, MatrixStack.Entry pose, float x, float y, float z, float tx, float ty, int light, Vector3f normal) {
        normal = normal.mul(pose.getNormalMatrix());
        consumer.vertex(m, x, y, z)
                .color(0xFFFFFFFF)
                .texture(tx, ty)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normal.x, normal.y, normal.z);
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
