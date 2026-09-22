package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitSniping extends AbstractTrait {
    private static final float BONUS_PER_BLOCK = 0.025f;
    private static final float MAX_BONUS = 0.50f;

    public TraitSniping() {
        super("sniping_throwingknife", TextFormatting.AQUA);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        if (player == null || target == null) return newDamage;
        double distance = player.getDistance(target);
        float bonus = (float) Math.min(distance * BONUS_PER_BLOCK, MAX_BONUS);
        return newDamage + damage * bonus;
    }
}