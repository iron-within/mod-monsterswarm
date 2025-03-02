package com.ironwithin.monsterswarm.data;

import java.util.Locale;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Vec3D {
    public double x;
    public double y;
    public double z;

    public Vec3D() {}

    public Vec3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3D(Entity ent) {
        this.x = ent.getX();
        this.y = ent.getY();
        this.z = ent.getZ();
    }

    public Vec3D set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec3D sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vec3D sub(Entity ent) {
        this.x -= ent.getX();
        this.y -= ent.getY();
        this.z -= ent.getZ();
        return this;
    }

    public Vec3D set(Vec3D that) {
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
        return this;
    }

    public Vec3 getMC() {
        return new Vec3(this.x, this.y, this.z);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vec3D normalize() {
        double len = length();
        return len == 0 ? this : set(this.x / len, this.y / len, this.z / len);
    }

    public Vec3D scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    @Override
    public String toString() {
        return String.format(Locale.UK, "[%.1f, %.1f, %.1f]", this.x, this.y, this.z);
    }

    public static void main(String[] args) {
        System.out.println(new Vec3D());
    }
}