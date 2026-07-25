package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitPressurized extends AbstractTrait {
    private static final float MAX_BONUS = 0.20F;

    public TraitPressurized() {
        super("pressurized_heatraygun", TextFormatting.AQUA);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        NBTTagCompound toolTag = TagUtil.getToolTag(tool);
        NBTTagCompound itemTag = tool.getTagCompound();
        if (toolTag == null || itemTag == null) return newDamage;
        int maxFuel = toolTag.getInteger("maxFuel");
        int fuel = itemTag.getInteger("Fuel");
        if (maxFuel <= 0) return newDamage;
        float progress = Math.min(1.0F, (float) fuel / maxFuel);
        float bonus = progress * MAX_BONUS;
        return newDamage + damage * bonus;
    }
}