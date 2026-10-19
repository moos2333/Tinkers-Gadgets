package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import com.npstra.tinkersgadgets.compat.tconstruct.tools.Boomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.ChainBlade;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.HeatRayGun;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.ThrowingKnife;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModRapid extends ModifierTrait {
    private static final int MAX_LEVEL = 3;
    private static final int POINTS_PER_LEVEL = 20;
    private static final float[] HEAT_GUN_BONUS = {0.15f, 0.30f, 0.45f};
    private static final float[] CHAIN_BONUS = {0.25f, 0.45f, 0.60f};
    private static final float MAX_THROW_BONUS = 0.60f;

    public ModRapid() {
        super("rapid_heatraygun", 0xF5F5DC, MAX_LEVEL, POINTS_PER_LEVEL);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.SUGAR), 1, 1));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof HeatRayGun
                || stack.getItem() instanceof ChainBlade
                || stack.getItem() instanceof ThrowingKnife
                || stack.getItem() instanceof Boomerang;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        if (toolTag.hasKey("speedRate")) {
            if (!toolTag.hasKey("baseSpeedRate")) {
                toolTag.setFloat("baseSpeedRate", toolTag.getFloat("speedRate"));
            }
            float bonus = calculateBonus(modData.current, CHAIN_BONUS);
            toolTag.setFloat("speedRate", toolTag.getFloat("baseSpeedRate") + bonus);
        } else if (toolTag.hasKey("chargeTime")) {
            if (!toolTag.hasKey("baseChargeTime")) {
                toolTag.setInteger("baseChargeTime", toolTag.getInteger("chargeTime"));
            }
            int base = toolTag.getInteger("baseChargeTime");
            float bonus = calculateBonus(modData.current, HEAT_GUN_BONUS);
            toolTag.setInteger("chargeTime", Math.max(1, Math.round(base / (1.0f + bonus))));
        } else {
            float bonus = Math.min(MAX_THROW_BONUS, modData.current * 0.01f);
            toolTag.setFloat("rapid_bonus", bonus);
        }
        TagUtil.setToolTag(rootCompound, toolTag);
    }

    private float calculateBonus(int points, float[] levelBonus) {
        float bonus = 0;
        int remaining = points;
        for (int i = 0; i < MAX_LEVEL && remaining > 0; i++) {
            int seg = Math.min(remaining, POINTS_PER_LEVEL);
            float start = (i == 0) ? 0 : levelBonus[i - 1];
            bonus += (levelBonus[i] - start) * seg / (float) POINTS_PER_LEVEL;
            remaining -= seg;
        }
        return Math.min(bonus, 0.99f);
    }
}