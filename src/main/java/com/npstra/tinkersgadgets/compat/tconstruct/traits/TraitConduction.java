package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitConduction extends AbstractTrait {
    private static final float DAMAGE_BONUS = 1.0f;

    public TraitConduction() {
        super("conduction_heatraygun", TextFormatting.GOLD);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        if (player == null || !player.isBurning()) return newDamage;
        return newDamage + damage * DAMAGE_BONUS;
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (!wasHit || player.world.isRemote) return;
        if (!player.isBurning()) return;
        NBTTagCompound root = tool.getTagCompound();
        if (root == null) return;
        int shotCount = root.getInteger("ShotCount");
        int threshold = getHeatThreshold(tool);
        if (threshold <= 0) return;
        int newShotCount = shotCount + 1;
        if (newShotCount >= threshold) {
            root.setLong("OverheatEndTick", player.world.getTotalWorldTime() + 100);
            root.setInteger("ShotCount", 0);
        } else {
            root.setInteger("ShotCount", newShotCount);
        }
    }

    private int getHeatThreshold(ItemStack stack) {
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey("heatCapacity")) {
            int threshold = toolTag.getInteger("heatCapacity");
            return threshold > 0 ? threshold : 10;
        }
        return 10;
    }
}