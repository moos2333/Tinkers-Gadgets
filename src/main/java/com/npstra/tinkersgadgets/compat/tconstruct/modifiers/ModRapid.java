package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModRapid extends ModifierTrait {
    private static final int MAX_LEVEL = 3;
    private static final int POINTS_PER_LEVEL = 45;
    private static final float[] LEVEL_BONUS = {0.10f, 0.18f, 0.24f};
    private static final String KEY_BASE = "baseChargeTime";

    public ModRapid() {
        super("rapid_heatraygun", 0xFF8C00, MAX_LEVEL, POINTS_PER_LEVEL);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.SUGAR), 1, 1));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        float baseEfficiency = 1.5f / toolTag.getInteger("chargeTime");
        if (!toolTag.hasKey(KEY_BASE)) {
            toolTag.setFloat(KEY_BASE, baseEfficiency);
        }
        float base = toolTag.getFloat(KEY_BASE);
        int points = modData.current;
        float bonus = 0;
        int remaining = points;
        for (int i = 0; i < MAX_LEVEL && remaining > 0; i++) {
            int seg = Math.min(remaining, POINTS_PER_LEVEL);
            float start = (i == 0) ? 0 : LEVEL_BONUS[i - 1];
            bonus += (LEVEL_BONUS[i] - start) * seg / (float) POINTS_PER_LEVEL;
            remaining -= seg;
        }
        float newEfficiency = base * (1.0f + Math.min(bonus, 0.99f));
        int newChargeTime = Math.max(1, Math.round(1.5f / newEfficiency));
        toolTag.setInteger("chargeTime", newChargeTime);
        TagUtil.setToolTag(rootCompound, toolTag);
    }
}