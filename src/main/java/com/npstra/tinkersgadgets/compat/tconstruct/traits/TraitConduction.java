package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitConduction extends AbstractTrait {

    private static final int MAX_STACKS_NORMAL = 5;
    private static final int MAX_STACKS_HEATRAY = 10;
    private static final float BONUS_PER_STACK = 0.10f;
    private static final int BURN_INTERVAL = 20;
    private static final int DECAY_INTERVAL = 100;
    private static final long RESET_THRESHOLD = 200;
    private static final int OFFHAND_SLOT = 40;
    private static final int OVERHEAT_DURATION = 100;

    private static final String KEY_STACKS = "conduction_stacks";
    private static final String KEY_LAST_CHANGE = "conduction_last_change";
    private static final String TAG_HEAT_CAPACITY = "heatCapacity";
    private static final String TAG_SHOT_COUNT = "ShotCount";
    private static final String TAG_OVERHEAT_END = "OverheatEndTick";

    public TraitConduction() {
        super("conduction_heatraygun", TextFormatting.GOLD);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        if (player == null) return newDamage;
        int stacks = TagUtil.getToolTag(tool).getInteger(KEY_STACKS);
        if (stacks <= 0) return newDamage;
        return newDamage + damage * stacks * BONUS_PER_STACK;
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        if (!isSelected && itemSlot != OFFHAND_SLOT) return;

        EntityPlayer player = (EntityPlayer) entity;
        NBTTagCompound tag = TagUtil.getToolTag(tool);
        int stacks = tag.getInteger(KEY_STACKS);
        long now = world.getTotalWorldTime();
        long lastChange = tag.getLong(KEY_LAST_CHANGE);

        if (lastChange == 0 || now - lastChange > RESET_THRESHOLD) {
            tag.setLong(KEY_LAST_CHANGE, now);
            TagUtil.setToolTag(tool, tag);
            return;
        }

        long elapsed = now - lastChange;

        if (player.isBurning()) {
            long gained = elapsed / BURN_INTERVAL;
            if (gained <= 0) return;
            int max = isHeatRayGun(tool) ? MAX_STACKS_HEATRAY : MAX_STACKS_NORMAL;
            stacks = (int) Math.min(max, stacks + gained);
            tag.setInteger(KEY_STACKS, stacks);
            tag.setLong(KEY_LAST_CHANGE, now);
            TagUtil.setToolTag(tool, tag);
        } else {
            if (stacks <= 0) return;
            long lost = elapsed / DECAY_INTERVAL;
            if (lost <= 0) return;
            stacks = (int) Math.max(0, stacks - lost);
            tag.setInteger(KEY_STACKS, stacks);
            tag.setLong(KEY_LAST_CHANGE, now);
            TagUtil.setToolTag(tool, tag);
        }
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
            root.setLong(TAG_OVERHEAT_END, player.world.getTotalWorldTime() + OVERHEAT_DURATION);
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