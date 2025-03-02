package com.ironwithin.monsterswarm.basic;

import java.lang.reflect.Field;
import java.util.List;

import com.ironwithin.monsterswarm.MonsterSwarmMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;

public class Magic {
    @SuppressWarnings("All")
    public static Class findClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            MonsterSwarmMod.LOGGER.debug("Class not found: {}", name);
            return null;
        }
    }

    public static void addClass(List<Class<?>> list, String name) {
        Class<?> cls = findClass(name);
        if (cls != null)
            list.add(cls);
    }

    @SuppressWarnings("All")
    public static void setResist(String blockName, float resist) {
        ResourceLocation blockLoc = new ResourceLocation(MonsterSwarmMod.MODID, blockName);
        Block block = ForgeRegistries.BLOCKS.getValue(blockLoc);

        if (block != null) {
            setResistance(block, resist);
            MonsterSwarmMod.LOGGER.debug("Resistance of '{}' set to {}", blockName, resist);
        } else {
            MonsterSwarmMod.LOGGER.debug("Block '{}' not found.", blockName);
        }
    }

    @SuppressWarnings("All")
    private static void setResistance(Block block, float resistance) {
        try {
            Field field = Block.class.getDeclaredField("properties");
            field.setAccessible(true);
            BlockBehaviour.Properties properties = (BlockBehaviour.Properties) field.get(block);
            Field resistanceField = BlockBehaviour.Properties.class.getDeclaredField("explosionResistance");
            resistanceField.setAccessible(true);
            resistanceField.setFloat(properties, resistance);
        } catch (Exception e) {
            MonsterSwarmMod.LOGGER.warn("Could not modify resistance for block: {}", block, e);
        }
    }
}