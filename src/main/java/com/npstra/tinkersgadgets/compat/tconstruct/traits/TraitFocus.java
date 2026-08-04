package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitFocus extends AbstractTrait {
    private static final float BONUS_PER_HEAT = 0.03f;

    public TraitFocus() {
        super("focus_heatraygun", TextFormatting.WHITE);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        NBTTagCompound toolTag = TagUtil.getToolTag(tool);
        NBTTagCompound itemTag = tool.getTagCompound();
        if (toolTag == null || itemTag == null) return newDamage;
        int heatCapacity = toolTag.getInteger("heatCapacity");
        int shotCount = itemTag.getInteger("ShotCount");
        if (heatCapacity <= 0) return newDamage;
        float bonus = Math.min(shotCount, heatCapacity) * BONUS_PER_HEAT;
        return newDamage + damage * bonus;
    }
}