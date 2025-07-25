package com.github.omoflop.crazypainting.recipe;

import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.github.omoflop.crazypainting.content.CrazyRecipes;
import com.github.omoflop.crazypainting.items.CanvasItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class CanvasCopyingRecipe extends SpecialCraftingRecipe {
    public CanvasCopyingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return gatherMatchingCanvases(input.getStacks()).isPresent();
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        Optional<Match> result = gatherMatchingCanvases(input.getStacks());
        if (result.isEmpty()) return ItemStack.EMPTY; // This shouldn't happen
        Match match = result.get();

        ItemStack resultStack = new ItemStack(match.signedCanvas.getItem());

        CanvasDataComponent data = match.signedCanvas.get(CrazyComponents.CANVAS_DATA);
        assert data != null;

        resultStack.set(CrazyComponents.CANVAS_DATA, data.withGeneration((byte) (data.generation() + 1)));
        return resultStack;
    }

    @Override
    public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (CanvasItem.isSigned(stack)) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                list.set(i, copy);
                break;
            }
        }

        return list;
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return CrazyRecipes.CANVAS_COPYING;
    }

    private static Optional<Match> gatherMatchingCanvases(List<ItemStack> stacks) {
        if (stacks.size() != 2) return Optional.empty();

        ItemStack stackA = stacks.getFirst();
        ItemStack stackB = stacks.getLast();

        if (stackA.getItem() != stackB.getItem()) return Optional.empty();

        if (CanvasItem.isSigned(stackA) && CanvasItem.getCanvasId(stackB) == -1 && CanvasItem.getGeneration(stackA) < 2)
            return Optional.of(new Match(stackA, stackB));

        if (CanvasItem.isSigned(stackB) && CanvasItem.getCanvasId(stackA) == -1 && CanvasItem.getGeneration(stackB) < 2)
            return Optional.of(new Match(stackB, stackA));

        return Optional.empty();
    }

    private record Match(ItemStack signedCanvas, ItemStack emptyCanvas) {}
}
