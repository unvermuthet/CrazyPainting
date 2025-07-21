package com.github.omoflop.crazypainting.entities;

import com.github.omoflop.crazypainting.CrazyPainting;
import com.github.omoflop.crazypainting.components.CanvasDataComponent;
import com.github.omoflop.crazypainting.content.CrazyComponents;
import com.github.omoflop.crazypainting.content.CrazyItems;
import com.github.omoflop.crazypainting.items.CanvasItem;
import com.github.omoflop.crazypainting.items.PaletteItem;
import com.github.omoflop.crazypainting.network.types.PaintingSize;
import com.github.omoflop.crazypainting.network.s2c.OpenPaintingS2CPacket;
import com.github.omoflop.crazypainting.network.s2c.NewPaintingS2CPacket;
import com.github.omoflop.crazypainting.state.CanvasManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;


public class EaselEntity extends LivingEntity {

    public long lastHitTime;

    public EaselEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer createAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.MAX_HEALTH, 4)
                .build();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (hand == Hand.OFF_HAND) return ActionResult.PASS;

        ItemStack heldStack = this.getStackInHand(Hand.MAIN_HAND);
        ItemStack playerHeldStack = player.getStackInHand(hand);
        boolean hasPaletteInEitherHand =
                player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof PaletteItem ||
                player.getStackInHand(Hand.OFF_HAND).getItem() instanceof PaletteItem;

        boolean itemIsCanvas = heldStack.getItem() instanceof CanvasItem;

        if (itemIsCanvas) {
            boolean holdingGlowItem = playerHeldStack.getItem() == CrazyPainting.GLOW_ITEM;
            boolean holdingUnGlowItem = playerHeldStack.getItem() == CrazyPainting.UNGLOW_ITEM;

            if (holdingGlowItem || holdingUnGlowItem) {
                CanvasDataComponent data = heldStack.get(CrazyComponents.CANVAS_DATA);

                boolean success = false;
                if (data == null) {
                    data = new CanvasDataComponent(-1, holdingGlowItem, null);
                    success = true;
                } else if (data.glow() == holdingUnGlowItem) {
                    data = new CanvasDataComponent(data.id(), holdingGlowItem, data.signedBy());
                    success = true;
                }

                if (success) {
                    heldStack.set(CrazyComponents.CANVAS_DATA, data);
                    player.playSound(holdingGlowItem ? SoundEvents.ITEM_GLOW_INK_SAC_USE : SoundEvents.ITEM_INK_SAC_USE);
                    playerHeldStack.decrementUnlessCreative(1, player);
                    return ActionResult.SUCCESS;
                }
            }


        }
        if (itemIsCanvas && heldStack.getItem() == CrazyPainting.UNGLOW_ITEM) {

        }

        if (heldStack.isEmpty()) {
            if (!playerHeldStack.isEmpty()) {
                this.setStackInHand(Hand.MAIN_HAND, playerHeldStack);
                player.setStackInHand(hand, ItemStack.EMPTY);
            }
        } else if (!itemIsCanvas || player.isSneaking()) {
            if (hand == Hand.MAIN_HAND && playerHeldStack.isEmpty()) {
                player.setStackInHand(hand, heldStack);
                this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        } else if (heldStack.getItem() instanceof CanvasItem canvasItem) {
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.SUCCESS;

            if (CanvasItem.getCanvasId(heldStack) == -1 && hasPaletteInEitherHand) {
                int newId = CanvasManager.getServerState(Objects.requireNonNull(serverPlayer.getServer())).getNextId();
                ServerPlayNetworking.send(serverPlayer, new NewPaintingS2CPacket(this.getId(), newId, PaintingSize.from(canvasItem)));
                CanvasItem.setId(heldStack, newId);

                return ActionResult.SUCCESS;
            }

            // Open the editor GUI when a palette is held unless the painting is signed, otherwise open the viewer gui
            boolean edit = hasPaletteInEitherHand;
            if (CanvasItem.isSigned(heldStack)) edit = false;

            ServerPlayNetworking.send(serverPlayer, new OpenPaintingS2CPacket(this.getId(), edit));
        }


        return super.interact(player, hand);
    }

    public boolean canUseSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND;
    }

    @Override
    protected void pushAway(Entity entity) { }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return getStackInHand(Hand.MAIN_HAND).getFormattedName();
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    protected void turnHead(float bodyRotation) {
        this.lastBodyYaw = this.lastYaw;
        this.bodyYaw = this.getYaw();
    }

    public void setBodyYaw(float bodyYaw) {
        this.lastBodyYaw = this.lastYaw = bodyYaw;
        this.lastHeadYaw = this.headYaw = bodyYaw;
    }

    public void setHeadYaw(float headYaw) {
        this.lastBodyYaw = this.lastYaw = headYaw;
        this.lastHeadYaw = this.headYaw = headYaw;
    }

    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.isRemoved()) {
            return false;
        } else if (!world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && source.getAttacker() instanceof MobEntity) {
            return false;
        } else if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {

            this.remove(RemovalReason.KILLED);
            return false;
        } else if (!this.isInvulnerableTo(world, source)) {
            if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                this.onBreak(world, source);

                this.remove(RemovalReason.KILLED);
                return false;
            } else if (source.isIn(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
                if (this.isOnFire()) {
                    this.updateHealth(world, source, 0.15F);
                } else {
                    this.setOnFireFor(5.0F);
                }

                return false;
            } else if (source.isIn(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
                this.updateHealth(world, source, 4.0F);
                return false;
            } else {
                boolean bl = source.isIn(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
                boolean bl2 = source.isIn(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
                if (!bl && !bl2) {
                    return false;
                } else {
                    Entity attacker = source.getAttacker();
                    if (attacker instanceof PlayerEntity playerEntity) {
                        if (!playerEntity.getAbilities().allowModifyWorld) {
                            return false;
                        }
                    }

                    if (source.isSourceCreativePlayer()) {
                        this.playBreakSound();
                        this.spawnBreakParticles();
                        this.remove(RemovalReason.KILLED);
                        return true;
                    } else {
                        long l = world.getTime();
                        if (l - this.lastHitTime > 5L && !bl2) {
                            world.sendEntityStatus(this, (byte)32);
                            this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
                            this.lastHitTime = l;
                        } else {
                            this.breakAndDropItem(world, source);
                            this.spawnBreakParticles();
                            this.remove(RemovalReason.KILLED);
                        }

                        return true;
                    }
                }
            }
        } else {
            return false;
        }
    }

    public void handleStatus(byte status) {
        if (status == 32) {
            if (this.getWorld().isClient) {
                this.getWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
                this.lastHitTime = this.getWorld().getTime();
            }
        } else {
            super.handleStatus(status);
        }

    }

    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * (double)4.0F;
        if (Double.isNaN(d) || d == (double)0.0F) {
            d = 4.0F;
        }

        d *= 64.0F;
        return distance < d * d;
    }

    private void spawnBreakParticles() {
        if (this.getWorld() instanceof ServerWorld) {
            ((ServerWorld)this.getWorld()).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.getX(), this.getBodyY(0.6666666666666666), this.getZ(), 10, (double)(this.getWidth() / 4.0F), (double)(this.getHeight() / 4.0F), (double)(this.getWidth() / 4.0F), 0.05);
        }

    }

    private void updateHealth(ServerWorld world, DamageSource damageSource, float amount) {
        float f = this.getHealth();
        f -= amount;
        if (f <= 0.5F) {
            this.onBreak(world, damageSource);
            this.kill(world);
        } else {
            this.setHealth(f);
            this.emitGameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getAttacker());
        }

    }

    private void breakAndDropItem(ServerWorld world, DamageSource damageSource) {
        ItemStack itemStack = new ItemStack(CrazyItems.EASEL_ITEM);
        itemStack.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());
        Block.dropStack(this.getWorld(), this.getBlockPos(), itemStack);
        this.onBreak(world, damageSource);
    }

    private void onBreak(ServerWorld world, DamageSource damageSource) {
        this.playBreakSound();
        this.drop(world, damageSource);

        for(EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
            ItemStack itemStack = this.equipment.put(equipmentSlot, ItemStack.EMPTY);
            if (!itemStack.isEmpty()) {
                Block.dropStack(this.getWorld(), this.getBlockPos().up(), itemStack);
            }
        }

    }

    private void playBreakSound() {
        this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
    }
}
