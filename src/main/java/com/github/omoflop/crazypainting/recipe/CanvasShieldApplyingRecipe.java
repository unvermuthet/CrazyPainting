package com.github.omoflop.crazypainting.recipe;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import com.github.omoflop.crazypainting.components.PaletteColorsComponent;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.github.omoflop.crazypainting.content.CrazyRecipes;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.items.PaletteItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CanvasShieldApplyingRecipe extends SpecialCraftingRecipe {
    public CanvasShieldApplyingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return findMatch(input).isPresent();
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        Match match = findMatch(input).orElseThrow();
        CanvasDataComponent canvasData = match.canvas.get(CrazyComponents.CANVAS_DATA);

        ItemStack result = match.shield.copy();
        result.set(CrazyComponents.CANVAS_DATA, canvasData);
        LoreComponent lore = result.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT);
        LoreComponent newLore = lore.with(Text.literal(canvasData.title()).formatted(Formatting.YELLOW));
        result.set(DataComponentTypes.LORE, newLore);

        return result;
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return CrazyRecipes.CANVAS_SHIELD_APPLYING;
    }

    // Requires a signed canvas and a shield with no banner applied
    private Optional<Match> findMatch(CraftingRecipeInput input) {
        if (input.getStackCount() != 2) return Optional.empty();

        List<ItemStack> stacks = input.getStacks();
        ItemStack shieldStack = null;
        ItemStack canvasStack = null;

        for (ItemStack stack : stacks) {
            if (stack.isOf(Items.SHIELD)) {
                if (shieldStack != null) return Optional.empty();
                shieldStack = stack;
            }
            if (stack.getItem() instanceof CanvasItem canvas && canvas.width == 1 && canvas.height == 2 && CanvasItem.isSigned(stack)) {
                if (canvasStack != null) return Optional.empty();
                canvasStack = stack;
            }
        }

        if (shieldStack == null || canvasStack == null) return Optional.empty();
        return Optional.of(new Match(shieldStack, canvasStack));
    }

    record Match(ItemStack shield, ItemStack canvas) {}
}
