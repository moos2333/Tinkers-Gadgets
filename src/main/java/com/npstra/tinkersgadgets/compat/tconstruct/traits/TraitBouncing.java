package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitBouncing extends ProjectileModifierTrait implements IBoomerangTrait {

    private static final String KEY_DAMAGE_MULT = "bouncing_mult";
    private static final int BOUNCE_COUNT = 3;
    private static final float DECAY_PER_BOUNCE = 0.25F;

    public TraitBouncing() {
        super("bouncing_boomerang", 0x32CD32);
        addAspects(ModifierAspect.projectileOnly);
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
        int count = boomerang.getInitialBounceCount() - boomerang.getBounceCount();
        float mult = Math.max(0.0F, 1.0F - DECAY_PER_BOUNCE * count);
        target.getEntityData().setFloat(KEY_DAMAGE_MULT, mult);
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
        NBTTagCompound data = target.getEntityData();
        if (data.hasKey(KEY_DAMAGE_MULT)) {
            float mult = data.getFloat(KEY_DAMAGE_MULT);
            data.removeTag(KEY_DAMAGE_MULT);
            return newDamage * mult;
        }
        return newDamage;
    }
}