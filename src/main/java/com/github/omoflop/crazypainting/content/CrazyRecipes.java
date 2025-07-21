package com.github.omoflop.crazypainting.content;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.recipe.PaletteFillingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CrazyRecipes {
    public static final RecipeSerializer<PaletteFillingRecipe> PALETTE_FILLING = new SpecialCraftingRecipe.SpecialRecipeSerializer<>(PaletteFillingRecipe::new);

    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, CrazyPainting.id("palette_filling"), PALETTE_FILLING);
    }
}
