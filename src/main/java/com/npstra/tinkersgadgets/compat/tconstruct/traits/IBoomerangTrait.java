package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerangShard;

public interface IBoomerangTrait {
    default int getBoomerangPriority() { return 100; }
    default void onBoomerangUpdate(EntityBoomerang boomerang, World world) {}
    default boolean onBoomerangHitEntity(EntityBoomerang boomerang, Entity target) { return false; }
    default boolean onBoomerangAfterHitEntity(EntityBoomerang boomerang, Entity target, Vec3d savedMotion) { return false; }
    default boolean onBoomerangHitBlock(EntityBoomerang boomerang, RayTraceResult hit) { return false; }
    default void onBoomerangShardCreated(EntityBoomerang boomerang, EntityBoomerangShard shard) {}
}