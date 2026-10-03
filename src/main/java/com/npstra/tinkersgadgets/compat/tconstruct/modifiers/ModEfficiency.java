package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import com.npstra.tinkersgadgets.compat.tconstruct.tools.ChainBlade;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
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
    private static final float FUEL_BONUS_PER_POINT = 0.01f;

    private static final String KEY_BASE_FUEL = "baseEfficiency";
    private static final String TAG_FUEL_EFFICIENCY = "fuelEfficiency";
    private static final String TAG_COMBO_BONUS = "comboBonus";
    private static final String TAG_EFFICIENCY_LEVEL = "efficiency_level";

    public ModEfficiency() {
        super("efficiency_heatraygun", 0x66CC99, MAX_LEVEL, POINTS_PER_LEVEL);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.GLASS_BOTTLE), 1, 1));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof HeatRayGun
                || stack.getItem() instanceof ChainBlade;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        if (toolTag.hasKey(TAG_FUEL_EFFICIENCY)) {
            applyHeatRayGun(toolTag, modData.current);
        } else if (toolTag.hasKey(TAG_COMBO_BONUS)) {
            applyChainBlade(toolTag, modData.current);
        }
        TagUtil.setToolTag(rootCompound, toolTag);
    }

    private void applyHeatRayGun(NBTTagCompound toolTag, int points) {
        float base = toolTag.getFloat(KEY_BASE_FUEL);
        if (base == 0.0f) {
            base = toolTag.getFloat(TAG_FUEL_EFFICIENCY);
            toolTag.setFloat(KEY_BASE_FUEL, base);
        }
        float totalBonus = points * FUEL_BONUS_PER_POINT;
        toolTag.setFloat(TAG_FUEL_EFFICIENCY, base * (1.0f + totalBonus));
    }

    private void applyChainBlade(NBTTagCompound toolTag, int points) {
        int level = Math.min(points / POINTS_PER_LEVEL, MAX_LEVEL);
        toolTag.setInteger(TAG_EFFICIENCY_LEVEL, level);
    }
}