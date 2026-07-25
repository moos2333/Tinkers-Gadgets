package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitTemper extends AbstractTrait {
    private static final String TAG_TEMPER_READY = "temper_ready";

    public TraitTemper() {
        super("temper_heatraygun", TextFormatting.GOLD);
    }

    @Override
    public void onUpdate(ItemStack tool, net.minecraft.world.World world, net.minecraft.entity.Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof EntityLivingBase)) return;
        if (!(tool.getItem() instanceof com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun)) return;
        NBTTagCompound itemTag = tool.getTagCompound();
        if (itemTag == null) return;
        long overheatEnd = itemTag.getLong("OverheatEndTick");
        long currentTime = world.getTotalWorldTime();
        boolean wasOverheated = itemTag.hasKey("OverheatEndTick");
        if (wasOverheated && currentTime >= overheatEnd) {
            if (!itemTag.getBoolean(TAG_TEMPER_READY)) {
                itemTag.setBoolean(TAG_TEMPER_READY, true);
                tool.setTagCompound(itemTag);
            }
        } else {
            if (itemTag.getBoolean(TAG_TEMPER_READY)) {
                itemTag.removeTag(TAG_TEMPER_READY);
                tool.setTagCompound(itemTag);
            }
        }
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        NBTTagCompound itemTag = tool.getTagCompound();
        if (itemTag == null) return newDamage;
        if (itemTag.getBoolean(TAG_TEMPER_READY)) {
            itemTag.removeTag(TAG_TEMPER_READY);
            tool.setTagCompound(itemTag);
            return newDamage + damage;
        }
        return newDamage;
    }
}