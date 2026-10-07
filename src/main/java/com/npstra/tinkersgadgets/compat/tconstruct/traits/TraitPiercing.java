package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerangShard;

public class TraitPiercing extends ProjectileModifierTrait implements IBoomerangTrait {

    private static final int PIERCE_COUNT = 5;

    public TraitPiercing() {
        super("boomerang_piercing", 0x8B0000);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public int getBoomerangPriority() {
        return 60;
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        if (projectile instanceof EntityBoomerang) {
            ((EntityBoomerang) projectile).setPiercing(true);
            ((EntityBoomerang) projectile).setPierceCount(PIERCE_COUNT);
        }
    }

    @Override
    public void onBoomerangShardCreated(EntityBoomerang boomerang, EntityBoomerangShard shard) {
        shard.setPiercing(boomerang.isPiercing());
        shard.setPierceCount(boomerang.getPierceCount());
    }

    @Override
    public boolean onBoomerangAfterHitEntity(EntityBoomerang boomerang, Entity target, Vec3d savedMotion) {
        if (boomerang.world.isRemote) return false;
        if (boomerang.isReturning()) return false;
        if (!boomerang.isPiercing()) return false;
        boomerang.motionX = savedMotion.x;
        boomerang.motionY = savedMotion.y;
        boomerang.motionZ = savedMotion.z;
        boomerang.decrementPierceCount();
        return true;
    }
}