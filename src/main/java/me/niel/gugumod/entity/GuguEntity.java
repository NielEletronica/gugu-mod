package me.niel.gugumod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;
import java.util.List;

public class GuguEntity extends PathfinderMob {


    public static final Item[] MONEY_ITEMS = new Item[] {
            Items.EMERALD,
            Items.GOLD_INGOT,
            Items.DIAMOND
    };

    public boolean isMoney(ItemStack stack) {
        for (Item item : MONEY_ITEMS) {
            if (stack.is(item)) {
                return true;
            }
        }
        return false;
    }


    public GuguEntity(EntityType<? extends GuguEntity> entityType, Level world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createGuguAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.TEMPT_RANGE, 15.0D);
    }

    @Override
    public boolean wantsToPickUp(@NonNull ServerLevel level, @NonNull ItemStack stack) {

        return isMoney(stack);
    }

    @Override
    public boolean canHoldItem(@NonNull ItemStack stack) {
        return isMoney(stack);
    }

    @Override
    protected void pickUpItem(@NonNull ServerLevel level, ItemEntity itemEntity) {
        ItemStack stackOnGround = itemEntity.getItem();

        if (this.wantsToPickUp(level, stackOnGround)) {
            this.onItemPickup(itemEntity);

            ItemStack currentHeldItem = this.getItemBySlot(EquipmentSlot.MAINHAND);

            if (currentHeldItem.is(stackOnGround.getItem())) {
                currentHeldItem.grow(stackOnGround.getCount());
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, stackOnGround.copy());
            }

            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            itemEntity.discard();
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // IA para buscar esmeraldas no chão (Prioridade Alta)
        this.goalSelector.addGoal(1, new PickupMoneyGoal(this, 1.2D));

        this.goalSelector.addGoal(2, new TemptGoal(this, 1.8D, Ingredient.of(MONEY_ITEMS), false));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 12.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0F));
    }

    // --- GOAL PARA NAVEGAR ATÉ A ESMERALDA ---
    private static class PickupMoneyGoal extends Goal {
        private final GuguEntity mob;
        private final double speed;
        private ItemEntity targetItem;

        public PickupMoneyGoal(GuguEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            List<ItemEntity> list = this.mob.level().getEntitiesOfClass(
                    ItemEntity.class,
                    this.mob.getBoundingBox().inflate(10.0D, 3.0D, 10.0D),
                    item -> item.isAlive() && this.mob.isMoney(item.getItem())
            );

            if (!list.isEmpty()) {
                this.targetItem = list.getFirst();
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            if (this.targetItem != null) {
                this.mob.getNavigation().moveTo(this.targetItem, this.speed);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetItem != null && this.targetItem.isAlive() && !this.mob.getNavigation().isDone();
        }

        @Override
        public void tick() {
            if (this.targetItem != null) {
                this.mob.getLookControl().setLookAt(this.targetItem, 30.0F, 30.0F);
                this.mob.getNavigation().moveTo(this.targetItem, this.speed);

                if (this.mob.distanceToSqr(this.targetItem) < 2.2D) {
                    if (this.mob.level() instanceof ServerLevel serverLevel) {
                        this.mob.pickUpItem(serverLevel, this.targetItem);
                    }
                }
            }
        }
    }
}