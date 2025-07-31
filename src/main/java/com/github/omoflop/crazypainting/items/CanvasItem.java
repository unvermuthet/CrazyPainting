package com.github.omoflop.crazypainting.items;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.Identifiable;
import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.github.omoflop.crazypainting.content.CrazyEntities;
import com.github.omoflop.crazypainting.entities.CanvasEntity;
import com.github.omoflop.crazypainting.network.types.PaintingData;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import net.minecraft.block.SideShapeType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public class CanvasItem extends Item implements Identifiable {
    public static final String UNTITLED = "Untitled Painting";

    public final Identifier id;

    public final byte width;
    public final byte height;

    public CanvasItem(String registryName, byte width, byte height) {
        super(new Settings().maxCount(1).registryKey(Identifiable.key(registryName))
                .component(DataComponentTypes.LORE, new LoreComponent(List.of(), List.of(Text.literal(width + "x" + height).formatted(Formatting.WHITE))))
                .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD).swappable(false).equipOnInteract(false).build())
        );
        this.id = CrazyPainting.id(registryName);
        this.width = width;
        this.height = height;

    }

    @Override
    public Text getName(ItemStack stack) {
        String title = getTitle(stack);
        if (title.equals(UNTITLED)) return super.getName(stack);
        return Text.literal(title);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction side = context.getSide();
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        ItemStack usageStack = context.getStack();

        if (CanvasItem.getCanvasId(usageStack) == -1) return ActionResult.PASS;

        boolean mayPlace = world.getBlockState(pos).isSideSolid(world, pos, side, SideShapeType.CENTER);
        if (!mayPlace) return ActionResult.PASS;

        ItemStack stack = usageStack.copyComponentsToNewStack(usageStack.getItem(), 1);

        CanvasEntity entity = CanvasEntity.create(world, stack, pos.add(side.getVector()), side);
        world.spawnEntity(entity);

        context.getStack().decrementUnlessCreative(1, context.getPlayer());
        return ActionResult.SUCCESS;
    }


    public static int getCanvasId(ItemStack stack) {
        if (!stack.hasChangedComponent(CrazyComponents.CANVAS_DATA)) return -1;
        var component = stack.getComponents().get(CrazyComponents.CANVAS_DATA);
        if (component == null) return -1;
        return component.id();
    }


    public static int getGeneration(ItemStack stack) {
        if (!stack.hasChangedComponent(CrazyComponents.CANVAS_DATA)) return -1;
        var component = stack.getComponents().get(CrazyComponents.CANVAS_DATA);
        if (component == null) return -1;
        return component.generation();
    }

    public static boolean getGlow(ItemStack stack) {
        if (!stack.hasChangedComponent(CrazyComponents.CANVAS_DATA)) return false;
        var component = stack.getComponents().get(CrazyComponents.CANVAS_DATA);
        if (component == null) return false;
        return component.glow();
    }

    public static @Nullable String getSignedBy(ItemStack stack) {
        if (!stack.hasChangedComponent(CrazyComponents.CANVAS_DATA)) return null;

        var component = stack.getComponents().get(CrazyComponents.CANVAS_DATA);
        if (component == null) return null;

        String signedBy = component.signedBy();
        if (signedBy.isEmpty()) return null;

        return signedBy;
    }

    public static String getTitle(ItemStack stack) {
        if (!stack.hasChangedComponent(CrazyComponents.CANVAS_DATA)) return UNTITLED;

        var component = stack.getComponents().get(CrazyComponents.CANVAS_DATA);
        if (component == null) return UNTITLED;

        return component.title();
    }

    public PaintingSize getSize() {
        return new PaintingSize(width, height);
    }

    public static boolean isSigned(ItemStack heldStack) {
        return getSignedBy(heldStack) != null;
    }

    @Override
    public Identifier getId() {
        return id;
    }
}
