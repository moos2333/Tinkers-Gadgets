package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TraitBouncing extends ProjectileModifierTrait implements IBoomerangTrait {

    private static final Map<UUID, Float> damageMultMap = new HashMap<>();
    private static final int BOUNCE_COUNT = 3;

    public TraitBouncing() {
        super("bouncing_boomerang", 0x32CD32);
        addAspects(ModifierAspect.projectileOnly);
    }

    public static void setDamageMult(UUID targetId, float mult) {
        damageMultMap.put(targetId, mult);
    }

    @Override
    public int getBoomerangPriority() {
        return 40;
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        if (projectile instanceof EntityBoomerang) {
            ((EntityBoomerang) projectile).setBounceCount(BOUNCE_COUNT);
        }
    }

    @Override
    public boolean onBoomerangHitEntity(EntityBoomerang boomerang, Entity target) {
        if (boomerang.world.isRemote) return false;
        if (boomerang.getBounceCount() <= 0) return false;
        int count = boomerang.getInitialBounceCount() - boomerang.getBounceCount();
        float mult = Math.max(0.0F, 1.0F - 0.25F * count);
        setDamageMult(target.getUniqueID(), mult);
        return false;
    }

    @Override
    public boolean onBoomerangAfterHitEntity(EntityBoomerang boomerang, Entity target, Vec3d savedMotion) {
        if (boomerang.world.isRemote) return false;
        if (boomerang.isReturning()) return false;
        if (boomerang.getBounceCount() <= 0) return false;
        EntityLivingBase nextTarget = boomerang.findNextBounceTarget(target);
        if (nextTarget == null) return false;
        boomerang.decrementBounceCount();
        boomerang.resetBounceDistance();
        boomerang.redirectToTarget(nextTarget);
        boomerang.resetStuckTicks();
        return true;
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        Float mult = damageMultMap.remove(target.getUniqueID());
        if (mult != null) {
            return newDamage * mult;
        }
        return newDamage;
    }
}