package com.ironwithin.monsterswarm;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MonsterSwarmMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MonsterSwarmConfig {

    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SOUNDS;
    public static final ForgeConfigSpec.ConfigValue<String> SWARM_OVERWORLD;
    public static final ForgeConfigSpec.ConfigValue<String> SWARM_UNDERGROUND;
    public static final ForgeConfigSpec.BooleanValue SWARM_NETHER;
    public static final ForgeConfigSpec.BooleanValue SWARM_DIMENSIONS;
    public static final ForgeConfigSpec.BooleanValue KILL_MOBS_DAYTIME;
    public static final ForgeConfigSpec.BooleanValue ATTACK_ANIMALS;
    public static final ForgeConfigSpec.BooleanValue MODIFY_RESISTANCE;
    public static final ForgeConfigSpec.IntValue AGGRO_RANGE;
    public static final ForgeConfigSpec.IntValue MAX_RESISTANCE;
    public static final ForgeConfigSpec.DoubleValue RESISTANCE_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue DEBUG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Monster Swarm General Settings").push("general");

        ENABLE_SOUNDS = builder
                .comment("Enable sounds for monster swarms")
                .define("enable_sounds", true);

        SWARM_OVERWORLD = builder
                .comment("Swarm Overworld Surface. Options: ALWAYS, NEVER, FULLMOON")
                .define("swarm_overworld", "FULLMOON");

        SWARM_UNDERGROUND = builder
                .comment("Swarm Overworld Underground. Options: ALWAYS, NEVER, FULLMOON")
                .define("swarm_underground", "ALWAYS");

        SWARM_NETHER = builder
                .comment("Allow monster swarms in the Nether")
                .define("swarm_nether", true);

        SWARM_DIMENSIONS = builder
                .comment("Allow monster swarms in other modded dimensions")
                .define("swarm_dimensions", true);

        KILL_MOBS_DAYTIME = builder
                .comment("All mobs burn in daylight if true")
                .define("kill_mobs_daytime", true);

        ATTACK_ANIMALS = builder
                .comment("Mobs will target animals if true")
                .define("attack_animals", true);

        MODIFY_RESISTANCE = builder
                .comment("Modify block resistance")
                .define("modify_resistance", true);

        AGGRO_RANGE = builder
                .comment("Aggro range of swarming mobs")
                .defineInRange("aggro_range", 120, 1, 500);

        MAX_RESISTANCE = builder
                .comment("Maximum resistance value")
                .defineInRange("max_resistance", 90, 1, 100);

        RESISTANCE_MULTIPLIER = builder
                .comment("Resistance multiplier")
                .defineInRange("resistance_multiplier", 0.8, 0.1, 5.0);

        DEBUG = builder
                .comment("Enable debug mode")
                .define("debug", false);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean isSwarm(ForgeConfigSpec.ConfigValue<String> config, boolean fullmoon) {
        String value = config.get().toUpperCase(); // Normalize case
        return switch (value) {
            case "ALWAYS" -> true;
            case "NEVER" -> false;
            case "FULLMOON" -> fullmoon;
            default -> false;
        };
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON_CONFIG) {
            loadConfig();
        }
    }

    private static void loadConfig() {
        // This ensures config values are properly updated in-game
        MonsterSwarmMod.LOGGER.info("Monster Swarm config loaded.");
    }
}
