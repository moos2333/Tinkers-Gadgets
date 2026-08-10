package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModCapacity extends ModifierTrait {
    private static final int MAX_LEVEL = 3;
    private static final int PER_LEVEL = 2000;
    private static final String KEY_BASE = "baseMaxFuel";

    public ModCapacity() {
        super("capacity_heatraygun", 0xCC5533, MAX_LEVEL, 20);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.BRICK), 1, 1));
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Blocks.BRICK_BLOCK), 1, 4));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        int base = toolTag.getInteger(KEY_BASE);
        if (base == 0) {
            base = toolTag.getInteger("maxFuel");
            toolTag.setInteger(KEY_BASE, base);
        }
        int totalBonus = modData.current * (PER_LEVEL / 20);
        toolTag.setInteger("maxFuel", base + totalBonus);
        TagUtil.setToolTag(rootCompound, toolTag);
    }
}