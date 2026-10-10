package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import com.npstra.tinkersgadgets.compat.tconstruct.tools.PistolSword;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitPressurized extends AbstractTrait {

    private static final float MAX_FUEL_BONUS = 0.20F;
    private static final float BONUS_PER_AMMO = 0.04F;

    private static final String TAG_MAX_FUEL = "maxFuel";
    private static final String TAG_FUEL = "Fuel";

    public TraitPressurized() {
        super("pressurized_heatraygun", TextFormatting.AQUA);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        NBTTagCompound toolTag = TagUtil.getToolTag(tool);
        if (toolTag == null) return newDamage;

        if (toolTag.hasKey(TAG_MAX_FUEL)) {
            return applyFuelBonus(tool, toolTag, damage, newDamage);
        }
        if (toolTag.hasKey(PistolSword.KEY_MAX_AMMO)) {
            return applyAmmoBonus(tool, damage, newDamage);
        }
        return newDamage;
    }

    private float applyFuelBonus(ItemStack tool, NBTTagCompound toolTag, float damage, float newDamage) {
        NBTTagCompound itemTag = tool.getTagCompound();
        if (itemTag == null) return newDamage;
        int maxFuel = toolTag.getInteger(TAG_MAX_FUEL);
        if (maxFuel <= 0) return newDamage;
        int fuel = itemTag.getInteger(TAG_FUEL);
        float progress = Math.min(1.0F, (float) fuel / maxFuel);
        return newDamage + damage * progress * MAX_FUEL_BONUS;
    }

    private float applyAmmoBonus(ItemStack tool, float damage, float newDamage) {
        NBTTagCompound itemTag = tool.getTagCompound();
        if (itemTag == null) return newDamage;
        byte[] ammo = itemTag.getByteArray(PistolSword.KEY_AMMO);
        if (ammo.length == 0) return newDamage;
        return newDamage + damage * ammo.length * BONUS_PER_AMMO;
    }
}