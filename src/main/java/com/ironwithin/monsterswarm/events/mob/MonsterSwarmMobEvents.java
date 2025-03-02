package com.ironwithin.monsterswarm.events.mob;

import com.ironwithin.monsterswarm.MonsterSwarmMod;
import com.ironwithin.monsterswarm.world.MonsterSwarmWorld;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MonsterSwarmMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MonsterSwarmMobEvents {

    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof PathfinderMob mob)) return;

        if (mob.getType().equals(EntityType.ZOMBIE) || mob.getType().equals(EntityType.HUSK)) {
            Level world = event.getLevel();

            if (world instanceof ServerLevel serverWorld) {
                MonsterSwarmWorld swarmWorld = MonsterSwarmMod.getSwarmWorld(serverWorld);
                LivingEntity target = serverWorld.getNearestPlayer(mob, 20);
                if (target != null) {
                    swarmWorld.addSwarmMob(mob, target);
                }
            }
        }
    }
}

