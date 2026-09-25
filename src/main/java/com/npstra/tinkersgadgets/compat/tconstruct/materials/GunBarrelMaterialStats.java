package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.npstra.tinkersgadgets.compat.tconstruct.parts.GunBarrelPartType;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;

import java.util.ArrayList;
import java.util.List;

public class GunBarrelMaterialStats extends AbstractMaterialStats {

    public static final String LOC_AMMO = "stat.gun_barrel.ammo.name";
    public static final String LOC_DAMAGE = "stat.gun_barrel.damage.name";
    public static final String LOC_DURABILITY = "stat.gun_barrel.durability.name";

    public static final GunBarrelMaterialStats UNKNOWN = new GunBarrelMaterialStats(0, 1.0f, 0);

    public final int ammoBonus;
    public final float rangedDamage;
    public final int durabilityBonus;

    public GunBarrelMaterialStats(int ammoBonus, float rangedDamage, int durabilityBonus) {
        super(GunBarrelPartType.GUN_BARREL);
        this.ammoBonus = Math.max(-3, Math.min(3, ammoBonus));
        this.rangedDamage = Math.max(0.5f, Math.min(1.5f, rangedDamage));
        this.durabilityBonus = Math.max(0, Math.min(500, durabilityBonus));
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();
        info.add(formatAmmo(LOC_AMMO, ammoBonus));
        info.add(formatDamage(LOC_DAMAGE, rangedDamage));
        info.add(formatDurability(LOC_DURABILITY, durabilityBonus));
        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> desc = new ArrayList<>();
        desc.add(Util.translate(LOC_AMMO + ".desc"));
        desc.add(Util.translate(LOC_DAMAGE + ".desc"));
        desc.add(Util.translate(LOC_DURABILITY + ".desc"));
        return desc;
    }

    private static String formatAmmo(String loc, int value) {
        String color = value == 0 ? "§7" : (value > 0 ? "§a" : "§c");
        String sign = value > 0 ? "+" : "";
        return Util.translateFormatted(loc, color + sign + value + "§r");
    }

    private static String formatDamage(String loc, float value) {
        int percent = Math.round((value - 1.0f) * 100);
        String color = percent == 0 ? "§7" : (percent > 0 ? "§a" : "§c");
        String sign = percent > 0 ? "+" : "";
        return Util.translateFormatted(loc, color + sign + percent + "%§r");
    }

    private static String formatDurability(String loc, int value) {
        String color = value > 0 ? "§a" : "§7";
        String sign = value > 0 ? "+" : "";
        return Util.translateFormatted(loc, color + sign + value + "§r");
    }
}