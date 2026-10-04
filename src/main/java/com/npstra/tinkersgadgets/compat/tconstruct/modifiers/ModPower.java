package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import com.npstra.tinkersgadgets.compat.tconstruct.tools.ChainBlade;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.PistolSword;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModPower extends ModifierTrait {

    private static final int MAX_LEVEL = 3;
    private static final int POINTS_PER_LEVEL = 20;
    private static final float PER_LEVEL = 0.10f;
    private static final float CHAIN_PER_LEVEL = 0.02f;

    private static final String KEY_BASE = "basePower";
    private static final String KEY_BASE_COMBO = "baseComboBonus";
    private static final String KEY_BASE_PISTOL = "basePistolRanged";

    private static final String TAG_POWER = "powerMultiplier";
    private static final String TAG_COMBO = "comboBonus";
    private static final String TAG_PISTOL = "gunBarrelRanged";

    public ModPower() {
        super("power_heatraygun", 0xFF4500, MAX_LEVEL, POINTS_PER_LEVEL);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.FIRE_CHARGE), 1, 1));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof HeatRayGun
                || stack.getItem() instanceof ChainBlade
                || stack.getItem() instanceof PistolSword;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        if (toolTag.hasKey(TAG_POWER)) {
            applyMultiplicative(toolTag, modData.current, KEY_BASE, TAG_POWER, PER_LEVEL);
        } else if (toolTag.hasKey(TAG_COMBO)) {
            applyAdditive(toolTag, modData.current, KEY_BASE_COMBO, TAG_COMBO, CHAIN_PER_LEVEL);
        } else if (toolTag.hasKey(TAG_PISTOL)) {
            applyMultiplicative(toolTag, modData.current, KEY_BASE_PISTOL, TAG_PISTOL, PER_LEVEL);
        }
        TagUtil.setToolTag(rootCompound, toolTag);
    }

    private void applyMultiplicative(NBTTagCompound toolTag, int points, String baseKey, String targetKey, float perLevel) {
        float base = toolTag.getFloat(baseKey);
        if (base == 0.0f) {
            base = toolTag.getFloat(targetKey);
            toolTag.setFloat(baseKey, base);
        }
        float perPoint = perLevel / POINTS_PER_LEVEL;
        toolTag.setFloat(targetKey, base * (1.0f + points * perPoint));
    }

    private void applyAdditive(NBTTagCompound toolTag, int points, String baseKey, String targetKey, float perLevel) {
        float base = toolTag.getFloat(baseKey);
        if (base == 0.0f) {
            base = toolTag.getFloat(targetKey);
            toolTag.setFloat(baseKey, base);
        }
        float perPoint = perLevel / POINTS_PER_LEVEL;
        toolTag.setFloat(targetKey, base + points * perPoint);
    }
}