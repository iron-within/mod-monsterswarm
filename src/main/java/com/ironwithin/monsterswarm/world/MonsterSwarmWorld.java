package com.ironwithin.monsterswarm.world;

import java.util.ArrayList;
import java.util.List;

import com.ironwithin.monsterswarm.MonsterSwarmConfig;
import com.ironwithin.monsterswarm.MonsterSwarmMod;
import com.ironwithin.monsterswarm.basic.Log;
import com.ironwithin.monsterswarm.basic.Maths;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MonsterSwarmWorld {
    protected ServerLevel world;
    private MonsterSwarmWorldDigger digg;
    public final List<PathfinderMob> attackers = new ArrayList<>();
    public final List<LivingEntity> targets = new ArrayList<>();
    public boolean isOverworld = false;
    public boolean isNether = false;
    public boolean fullmoon = true;
    public boolean isDay = false;
    int index;
    int ticks;

    public boolean isSurface(Entity mob) {
        if (mob == null) return false;
        return this.isOverworld && mob.getY() > 40.0D;
    }

    public MonsterSwarmWorld(ServerLevel world) {
        this.index = 0;
        this.ticks = 0;
        Log.log("Registering world dimension " + world.dimension());
        this.world = world;
        this.digg = new MonsterSwarmWorldDigger(this);
        MinecraftForge.EVENT_BUS.register(this);

        if (world.dimension() == Level.OVERWORLD) this.isOverworld = true;
        if (world.dimension() == Level.NETHER) this.isNether = true;
    }

    public void run() {
        this.ticks++;
        if (this.isOverworld) {
            int time = (int) this.world.getDayTime() % 24000;
            int day = (int) this.world.getDayTime() / 24000;
            this.isDay = (time < 12200 || time > 23850);
            this.fullmoon = (day % 8 == 0 && day > 2);
        } else {
            this.fullmoon = true;
            this.isDay = false;
        }

        if (this.index >= this.attackers.size()) {
            if (this.ticks < 20) return;
            this.ticks = 0;
            this.index = 0;
            collectEntities();
            this.digg.update();
            return;
        }

        int top = Math.min(this.index + 2, this.attackers.size());
        for (; this.index < top; this.index++) {
            PathfinderMob mob = this.attackers.get(this.index);
            if (this.isDay && MonsterSwarmConfig.KILL_MOBS_DAYTIME.get() && !mob.isPersistenceRequired()) {
                int light = mob.level.getBrightness(LightLayer.SKY, mob.blockPosition());
                if (light > 11) {
                    mob.setSecondsOnFire(8);
                    mob.hurt(net.minecraft.world.damagesource.DamageSource.ON_FIRE, 2.0F);
                }
            }

            LivingEntity target = findTargetFor(mob, 64);
            if (target == null) {
                target = mob.getTarget();
                if (target instanceof Monster) {
                    target = null;
                    mob.setTarget(null);
                }
            }

            if (target != null) {
                mob.setTarget(target);
                Vec3i point = Maths.findPointTowards(mob, target, 15);
                boolean canDigg = true;

                if (this.isOverworld) {
                    if (mob.getY() > 40.0D) {
                        canDigg = MonsterSwarmConfig.isSwarm(MonsterSwarmConfig.SWARM_OVERWORLD, this.fullmoon);
                    } else {
                        canDigg = MonsterSwarmConfig.isSwarm(MonsterSwarmConfig.SWARM_UNDERGROUND, this.fullmoon);
                    }
                } else if (this.isNether) {
                    canDigg = MonsterSwarmConfig.SWARM_NETHER.get();
                } else {
                    canDigg = MonsterSwarmConfig.SWARM_DIMENSIONS.get();
                }

                if (canDigg && (mob instanceof net.minecraft.world.entity.monster.Zombie || mob instanceof net.minecraft.world.entity.monster.Skeleton))
                    this.digg.process(mob, target);

                mob.getNavigation().moveTo(point.getX() + 0.5D, point.getY() + 0.5D, point.getZ() + 0.5D, 1.0D);
            }
        }
    }

    public LivingEntity findTargetFor(PathfinderMob attacker, int radius) {
        AABB aabb = new AABB(attacker.getX() - radius, attacker.getY() - radius - radius, attacker.getZ() - radius,
                attacker.getX() + radius, attacker.getY() + radius + radius, attacker.getZ() + radius);
        LivingEntity nearest = null;
        double ndistSq = Double.POSITIVE_INFINITY;

        for (LivingEntity ent : this.targets) {
            if (!ent.isAlive() || !Maths.contains(aabb, ent)) continue;
            double dx = attacker.getX() - ent.getX();
            double dy = attacker.getY() - ent.getY();
            double dz = attacker.getZ() - ent.getZ();
            double distSq = dx * dx + dz * dz;
            double distHeight = Math.abs(dy);
            if (distSq > (radius * radius)) continue;
            if (!(ent instanceof Player)) distSq += distSq;
            if (distSq < ndistSq) {
                if (this.isOverworld && ((ent.getY() < 40.0D) ? (attacker.getY() > 40.0D) : (attacker.getY() < 40.0D)))
                    continue;
                nearest = ent;
                ndistSq = distSq;
            }
        }
        return nearest;
    }

    private void collectEntities() {
        this.attackers.clear();
        this.targets.clear();
        for (Entity ent : this.world.getEntities().getAll()) {
            if (ent instanceof PathfinderMob mob) {
                if (isSurface(mob) && !MonsterSwarmConfig.isSwarm(MonsterSwarmConfig.SWARM_OVERWORLD, this.fullmoon)) {
                    if (MonsterSwarmMod.isSurfaceExcluded(mob)) continue;
                }
                this.attackers.add(mob);
                continue;
            }
            if (ent instanceof Player player) {
                if (player.isCreative() || player.isSpectator()) continue;
                this.targets.add(player);
            }
        }
    }

    public void addSwarmMob(PathfinderMob mob, LivingEntity target) {
        if(target instanceof Mob)
            this.digg.mobs.put(mob, (Mob)target);
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        run();
    }
}
