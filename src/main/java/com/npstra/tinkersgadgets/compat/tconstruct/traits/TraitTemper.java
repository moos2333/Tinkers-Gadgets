package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitTemper extends AbstractTrait {
    private static final String TAG_TEMPER_READY = "temper_ready";

    public TraitTemper() {
        super("temper_heatraygun", TextFormatting.GOLD);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (!(entity instanceof EntityLivingBase)) return;
        if (!(tool.getItem() instanceof HeatRayGun)) return;

        NBTTagCompound itemTag = tool.getTagCompound();
        if (itemTag == null) return;
        NBTTagCompound traitTag = TinkerUtil.getModifierTag(tool, getModifierIdentifier());

        boolean wasOverheated = itemTag.hasKey("OverheatEndTick");
        long overheatEnd = itemTag.getLong("OverheatEndTick");
        long currentTime = world.getTotalWorldTime();
        boolean ready = traitTag.getBoolean(TAG_TEMPER_READY);

        if (wasOverheated && currentTime >= overheatEnd) {
            if (!ready) traitTag.setBoolean(TAG_TEMPER_READY, true);
        } else {
            if (ready) traitTag.setBoolean(TAG_TEMPER_READY, false);
        }
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        NBTTagCompound traitTag = TinkerUtil.getModifierTag(tool, getModifierIdentifier());
        if (traitTag.getBoolean(TAG_TEMPER_READY)) {
            traitTag.setBoolean(TAG_TEMPER_READY, false);
            return newDamage + damage;
        }
        return newDamage;
    }
}