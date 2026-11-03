package me.niel.gugumod.entity.ai;

import me.niel.gugumod.sound.ModSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.sounds.SoundSource;

public class GuguAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    public GuguAvoidEntityGoal(PathfinderMob mob, Class<T> entityToAvoid, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier) {
        super(mob, entityToAvoid, maxDistance, walkSpeedModifier, sprintSpeedModifier);
    }

    @Override
    public void start() {
        super.start();
        // Play the sound on the server side when Gugu starts fleeing
        if (!this.mob.level().isClientSide()) {
            this.mob.level().playSound(
                    null,
                    this.mob.getX(),
                    this.mob.getY(),
                    this.mob.getZ(),
                    ModSounds.SAI_DE_PERTO,
                    SoundSource.NEUTRAL,
                    1.0F,
                    1.0F
            );
        }
    }
}
