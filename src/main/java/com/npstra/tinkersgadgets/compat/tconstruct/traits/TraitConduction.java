package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitConduction extends AbstractTrait {

    private static final float GENERIC_BONUS = 0.40f;
    private static final float HEATRAY_BONUS = 0.80f;

    private static final String TAG_HEAT_CAPACITY = "heatCapacity";
    private static final String TAG_SHOT_COUNT = "ShotCount";
    private static final String TAG_OVERHEAT_END = "OverheatEndTick";

    public TraitConduction() {
        super("conduction_heatraygun", TextFormatting.GOLD);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        if (player == null || !player.isBurning()) return newDamage;
        float bonus = isHeatRayGun(tool) ? HEATRAY_BONUS : GENERIC_BONUS;
        return newDamage + damage * bonus;
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                         float damageDealt, boolean wasCritical, boolean wasHit) {
        if (!wasHit || player.world.isRemote) return;
        if (!player.isBurning()) return;
        if (!isHeatRayGun(tool)) return;

        NBTTagCompound root = tool.getTagCompound();
        if (root == null) return;

        int shotCount = root.getInteger(TAG_SHOT_COUNT);
        int threshold = getHeatThreshold(tool);
        if (threshold <= 0) return;

        int newShotCount = shotCount + 1;
        if (newShotCount >= threshold) {
            root.setLong(TAG_OVERHEAT_END, player.world.getTotalWorldTime() + 100);
            root.setInteger(TAG_SHOT_COUNT, 0);
        } else {
            root.setInteger(TAG_SHOT_COUNT, newShotCount);
        }
    }

    private boolean isHeatRayGun(ItemStack tool) {
        NBTTagCompound toolTag = TagUtil.getToolTag(tool);
        return toolTag != null && toolTag.hasKey(TAG_HEAT_CAPACITY);
    }

    private int getHeatThreshold(ItemStack stack) {
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey(TAG_HEAT_CAPACITY)) {
            int threshold = toolTag.getInteger(TAG_HEAT_CAPACITY);
            return threshold > 0 ? threshold : 10;
        }
        return 10;
    }
}