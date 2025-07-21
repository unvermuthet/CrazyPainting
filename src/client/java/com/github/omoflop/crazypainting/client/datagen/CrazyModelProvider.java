package com.github.omoflop.crazypainting.client.datagen;

import com.github.omoflop.crazypainting.client.itemproperties.PaletteHasColorsProperty;
import com.github.omoflop.crazypainting.client.models.canvas.CanvasSpecialRenderer;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.github.omoflop.crazypainting.content.CrazyItems;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.items.PaletteItem;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ConditionItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.property.bool.HasComponentProperty;
import net.minecraft.client.render.item.property.select.DisplayContextProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;

public class CrazyModelProvider extends FabricModelProvider {

    public CrazyModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator gen) {
        registerBasic(gen, CrazyItems.EASEL_ITEM);
        registerPalette(gen, CrazyItems.PALETTE_ITEM);

        CrazyItems.allCanvases.forEach(canvas -> registerCanvas(gen, canvas));
    }

    private void registerBasic(ItemModelGenerator gen, Item item) {
        gen.register(item, Models.GENERATED);
    }

    private void registerCanvas(ItemModelGenerator gen, CanvasItem item) {
        ItemModel.Unbaked emptyModel = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked paintedModel = ItemModels.basic(ModelIds.getItemSubModelId(item, "_painted"));
        ItemModel.Unbaked specialModel = ItemModels.special(ModelIds.getItemSubModelId(item, "_painted"), new CanvasSpecialRenderer.Unbaked());

        ItemModel.Unbaked paintedModelWithCondition = ItemModels.select(new DisplayContextProperty(), specialModel, ItemModels.switchCase(ItemDisplayContext.GROUND, paintedModel));

        // Create the item asset
        ConditionItemModel.Unbaked canvasModel = new ConditionItemModel.Unbaked(new HasComponentProperty(CrazyComponents.CANVAS_DATA, false), paintedModelWithCondition, emptyModel);
        ItemAsset asset = new ItemAsset(canvasModel, new ItemAsset.Properties(true, true));
        gen.output.accept(item, asset.model());
        gen.registerSubModel(item, "", Models.GENERATED);
        gen.registerSubModel(item, "_painted", Models.GENERATED);
    }

    private void registerPalette(ItemModelGenerator gen, PaletteItem item) {
        var modelFilled = ItemModels.basic(ModelIds.getItemSubModelId(item, "_filled"));
        var model = ItemModels.basic(ModelIds.getItemModelId(item));
        gen.registerCondition(item, new PaletteHasColorsProperty((byte)1), modelFilled, model);
        gen.registerSubModel(item, "", Models.GENERATED); // ???? lol
        gen.registerSubModel(item, "_filled", Models.GENERATED);
    }
}