package me.niel.gugumod.entity;

import me.niel.gugumod.entity.ai.GuguAvoidEntityGoal;
import me.niel.gugumod.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.TEMPT_RANGE, 100.0D);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        Level level = this.level();

        if (!level.isClientSide()) {
            int tntCount = 20;
            double radius = 10.0;       // Radius of the ring around Gugu
            double heightOffset = 20.0; // Height above Gugu

            double centerX = this.getX();
            double centerY = this.getY() + heightOffset;
            double centerZ = this.getZ();

            for (int i = 0; i < tntCount; i++) {
                // Calculate point on the circle circumference around Gugu
                double angle = (5.0 * Math.PI / tntCount) * i;
                double spawnX = centerX + (radius * Math.cos(angle));
                double spawnZ = centerZ + (radius * Math.sin(angle));

                PrimedTnt tnt = new PrimedTnt(level, spawnX, centerY, spawnZ, this.getKillCredit());

                // Set fuse (e.g. 80 ticks = 4 seconds)
                tnt.setFuse(50);

                // Straight downward drop, no horizontal inward/outward motion
                tnt.setDeltaMovement(new Vec3(0.0, -0.2, 0.0));

                level.addFreshEntity(tnt);
            }
        }
    }

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source){
        return ModSounds.QUEBREI_MEU_DEDO;
    }

    @Override
    protected SoundEvent getDeathSound(){
        return ModSounds.AI_MEU_CORACAO;
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
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        // IA para buscar esmeraldas no chão (Prioridade Alta)
        this.goalSelector.addGoal(1, new PickupMoneyGoal(this, 1.2D));

        this.goalSelector.addGoal(2, new TemptGoal(this, 1.8D, Ingredient.of(MONEY_ITEMS), false));
        this.goalSelector.addGoal(3, new GuguAvoidEntityGoal<>(this, Monster.class,8.0F,1.0D,1.3));
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