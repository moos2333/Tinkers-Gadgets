package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModEfficiency extends ModifierTrait {
    private static final int MAX_LEVEL = 3;
    private static final int POINTS_PER_LEVEL = 30;
    private static final float BONUS_PER_POINT = 0.005f;
    private static final String KEY_BASE = "baseEfficiency";

    public ModEfficiency() {
        super("efficiency_heatraygun", 0x66CC99, MAX_LEVEL, POINTS_PER_LEVEL);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.GLASS_BOTTLE), 1, 1));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        float base = toolTag.getFloat(KEY_BASE);
        if (base == 0.0f) {
            base = toolTag.getFloat("fuelEfficiency");
            toolTag.setFloat(KEY_BASE, base);
        }
        float totalBonus = modData.current * BONUS_PER_POINT;
        toolTag.setFloat("fuelEfficiency", base * (1.0f + totalBonus));
        TagUtil.setToolTag(rootCompound, toolTag);
    }
}