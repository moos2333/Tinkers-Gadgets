package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitCondensation extends AbstractTrait {
    private static final String TAG_LAST_COOLDOWN = "condensation_last_cooldown";
    private static final int COOLDOWN_INTERVAL = 100;

    public TraitCondensation() {
        super("condensation_heatraygun", TextFormatting.AQUA);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (player.getActiveItemStack() == tool) return;

        boolean inHotbar = itemSlot >= 0 && itemSlot <= 8;
        boolean inOffhand = player.getHeldItemOffhand() == tool;
        if (!inHotbar && !inOffhand) return;

        NBTTagCompound itemTag = tool.getTagCompound();
        if (itemTag == null) return;

        int shotCount = itemTag.getInteger("ShotCount");
        if (shotCount <= 0) return;

        NBTTagCompound traitTag = TinkerUtil.getModifierTag(tool, getModifierIdentifier());
        long currentTick = world.getTotalWorldTime();
        long lastTick = traitTag.getLong(TAG_LAST_COOLDOWN);
        if (lastTick == 0L) {
            traitTag.setLong(TAG_LAST_COOLDOWN, currentTick);
            return;
        }

        if (currentTick - lastTick >= COOLDOWN_INTERVAL) {
            itemTag.setInteger("ShotCount", shotCount - 1);
            traitTag.setLong(TAG_LAST_COOLDOWN, currentTick);
        }
    }
}