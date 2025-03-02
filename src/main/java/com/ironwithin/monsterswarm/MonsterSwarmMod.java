package com.ironwithin.monsterswarm;

import com.ironwithin.monsterswarm.basic.Magic;
import com.ironwithin.monsterswarm.world.MonsterSwarmWorld;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The value should match the entry in META-INF/mods.toml
@Mod(MonsterSwarmMod.MODID)
public class MonsterSwarmMod {

    public static final String MODID = "monsterswarm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final List<Class<?>> ExcludedSurfaceAttackers = new ArrayList<>();
    public static final List<Class<?>> ExcludedAttackers = new ArrayList<>();
    public static final List<Class<?>> IncludedAttackers = new ArrayList<>();
    public static final List<Class<?>> IncludedTargets = new ArrayList<>();
    public static final List<Class<?>> IncludedDiggers = new ArrayList<>();

    private static final Map<ServerLevel, MonsterSwarmWorld> swarmWorlds = new HashMap<>();

    @SuppressWarnings("All")
    public MonsterSwarmMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // Register the config properly
        MonsterSwarmConfig.register();

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static MonsterSwarmWorld getSwarmWorld(ServerLevel level) {
        return swarmWorlds.computeIfAbsent(level, MonsterSwarmWorld::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Monster Swarm mod initialized on singleplayer.");
        initializeEntities();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Monster Swarm mod initialized on server.");
        swarmWorlds.clear(); // Clear old worlds to avoid memory leaks
    }

    private void initializeEntities() {
        try {
            addEntity(IncludedAttackers, "net.minecraft.world.entity.monster.Monster");
            addEntity(IncludedAttackers, "net.minecraft.world.entity.monster.Zombie");
            addEntity(IncludedAttackers, "net.minecraft.world.entity.monster.Skeleton");
            addEntity(IncludedAttackers, "drzhark.mocreatures.entity.passive.MoCEntityBear");
            addEntity(IncludedAttackers, "drzhark.mocreatures.entity.passive.MoCEntityBoar");
            addEntity(ExcludedSurfaceAttackers, "drzhark.mocreatures.entity.monster.MoCEntityGolem");
            addEntity(ExcludedSurfaceAttackers, "drzhark.mocreatures.entity.monster.MoCEntityMiniGolem");
            addEntity(ExcludedAttackers, "net.minecraft.world.entity.monster.EnderMan");
            addEntity(ExcludedAttackers, "crazypants.enderzoo.entity.EntityOwl");
            addEntity(IncludedDiggers, "net.minecraft.world.entity.monster.Zombie");
            addEntity(IncludedDiggers, "net.minecraft.world.entity.monster.Skeleton");
            addEntity(IncludedTargets, "net.minecraft.world.entity.player.Player");
            addEntity(IncludedTargets, "net.minecraft.world.entity.animal.IronGolem");
            addEntity(IncludedTargets, "net.minecraft.world.entity.npc.Villager");
        } catch (Exception e) {
            LOGGER.error("Error initializing entity lists", e);
        }
    }

    private void addEntity(List<Class<?>> list, String className) {
        Magic.addClass(list, className);
    }

    public static boolean isSurfaceExcluded(Entity entity) {
        for (Class<?> c : ExcludedSurfaceAttackers) {
            if (c.isInstance(entity))
                return true;
        }
        return false;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Client instance of Monster Swarm mod initialized.");
        }
    }
}
