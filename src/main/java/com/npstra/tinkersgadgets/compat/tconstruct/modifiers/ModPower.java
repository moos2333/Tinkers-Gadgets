package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModPower extends ModifierTrait {
    private static final int MAX_LEVEL = 3;
    private static final float PER_LEVEL = 0.10f;
    private static final String KEY_BASE = "basePower";

    public ModPower() {
        super("power_heatraygun", 0xFF4500, MAX_LEVEL, 20);
        addRecipeMatch(new RecipeMatch.Item(new ItemStack(Items.FIRE_CHARGE), 1, 1));
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
            base = toolTag.getFloat("powerMultiplier");
            toolTag.setFloat(KEY_BASE, base);
        }
        float perPoint = PER_LEVEL / 20.0f;
        toolTag.setFloat("powerMultiplier", base * (1.0f + modData.current * perPoint));
        TagUtil.setToolTag(rootCompound, toolTag);
    }
}