package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;

public class TraitTrauma extends ProjectileModifierTrait {
    private static final String TAG_TRAUMA_LEVEL = "trauma_level";
    private static final String TAG_TRAUMA_TIME = "trauma_time";
    private static final int MAX_LEVEL = 10;
    private static final float BONUS_PER_LEVEL = 0.05F;
    private static final int DURATION_TICKS = 100;

    public TraitTrauma() {
        super("trauma_throwingknife", 0xE5DFD6);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        if (target == null || target.world.isRemote) return newDamage;
        NBTTagCompound data = target.getEntityData();
        long currentTime = target.world.getTotalWorldTime();
        int level = data.getInteger(TAG_TRAUMA_LEVEL);
        long expireTime = data.getLong(TAG_TRAUMA_TIME);
        if (level > 0 && currentTime <= expireTime) {
            newDamage += damage * level * BONUS_PER_LEVEL;
        }
        return newDamage;
    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {
        if (world.isRemote) return;
        if (!(target instanceof EntityLivingBase)) return;
        EntityLivingBase livingTarget = (EntityLivingBase) target;
        NBTTagCompound data = livingTarget.getEntityData();
        long currentTime = world.getTotalWorldTime();
        int level = data.getInteger(TAG_TRAUMA_LEVEL);
        long expireTime = data.getLong(TAG_TRAUMA_TIME);
        if (currentTime > expireTime || level == 0) {
            level = 1;
        } else {
            level = Math.min(level + 1, MAX_LEVEL);
        }
        data.setInteger(TAG_TRAUMA_LEVEL, level);
        data.setLong(TAG_TRAUMA_TIME, currentTime + DURATION_TICKS);
    }
}