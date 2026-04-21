package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitKeen extends AbstractTrait {
    private static final String TAG_ATTACHED = "keen_attached";
    private static final String TAG_ATTACH_TARGET = "keen_target";
    private static final String TAG_ATTACH_TICKS = "keen_ticks";
    private static final int TICK_INTERVAL = 20;
    private static final int MAX_ATTACH_TICKS = 100;
    private static final float DAMAGE_PERCENT = 0.75f;

    public TraitKeen() {
        super("keen", 0xAA0000);
    }

    @Override
    public void onUpdate(ItemStack tool, net.minecraft.world.World world, net.minecraft.entity.Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote || !(tool.getItem() instanceof slimeknights.tconstruct.library.tools.ToolCore)) return;
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase holder = (EntityLivingBase) entity;
        NBTTagCompound root = TagUtil.getTagSafe(tool);
        if (!root.getBoolean(TAG_ATTACHED)) return;

        int targetId = root.getInteger(TAG_ATTACH_TARGET);
        EntityLivingBase target = (EntityLivingBase) world.getEntityByID(targetId);
        if (target == null || !target.isEntityAlive()) {
            root.setBoolean(TAG_ATTACHED, false);
            tool.setTagCompound(root);
            return;
        }

        int totalTicks = root.getInteger(TAG_ATTACH_TICKS) + 1;
        if (totalTicks >= MAX_ATTACH_TICKS) {
            root.setBoolean(TAG_ATTACHED, false);
            tool.setTagCompound(root);
            return;
        }

        if (totalTicks % TICK_INTERVAL == 0) {
            float damage = (float) (slimeknights.tconstruct.library.utils.ToolHelper.getActualDamage(tool, holder) * DAMAGE_PERCENT);
            target.attackEntityFrom(net.minecraft.util.DamageSource.causeMobDamage(holder), damage);
            target.hurtResistantTime = 0;
        }
        root.setInteger(TAG_ATTACH_TICKS, totalTicks);
        tool.setTagCompound(root);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (!wasHit || !target.isEntityAlive()) return;
        if (!(tool.getItem() instanceof slimeknights.tconstruct.library.tools.ToolCore)) return;
        NBTTagCompound root = TagUtil.getTagSafe(tool);
        root.setBoolean(TAG_ATTACHED, true);
        root.setInteger(TAG_ATTACH_TARGET, target.getEntityId());
        root.setInteger(TAG_ATTACH_TICKS, 0);
        tool.setTagCompound(root);
    }
}