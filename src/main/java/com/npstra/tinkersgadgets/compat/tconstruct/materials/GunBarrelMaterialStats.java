package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.google.common.collect.ImmutableList;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.GunBarrelPartType;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;

import java.util.List;

public class GunBarrelMaterialStats extends AbstractMaterialStats {

    private static final String LOC_AMMO = "stat.gun_barrel.ammo";
    private static final String LOC_DAMAGE = "stat.gun_barrel.damage";
    private static final String LOC_DURABILITY = "stat.gun_barrel.durability";

    public static final GunBarrelMaterialStats UNKNOWN = new GunBarrelMaterialStats(0, 1.0f, 0);

    public final int ammoBonus;
    public final float rangedDamage;
    public final int durabilityBonus;

    public GunBarrelMaterialStats(int ammoBonus, float rangedDamage, int durabilityBonus) {
        super(GunBarrelPartType.GUN_BARREL);
        this.ammoBonus = Math.max(-5, Math.min(10, ammoBonus));
        this.rangedDamage = Math.max(0.01f, Math.min(10.0f, rangedDamage));
        this.durabilityBonus = Math.max(0, Math.min(9999, durabilityBonus));
    }

    @Override
    public List<String> getLocalizedInfo() {
        return ImmutableList.of(
                formatAmmo(LOC_AMMO + ".name", ammoBonus),
                formatDamage(LOC_DAMAGE + ".name", rangedDamage),
                formatDurability(LOC_DURABILITY + ".name", durabilityBonus)
        );
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(
                Util.translate(LOC_AMMO + ".desc"),
                Util.translate(LOC_DAMAGE + ".desc"),
                Util.translate(LOC_DURABILITY + ".desc")
        );
    }

    private static String formatAmmo(String loc, int value) {
        String color = value == 0 ? TextFormatting.GRAY.toString() : (value > 0 ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString());
        String sign = value > 0 ? "+" : "";
        return Util.translateFormatted(loc, color + sign + value + TextFormatting.RESET.toString());
    }

    private static String formatDamage(String loc, float value) {
        int percent = Math.round((value - 1.0f) * 100);
        String color = percent == 0 ? TextFormatting.GRAY.toString() : (percent > 0 ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString());
        String sign = percent > 0 ? "+" : "";
        return Util.translateFormatted(loc, color + sign + percent + "%" + TextFormatting.RESET.toString());
    }

    private static String formatDurability(String loc, int value) {
        String color = value > 0 ? TextFormatting.GREEN.toString() : TextFormatting.GRAY.toString();
        String sign = value > 0 ? "+" : "";
        return Util.translateFormatted(loc, color + sign + value + TextFormatting.RESET.toString());
    }
}