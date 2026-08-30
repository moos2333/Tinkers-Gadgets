package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitTrauma extends AbstractTrait {
    private static final String TAG_TRAUMA_LEVEL = "trauma_level";
    private static final String TAG_TRAUMA_TIME = "trauma_time";
    private static final int MAX_LEVEL = 10;
    private static final float BONUS_PER_LEVEL = 0.05F;
    private static final int DURATION_TICKS = 100;

    public TraitTrauma() {
        super("trauma_throwingknife", TextFormatting.GRAY);
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
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (!wasHit || target == null || target.world.isRemote) return;
        NBTTagCompound data = target.getEntityData();
        long currentTime = target.world.getTotalWorldTime();
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