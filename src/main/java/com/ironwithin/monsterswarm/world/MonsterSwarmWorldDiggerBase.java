package com.ironwithin.monsterswarm.world;

import java.util.*;
import java.util.stream.Collectors;

import com.ironwithin.monsterswarm.MonsterSwarmConfig;
import com.ironwithin.monsterswarm.MonsterSwarmMod;
import com.ironwithin.monsterswarm.data.ObjPool;
import com.ironwithin.monsterswarm.data.ValueMap;
import com.ironwithin.monsterswarm.data.Vec3I;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

public class MonsterSwarmWorldDiggerBase {
    static final Block web = Blocks.COBWEB;
    protected final MonsterSwarmWorld sw;
    protected Block bridge;
    protected final ObjPool<Vec3I> vecpool;
    private final ValueMap<Vec3I> damaged;
    private static final float minimum = 0.26F;
    Map<PathfinderMob, Mob> mobs;

    public MonsterSwarmWorldDiggerBase(MonsterSwarmWorld sw) {
        TagKey<Block> planksTag = BlockTags.PLANKS;  // Get the TagKey for planks
        @NotNull List<Holder<Block>> planks = Registry.BLOCK.getTag(planksTag)  // Get the blocks from the tag registry
                .map(tag -> tag.stream().collect(Collectors.toList()))  // Stream the blocks and collect them in a list
                .orElseThrow(() -> new RuntimeException("Planks tag is missing or empty"));

        if (planks.isEmpty()) {
            this.bridge = Blocks.OAK_PLANKS;  // Fallback if no planks are found
        } else {
            Random rand = new Random();
            this.bridge = planks.get(rand.nextInt(planks.size())).value();  // Randomly pick a plank
        }
        this.vecpool = new ObjPool<>(Vec3I.class);
        this.damaged = new ValueMap<>(2048);
        this.mobs = new HashMap<>();
        this.sw = sw;
    }

    public boolean damage(int x, int y, int z, int damage) {
        MonsterSwarmMod.LOGGER.info(String.format("Tried to damage at {}-{}-{} for {}", x, y, z, damage));
        MonsterSwarmMod.LOGGER.debug("Tried to damage a block!");

        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = this.sw.world.getBlockState(pos);
        Block type = state.getBlock();
        if (type == Blocks.AIR || type == null)
            return true;
        int maxdamage = getMatDmg(type, x, y, z);
        if (maxdamage < 0)
            return false;
        if (!SwarmControl.getInstance().canSwarmBreakBlock(this.sw.world, x, y, z))
            return false;
        if (maxdamage == 0) {
            if (state.getMaterial() == Material.GLASS)
                this.sw.world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.sw.world.destroyBlock(pos, true);
            return true;
        }
        if (this.damaged.size() >= 2048)
            return false;
        Vec3I loc = new Vec3I(x, y, z);
        damage = this.damaged.increment(loc, damage) + damage;
        if (damage >= maxdamage) {
            this.damaged.remove(loc, 0);
            this.sw.world.destroyBlock(pos, true);
            return true;
        }
        return false;
    }

    protected int getMatDmg(Block type, int x, int y, int z) {
        float res = (float) (type.getExplosionResistance() * MonsterSwarmConfig.RESISTANCE_MULTIPLIER.get());
        MonsterSwarmMod.LOGGER.info("Block {} has resistance: {}", type.getName(), res);
        if (res < 0.26F)
            return 0;
        if (res > MonsterSwarmConfig.MAX_RESISTANCE.get())
            res = MonsterSwarmConfig.MAX_RESISTANCE.get();
        return Math.max(2, (int)(res * 2.5F));
    }

    public void addSwarmMob(PathfinderMob mob, Mob target) {
        if (mob == null || target == null) return;

        this.mobs.put(mob, target);
        MonsterSwarmMod.LOGGER.info("Added {} to swarm targeting {}", mob.getType(), target.getName().getString());
    }


    public boolean bridge(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);

        MonsterSwarmMod.LOGGER.info(String.format("Tried to bridge! at {}-{}-{}", x, y, z));
        if (!this.sw.world.isLoaded(pos))
            return false;
        if (!SwarmControl.getInstance().canSwarmBreakBlock(this.sw.world, x, y, z))
            return false;
        BlockState state = this.sw.world.getBlockState(pos);
        if (!state.isAir())
            this.sw.world.destroyBlock(pos, true);
        this.sw.world.setBlockAndUpdate(pos, this.bridge.defaultBlockState());
        return true;
    }

    public void update() {
        this.damaged.reduce(null);
        Iterator<Map.Entry<PathfinderMob, Mob>> iter = this.mobs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<PathfinderMob, Mob> next = iter.next();
            if (!next.getKey().isAlive())
                iter.remove();
        }
    }

    public final boolean isFreePass(int x, int y, int z) {
        BlockState state = this.sw.world.getBlockState(new BlockPos(x, y, z));
        return !state.getMaterial().isSolid() && state.getBlock() != Blocks.AIR;
    }


    public final boolean tryAttack(int x, int y, int z) {
        BlockState state = this.sw.world.getBlockState(new BlockPos(x, y, z));
        Block type = state.getBlock();
        if (type == null || type == Blocks.AIR)
            return false;
        return !damage(x, y, z, 3);
    }

    public static class SwarmControl {
        private static final SwarmControl INSTANCE = new SwarmControl();

        public static SwarmControl getInstance() {
            return INSTANCE;
        }

        public boolean canSwarmBreakBlock(Level world, int x, int y, int z) {
            return true;
        }

        public boolean canSwarmPlaceBlock(Level world, int x, int y, int z) {
            return true;
        }
    }

}
