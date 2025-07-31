package com.github.omoflop.crazypainting.client.models;

import com.github.omoflop.crazypainting.client.compat.IrisCompat;
import com.github.omoflop.crazypainting.client.models.canvas.CanvasSpecialRenderer;
import com.github.omoflop.crazypainting.client.texture.CanvasTexture;
import com.github.omoflop.crazypainting.client.texture.CanvasTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;

public final class CanvasRenderer {

    public static final Identifier WHITE = Identifier.of("textures/misc/white.png");
    public static final Identifier BACK = Identifier.of("textures/block/oak_planks.png");

    public static void render(@Nullable CanvasSpecialRenderer.Data data, ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Identifier textureId = WHITE;

        boolean isGui = displayContext == ItemDisplayContext.GUI;
        boolean isGround = displayContext == ItemDisplayContext.GROUND;

        boolean glow = false;
        if (data != null) {
            glow = data.glow();
            Optional<Identifier> id = tryGetCanvasId(data.canvasId());
            if (id.isPresent()) {
                textureId = id.get();
            }
        }

        // Make it appear full bright in GUI
        if (isGui) glow = true;
        int width = data == null ? 1 : data.width();
        int height = data == null ? 1 : data.height();

        matrices.push();

        prepareForItem(matrices, isGui, displayContext, width, height);

        boolean drawBack = isGround || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        renderVertices(vertexConsumers, matrices, drawBack, textureId, width, height, glow, light);

        matrices.pop();
    }

    public static Optional<Identifier> tryGetCanvasId(int canvasId) {
        Optional<CanvasTexture> texture = CanvasTextureManager.request(canvasId);

        if (texture.isPresent() && texture.get().isReady()) {
            return Optional.of(texture.get().textureId);
        }
        return Optional.empty();
    }

    public static void renderVertices(VertexConsumerProvider vertexConsumers, MatrixStack matrices, boolean drawBack, Identifier textureId, int width, int height, boolean glow, int light) {
        Matrix4f m = matrices.peek().getPositionMatrix();
        MatrixStack.Entry pose = matrices.peek();

        Vector3f normal = Direction.NORTH.getUnitVector();

        VertexConsumer consumer;
        if (glow) {
            if (IrisCompat.isShaderEnabled()) {
                consumer = vertexConsumers.getBuffer(RenderLayer.getEyes(textureId));
            } else {
                consumer = vertexConsumers.getBuffer(RenderLayer.getEntityAlpha(textureId));
            }
        } else {
            consumer = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(textureId));
        }

        addVertex(consumer, m, pose, 0,        16*height, -1.5f, 0f, 1f, light, normal);
        addVertex(consumer, m, pose, 16*width, 16*height, -1.5f, 1f, 1f, light, normal);
        addVertex(consumer, m, pose, 16*width, 0,         -1.5f, 1f, 0f, light, normal);
        addVertex(consumer, m, pose, 0,        0,         -1.5f, 0f, 0f, light, normal);

        if (drawBack) {
            VertexConsumer back = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(BACK));
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    addVertex(back, m, pose, x*16,     y*16,       -1.4f, 0f, 0f, light, normal);
                    addVertex(back, m, pose, x*16 + 16, y*16,      -1.4f, 1f, 0f, light, normal);
                    addVertex(back, m, pose, x*16 + 16, y*16 + 16, -1.4f, 1f, 1f, light, normal);
                    addVertex(back, m, pose, x*16,      y*16 + 16, -1.4f, 0f, 1f, light, normal);
                }
            }
        }
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f m, MatrixStack.Entry pose, float x, float y, float z, float tx, float ty, int light, Vector3f normal) {
        normal = normal.mul(pose.getNormalMatrix());
        consumer.vertex(m, x, y, z)
                .color(0xFFFFFFFF)
                .texture(tx, ty)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normal.x, normal.y, normal.z);
    }

    public static void prepareForShield(MatrixStack matrices) {
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.scale(1/32f, 1/32f, 1/32f);
        matrices.translate(-10, -20, -2.5f);
        matrices.scale(1.25f, 1.25f, 1.25f);

    }

    public static void prepareForItem(MatrixStack matrices, boolean isGui, ItemDisplayContext displayContext, int width, int height) {
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.scale(1/32f,1/32f,1/32f);
        matrices.translate(0, 0, -16);

        if (displayContext == ItemDisplayContext.HEAD) {
            matrices.translate(-16, (height+1)*16, -26);
            matrices.scale(2, 2, 2);
        }

        if (isGui || displayContext == ItemDisplayContext.FIXED) {
            int longestSide = Math.max(width, height);
            matrices.translate(0, -32, 0);
            matrices.scale(2, 2, 1);
            matrices.scale(1f/longestSide, 1f/longestSide, 1);
            if (width > height) {
                matrices.translate(0, (width-height)*8, 0);
            } else if (width < height) {
                matrices.translate((height-width)*8, 0, 0);
            }

        } else  {
            matrices.translate(0, 12, 0);
            matrices.translate(16 - width*8, -16 - height*16, 0);
        }

        if (IrisCompat.isShaderEnabled()) {
            matrices.translate(0, 0, -.5f);
        }
    }
}
