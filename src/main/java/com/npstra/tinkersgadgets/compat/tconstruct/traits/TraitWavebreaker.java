package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitWavebreaker extends AbstractTrait {

    private static final float BONUS = 5.0F;

    public TraitWavebreaker() {
        super("wavebreaker_throwingknife", 0x5FCDCD);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        if (target instanceof EntityWaterMob || target instanceof EntityGuardian) {
            return newDamage + BONUS;
        }
        return newDamage;
    }
}