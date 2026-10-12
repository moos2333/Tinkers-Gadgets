package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitSeasoned extends AbstractTrait {

    private static final String TAG_STACKS = "seasoned_stacks";
    private static final int MAX_STACKS = 15;
    private static final float CHANCE_PER_STACK = 0.01f;
    private static final int DECAY_INTERVAL = 100;

    public TraitSeasoned() {
        super("seasoned_chainblade", 0xCCCCCC);
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
        if (entity.world.isRemote) return newDamage;
        if (newDamage <= 0) return newDamage;
        NBTTagCompound tag = TinkerUtil.getModifierTag(tool, getModifierIdentifier());
        int stacks = tag.getInteger(TAG_STACKS);
        if (stacks > 0 && random.nextFloat() < stacks * CHANCE_PER_STACK) {
            return 0;
        }
        if (stacks < MAX_STACKS) {
            tag.setInteger(TAG_STACKS, stacks + 1);
        }
        return newDamage;
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (world.getTotalWorldTime() % DECAY_INTERVAL != 0) return;
        NBTTagCompound tag = TinkerUtil.getModifierTag(tool, getModifierIdentifier());
        int stacks = tag.getInteger(TAG_STACKS);
        if (stacks > 0) {
            tag.setInteger(TAG_STACKS, stacks - 1);
        }
    }
}