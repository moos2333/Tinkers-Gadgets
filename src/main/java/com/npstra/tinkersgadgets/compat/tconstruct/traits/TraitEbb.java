package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import com.npstra.tinkersgadgets.compat.tconstruct.tools.ChainBlade;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitEbb extends AbstractTrait {

    private static final String TAG_CHARGE = "chain_charge";
    private static final float BONUS_PER_MISSING = 0.01f;

    public TraitEbb() {
        super("ebb_chainblade", 0x2B4C8C);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        NBTTagCompound toolTag = TagUtil.getToolTag(tool);
        int max = toolTag.hasKey(ChainBlade.KEY_MAX_CHARGE)
                ? toolTag.getInteger(ChainBlade.KEY_MAX_CHARGE)
                : ChainBlade.MAX_CHARGE;
        int missing = max - toolTag.getInteger(TAG_CHARGE);
        if (missing <= 0) return newDamage;
        return newDamage + damage * missing * BONUS_PER_MISSING;
    }
}