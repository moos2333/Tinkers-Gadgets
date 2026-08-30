package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitPulse extends AbstractTrait {

    public TraitPulse() {
        super("pulse_throwingknife", 0xCC0000);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit && target != null && !target.world.isRemote) {
            target.hurtResistantTime = Math.max(0, target.hurtResistantTime / 2);
        }
    }
}