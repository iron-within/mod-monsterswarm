package com.ironwithin.monsterswarm.world;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public class MonsterSwarmWorldDigger extends MonsterSwarmWorldDiggerBase {
    public MonsterSwarmWorldDigger(MonsterSwarmWorld sw) {
        super(sw);
    }

    public void process(PathfinderMob mob, LivingEntity target) {
        int sx = Mth.floor(mob.getX());
        int sy = Mth.floor(mob.getY());
        int sz = Mth.floor(mob.getZ());
        int tx = Mth.floor(target.getX());
        int ty = Mth.floor(target.getY());
        int tz = Mth.floor(target.getZ());

        int dx = tx - sx;
        int dy = ty - sy;
        int dz = tz - sz;
        int xd = 0;
        int zd = 0;
        boolean higher = false;

        if (Math.abs(dx) > Math.abs(dz)) {
            xd = (dx > 0) ? 1 : -1;
            higher = (dy > Math.abs(dx));
        } else {
            zd = (dz > 0) ? 1 : -1;
            higher = (dy > Math.abs(dz));
        }

        if (higher) {
            if (isFreePass(sx, sy - 1, sz)) {
                bridge(sx, sy - 1, sz);
                return;
            }
            if (tryAttack(sx, sy + 1, sz) || tryAttack(sx, sy + 2, sz)) {
                return;
            }
            mob.teleportTo(sx + 0.5D, sy + 1, sz + 0.5D);
            bridge(sx, sy, sz);
            return;
        }

        if (dy > 0) {
            if (isFreePass(sx, sy - 1, sz)) {
                bridge(sx, sy - 1, sz);
                return;
            }
            if (tryAttack(sx + xd, sy + 1, sz + zd) || tryAttack(sx, sy + 2, sz) ||
                    tryAttack(sx + xd, sy + 2, sz + zd) || tryAttack(sx + xd, sy, sz + zd)) {
                return;
            }
            if (isFreePass(sx + xd, sy, sz + zd) && isFreePass(sx + xd, sy - 1, sz + zd) && isFreePass(sx + xd, sy - 2, sz + zd)) {
                bridge(sx + xd, sy, sz + zd);
            }
            return;
        }

        if (dy < 0) {
            if (tryAttack(sx + xd, sy, sz + zd) || tryAttack(sx, sy - 1, sz) || tryAttack(sx + xd, sy - 1, sz + zd)) {
                return;
            }
            if (isFreePass(sx + xd, sy - 1, sz + zd) && isFreePass(sx + xd, sy - 2, sz + zd)) {
                bridge(sx + xd, sy, sz + zd);
            }
            return;
        }

        if (tryAttack(sx + xd, sy + 1, sz + zd) || tryAttack(sx + xd, sy, sz + zd)) {
            return;
        }
        if (isFreePass(sx + xd, sy - 1, sz + zd) && isFreePass(sx + xd, sy - 2, sz + zd)) {
            bridge(sx + xd, sy - 1, sz + zd);
        }
    }
}
