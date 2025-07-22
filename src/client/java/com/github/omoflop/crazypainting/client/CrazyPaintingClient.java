package com.github.omoflop.crazypainting.client;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.client.itemproperties.PaletteHasColorsProperty;
import com.github.omoflop.crazypainting.client.models.canvas.CanvasEntityRenderer;
import com.github.omoflop.crazypainting.client.models.canvas.CanvasSpecialRenderer;
import com.github.omoflop.crazypainting.client.models.easel.EaselEntityModel;
import com.github.omoflop.crazypainting.client.models.easel.EaselEntityRenderer;
import com.github.omoflop.crazypainting.client.network.ClientPaintingChangeHandler;
import com.github.omoflop.crazypainting.client.network.PaintingCanUpdateHandler;
import com.github.omoflop.crazypainting.client.network.PaintingUpdateHandler;
import com.github.omoflop.crazypainting.client.network.UpdateEaselCanvasIdHandler;
import com.github.omoflop.crazypainting.client.resources.BrushReloadListener;
import com.github.omoflop.crazypainting.content.CrazyEntities;
import com.github.omoflop.crazypainting.network.event.PaintingChangeEvent;
import com.github.omoflop.crazypainting.network.s2c.PaintingCanUpdateS2C;
import com.github.omoflop.crazypainting.network.s2c.PaintingUpdateS2C;
import com.github.omoflop.crazypainting.network.s2c.UpdateEaselCanvasIdS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.resource.ResourceType;

public class CrazyPaintingClient implements ClientModInitializer {

    public static final EntityModelLayer CANVAS_MODEL_LAYER = new EntityModelLayer(CrazyPainting.id("canvas"), "main");
    public static final EntityModelLayer EASEL_MODEL_LAYER = new EntityModelLayer(CrazyPainting.id("easel"), "main");


    @Override
    public void onInitializeClient() {
        // Register boolean property
        BooleanProperties.ID_MAPPER.put(PaletteHasColorsProperty.ID, PaletteHasColorsProperty.CODEC);

        // Register special model type for canvas rendering
        SpecialModelTypes.ID_MAPPER.put(CanvasSpecialRenderer.SPECIAL_ID, CanvasSpecialRenderer.Unbaked.CODEC);

        // Register easel model
        EntityModelLayerRegistry.registerModelLayer(EASEL_MODEL_LAYER, EaselEntityModel::getTexturedModelData);

        // Register easel and canvas entity renderers
        EntityRendererRegistry.register(CrazyEntities.EASEL_ENTITY_TYPE, (EaselEntityRenderer::new));
        EntityRendererRegistry.register(CrazyEntities.CANVAS_ENTITY_TYPE, (CanvasEntityRenderer::new));

        // Packet receivers
        ClientPlayNetworking.registerGlobalReceiver(PaintingChangeEvent.ID, new ClientPaintingChangeHandler());
        ClientPlayNetworking.registerGlobalReceiver(PaintingUpdateS2C.ID, new PaintingUpdateHandler());
        ClientPlayNetworking.registerGlobalReceiver(PaintingCanUpdateS2C.ID, new PaintingCanUpdateHandler());
        ClientPlayNetworking.registerGlobalReceiver(UpdateEaselCanvasIdS2C.ID, new UpdateEaselCanvasIdHandler());

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new BrushReloadListener());
    }




}
