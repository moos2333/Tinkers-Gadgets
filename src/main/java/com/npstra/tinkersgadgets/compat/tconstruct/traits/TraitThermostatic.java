package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitThermostatic extends AbstractTrait {
    public TraitThermostatic() {
        super("thermostatic_heatraygun", TextFormatting.GOLD);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        NBTTagCompound toolTag = TagUtil.getToolTag(tool);
        NBTTagCompound itemTag = tool.getTagCompound();
        if (toolTag == null || itemTag == null) return newDamage;

        int heatCapacity = toolTag.getInteger("heatCapacity");
        int shotCount = itemTag.getInteger("ShotCount");
        if (heatCapacity <= 1) return newDamage;

        float progress = (float) shotCount / (heatCapacity - 1);
        progress = Math.min(1.0F, Math.max(0.0F, progress));

        float base = 1.0F - 4.0F * (progress - 0.5F) * (progress - 0.5F);
        float bonus = base * 0.3F - 0.05F;
        return newDamage + damage * bonus;
    }
}