package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

import java.util.List;

public class TraitDeflect extends ProjectileModifierTrait implements IBoomerangTrait {

    private static final double DEFLECT_MULTIPLIER_XZ = -1.5D;
    private static final double DEFLECT_MULTIPLIER_Y = -0.5D;
    private static final double SEARCH_RADIUS = 1.5D;

    public TraitDeflect() {
        super("deflect_boomerang", 0x5BC7FF);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onBoomerangUpdate(EntityBoomerang boomerang, World world) {
        if (boomerang.isReturning()) return;
        AxisAlignedBB box = boomerang.getEntityBoundingBox().grow(SEARCH_RADIUS);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(boomerang, box);
        for (Entity entity : list) {
            deflect(entity);
        }
    }

    @Override
    public boolean onBoomerangHitEntity(EntityBoomerang boomerang, Entity target) {
        if (boomerang.isReturning()) return false;
        if (isDeflectable(target)) {
            deflect(target);
            return true;
        }
        return false;
    }

    private boolean isDeflectable(Entity entity) {
        return entity instanceof EntityArrow
                || entity instanceof EntityFireball
                || entity instanceof EntityThrowable;
    }

    private void deflect(Entity entity) {
        if (!isDeflectable(entity)) return;
        entity.motionX *= DEFLECT_MULTIPLIER_XZ;
        entity.motionY *= DEFLECT_MULTIPLIER_Y;
        entity.motionZ *= DEFLECT_MULTIPLIER_XZ;
        entity.velocityChanged = true;
    }
}