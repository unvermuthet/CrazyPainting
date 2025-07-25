package com.github.omoflop.crazypainting.entities;

import com.github.omoflop.crazypainting.content.CrazyEntities;
import com.github.omoflop.crazypainting.items.CanvasItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CanvasEntity extends AbstractDecorationEntity {
    public static final TrackedData<ItemStack> CANVAS_ITEM = DataTracker.registerData(CanvasEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    public static final TrackedData<Byte> ROTATION = DataTracker.registerData(CanvasEntity.class, TrackedDataHandlerRegistry.BYTE);

    public CanvasEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CANVAS_ITEM, ItemStack.EMPTY);
        builder.add(ROTATION, (byte)0);
    }

    public static CanvasEntity create(World world, ItemStack stack, BlockPos pos, Direction facing) {
        CanvasEntity entity = new CanvasEntity(CrazyEntities.CANVAS_ENTITY_TYPE, world);
        entity.setPosition(pos.toCenterPos());
        entity.setHeldItemStack(stack);
        entity.setFacing(facing);
        return entity;
    }

    private double offs(int l) {
        return l % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (getHeldItemStack().getItem() instanceof CanvasItem canvasItem && canvasItem.width == canvasItem.height) {

            setRotation((byte) (getItemRotation() + 1));
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    protected Box calculateBoundingBox(BlockPos pos, Direction side) {
        float width = 1;
        float height = 1;

        if (dataTracker.get(CANVAS_ITEM).getItem() instanceof CanvasItem canvas) {
            width = canvas.width;
            height = canvas.height;
        }

        float widthTweak = width % 2 == 0 ? 0.5f : 0;
        float heightTweak = height % 2 == 0 ? 0.5f : 0;

        final float distFromWall = 0.46f;
        final float pixel = 1/16f;

        width -= pixel*2.35f;
        height -= pixel*2.35f;

        if (side == Direction.UP) {
            return Box.of(pos.toCenterPos().subtract(widthTweak, distFromWall, heightTweak), width, pixel, height);
        } else if (side == Direction.DOWN) {
            return Box.of(pos.toCenterPos().subtract(widthTweak, -distFromWall, heightTweak), width, pixel, height);
        } else if (side == Direction.NORTH) {
            return Box.of(pos.toCenterPos().subtract(widthTweak, heightTweak, -distFromWall), width, height, pixel);
        }  else if (side == Direction.EAST) {
            return Box.of(pos.toCenterPos().subtract(distFromWall, heightTweak, widthTweak), pixel, height, width);
        } else if (side == Direction.SOUTH) {
            return Box.of(pos.toCenterPos().subtract(widthTweak, heightTweak, distFromWall), width, height, pixel);
        } else if (side == Direction.WEST) {
            return Box.of(pos.toCenterPos().subtract(-distFromWall, heightTweak, widthTweak), pixel, height, width);
        }

        return Box.of(pos.toCenterPos(), 1, 1, 1);
    }

    protected void setFacing(Direction facing) {
        super.setFacingInternal(facing);
        if (facing.getAxis().isHorizontal()) {
            this.setPitch(0.0F);
            this.setYaw((float)(facing.getHorizontalQuarterTurns() * 90));
        } else {
            this.setPitch((float)(-90 * facing.getDirection().offset()));
            this.setYaw(0.0F);
        }

        this.lastPitch = this.getPitch();
        this.lastYaw = this.getYaw();
        this.updateAttachmentPosition();
    }

    @Override
    public void onPlace() {

    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void onBreak(ServerWorld world, @Nullable Entity breaker) {
        dropStack(world, dataTracker.get(CANVAS_ITEM));
    }

    public ItemStack getHeldItemStack() {
        return dataTracker.get(CANVAS_ITEM);
    }

    public void setHeldItemStack(ItemStack value) {
        if (!value.isEmpty()) {
            value = value.copyWithCount(1);
        }

        this.setAsStackHolder(value);
        this.getDataTracker().set(CANVAS_ITEM, value);
    }

    private void setAsStackHolder(ItemStack stack) {
        if (!stack.isEmpty() && stack.getFrame() == null) {
            stack.setHolder(this);
        }

        this.updateAttachmentPosition();
    }

    public byte getItemRotation() {
        return this.getDataTracker().get(ROTATION);
    }

    public void setItemRotation(byte rotation) {
        this.getDataTracker().set(ROTATION, rotation);
    }

    public void setRotation(byte value) {
        this.getDataTracker().set(ROTATION, (byte)(value % 4));
    }

    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        ItemStack itemStack = this.getHeldItemStack();
        if (!itemStack.isEmpty()) view.put("Item", ItemStack.CODEC, itemStack);

        view.putByte("ItemRotation", this.getItemRotation());
        view.put("Facing", Direction.INDEX_CODEC, this.getFacing());
    }

    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        ItemStack itemStack = view.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);

        this.setHeldItemStack(itemStack);
        this.setItemRotation(view.getByte("ItemRotation", (byte)0));
        this.setFacing(view.read("Facing", Direction.INDEX_CODEC).orElse(Direction.DOWN));
    }

}
