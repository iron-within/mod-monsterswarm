package com.ironwithin.monsterswarm.data;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Vec3I {
    public int x;
    public int y;
    public int z;

    public Vec3I() {}

    public Vec3I(Vec3I that) {
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
    }

    public Vec3I(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public Vec3I(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3I(Entity ent) {
        this.x = Mth.floor(ent.getX());
        this.y = Mth.floor(ent.getY() + 0.2D);
        this.z = Mth.floor(ent.getZ());
        if (ent.getX() < 0.0D) this.x--;
        if (ent.getZ() < 0.0D) this.z--;
    }

    public BlockPos getPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public Block getBlock(Level world) {
        return world.getBlockState(getPos()).getBlock();
    }

    public BlockState getData(Level world) {
        return world.getBlockState(getPos());
    }

    public int getLightSky(Level world) {
        return world.getBrightness(LightLayer.SKY, getPos());
    }

    public int getLightBlocks(Level world) {
        return world.getBrightness(LightLayer.BLOCK, getPos());
    }

    public Vec3I set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec3I set(Vec3I that) {
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
        return this;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vec3I scale(double scalar) {
        this.x = (int) (this.x * scalar);
        this.y = (int) (this.y * scalar);
        this.z = (int) (this.z * scalar);
        return this;
    }

    public Vec3I getRelative(int x, int y, int z) {
        return new Vec3I(this.x + x, this.y + y, this.z + z);
    }

    public void setBlock(ServerLevel world, Block type) {
        world.setBlock(getPos(), type.defaultBlockState(), 3);
    }

    @Override
    public int hashCode() {
        return this.x ^ (this.y << 14) ^ (this.z << 20);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3I) {
            Vec3I that = (Vec3I) obj;
            return this.x == that.x && this.y == that.y && this.z == that.z;
        }
        return false;
    }

    public boolean equals(Vec3I that) {
        return this.x == that.x && this.y == that.y && this.z == that.z;
    }

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "," + this.z + "]";
    }
}
