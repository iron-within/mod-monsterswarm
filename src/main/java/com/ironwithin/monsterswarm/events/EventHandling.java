package com.ironwithin.monsterswarm.events;

import com.ironwithin.monsterswarm.MonsterSwarmConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import com.ironwithin.monsterswarm.MonsterSwarmMod;

public class EventHandling {

    // Register to the event bus to automatically call methods on the right time
    public EventHandling(IEventBus bus) {
        bus.register(this);
    }

    // Event when an entity joins the world
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoin(EntityJoinLevelEvent event) {
        // Check if the world is server-side (world.isClientSide)
        if (event.getEntity().level.isClientSide())
            return;

        // Only handle EntityCreature-like entities
        if (!(event.getEntity() instanceof Mob mob))
            return;

        // Excluded entities
        for (Class<?> cls : MonsterSwarmMod.ExcludedAttackers) {
            if (cls.isInstance(mob))
                return;
        }

        // Flag to check if entity is included in the attackable list
        boolean inst = false;
        for (Class<?> cls : MonsterSwarmMod.IncludedAttackers) {
            if (cls.isInstance(mob)) {
                inst = true;
                break;
            }
        }

        if (inst) {
            // Attack animals if enabled
            if (MonsterSwarmConfig.ATTACK_ANIMALS.get() && !(mob instanceof net.minecraft.world.entity.monster.Creeper)) {
                mob.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, Animal.class, false));
            }
            // Attack golems
            mob.goalSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, IronGolem.class, false));
            mob.goalSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, SnowGolem.class, false));
        }
    }

    @SuppressWarnings("All")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackTarget(LivingSetAttackTargetEvent event) {
        // Prevent mobs from attacking other mobs of the same type
        if (event.getEntity() instanceof Monster && event.getTarget() instanceof Monster) {
            ((Monster) event.getEntity()).setTarget(null);
        }
    }

    // Custom goal for nearest attackable target
    public static class NearestAttackableTargetGoal<T extends Entity> extends Goal {

        private final Mob mob;
        private final Class<T> targetClass;
        private final boolean mustSee;

        public NearestAttackableTargetGoal(Mob mob, Class<T> targetClass, boolean mustSee) {
            this.mob = mob;
            this.targetClass = targetClass;
            this.mustSee = mustSee;
        }

        @Override
        public boolean canUse() {
            // Check if there is a valid target
            return !this.mob.getTarget().isAlive();
        }

        @Override
        public void start() {
            // Set the target here
            this.mob.setTarget(null); // Set the appropriate target logic
        }
    }
}
