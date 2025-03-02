package com.ironwithin.monsterswarm.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;

public class Maths {

    public static double fastSqrt(double a) {
        return Double.longBitsToDouble((Double.doubleToLongBits(a) - 4503599627370496L >> 1L) + 2305843009213693952L);
    }

    public static double fastSqrtNewton(double a) {
        double sqrt = fastSqrt(a);
        sqrt = (sqrt + a / sqrt) / 2.0D;
        return (sqrt + a / sqrt) / 2.0D;
    }

    public static Vec3i findPointTowards(PathfinderMob attacker, LivingEntity target, int dist) {
        double dx = target.getX() - attacker.getX();
        double dy = target.getY() - attacker.getY();
        double dz = target.getZ() - attacker.getZ();
        double len = fastSqrtNewton(dx * dx + dy * dy + dz * dz);

        if (len < dist)
            return new Vec3i(target.getX(), target.getY(), target.getZ());

        dx = dx / len * dist;
        dy = dy / len * dist;
        dz = dz / len * dist;

        return new Vec3i(attacker.getX() + dx, attacker.getY() + dy, attacker.getZ() + dz);
    }

    public static boolean contains(AABB aabb, Entity ent) {
        return (ent.getX() >= aabb.minX && ent.getX() <= aabb.maxX &&
                ent.getY() >= aabb.minY && ent.getY() <= aabb.maxY &&
                ent.getZ() >= aabb.minZ && ent.getZ() <= aabb.maxZ);
    }
}
