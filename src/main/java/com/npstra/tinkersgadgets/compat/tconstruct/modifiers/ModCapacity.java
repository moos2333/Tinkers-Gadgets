package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.PistolSword;
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
    private static final int POINTS_PER_LEVEL = 20;
    private static final int FUEL_PER_LEVEL = 2000;
    private static final int AMMO_PER_LEVEL = 1;
    private static final int HEAT_PER_LEVEL = 1;

    private static final String KEY_BASE_FUEL = "baseMaxFuel";
    private static final String KEY_BASE_AMMO = "baseMaxAmmo";
    private static final String KEY_BASE_HEAT = "baseHeatCapacity";
    private static final String TAG_FUEL = "maxFuel";
    private static final String TAG_HEAT = "heatCapacity";

    public ModCapacity() {
        super("capacity_heatraygun", 0xCC5533, MAX_LEVEL, POINTS_PER_LEVEL);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.BRICK), 1, 1));
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Blocks.BRICK_BLOCK), 1, 4));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof HeatRayGun
                || stack.getItem() instanceof PistolSword;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        if (toolTag.hasKey(TAG_FUEL)) {
            applyHeatRayGun(toolTag, modData.current);
        } else if (toolTag.hasKey(PistolSword.KEY_MAX_AMMO)) {
            applyPistolSword(toolTag, modData.current);
        }
        TagUtil.setToolTag(rootCompound, toolTag);
    }

    private void applyHeatRayGun(NBTTagCompound toolTag, int points) {
        int levels = points / POINTS_PER_LEVEL;

        int fuelBase = toolTag.getInteger(KEY_BASE_FUEL);
        if (fuelBase == 0) {
            fuelBase = toolTag.getInteger(TAG_FUEL);
            toolTag.setInteger(KEY_BASE_FUEL, fuelBase);
        }
        toolTag.setInteger(TAG_FUEL, fuelBase + levels * FUEL_PER_LEVEL);

        int heatBase = toolTag.getInteger(KEY_BASE_HEAT);
        if (heatBase == 0) {
            heatBase = toolTag.getInteger(TAG_HEAT);
            toolTag.setInteger(KEY_BASE_HEAT, heatBase);
        }
        toolTag.setInteger(TAG_HEAT, heatBase + levels * HEAT_PER_LEVEL);
    }

    private void applyPistolSword(NBTTagCompound toolTag, int points) {
        int base = toolTag.getInteger(KEY_BASE_AMMO);
        if (base == 0) {
            base = toolTag.getInteger(PistolSword.KEY_MAX_AMMO);
            toolTag.setInteger(KEY_BASE_AMMO, base);
        }
        int levels = points / POINTS_PER_LEVEL;
        toolTag.setInteger(PistolSword.KEY_MAX_AMMO, base + levels * AMMO_PER_LEVEL);
    }
}