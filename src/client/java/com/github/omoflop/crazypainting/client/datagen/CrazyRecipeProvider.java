package com.github.omoflop.crazypainting.client.datagen;

import com.github.omoflop.crazypainting.content.CrazyItems;
import com.github.omoflop.crazypainting.items.CanvasItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class CrazyRecipeProvider extends FabricRecipeProvider {
    public CrazyRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter exporter) {
        return new RecipeGenerator(wrapperLookup, exporter) {
            @Override
            public void generate() {
                createShapeless(RecipeCategory.TOOLS, CrazyItems.PALETTE_ITEM)
                        .input(ItemTags.WOODEN_SLABS)
                        .criterion(hasItem(Items.OAK_SLAB), conditionsFromTag(ItemTags.WOODEN_SLABS))
                        .offerTo(exporter);

                createShaped(RecipeCategory.DECORATIONS, CrazyItems.SMALL_CANVAS_ITEM)
                        .pattern("///")
                        .pattern("/p/")
                        .pattern("///")
                        .input('p', Items.PAPER)
                        .input('/', Items.STICK)
                        .criterion(hasItem(CrazyItems.PALETTE_ITEM), conditionsFromItem(CrazyItems.PALETTE_ITEM))
                        .offerTo(exporter);

                offerCanvas(CrazyItems.TALL_CANVAS_ITEM);
            }

            private void offerCanvas(CanvasItem canvas) {

            }
        };
    }

    @Override
    public String getName() {
        return "CrazyPaintingRecipeProvider";
    }
}
