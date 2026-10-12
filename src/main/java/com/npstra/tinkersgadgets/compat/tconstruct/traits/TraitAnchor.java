package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitAnchor extends AbstractTrait {

    private static final float DAMAGE_MULTIPLIER = 0.2f;

    public TraitAnchor() {
        super("anchor_chainblade", 0xCCCCCC);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        if (player.moveForward != 0.0F || player.moveStrafing != 0.0F) return newDamage;
        return newDamage + damage * DAMAGE_MULTIPLIER;
    }
}