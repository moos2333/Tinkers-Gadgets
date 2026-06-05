package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.FuelTankPartType;
import java.util.List;

public class FuelTankMaterialStats extends AbstractMaterialStats {
    public static final FuelTankMaterialStats UNKNOWN = new FuelTankMaterialStats(0, 0, 1.0f);
    public final int maxFuel;
    public final int heatCapacity;
    public final float efficiency;

    public FuelTankMaterialStats(int maxFuel, int heatCapacity, float efficiency) {
        super(FuelTankPartType.FUEL_TANK);
        this.maxFuel = maxFuel;
        this.heatCapacity = heatCapacity;
        this.efficiency = Math.max(0.1f, Math.min(efficiency, 2.0f));
    }

    @Override
    public List<String> getLocalizedInfo() {
        int effPercent = (int)((efficiency - 1.0f) * 100);
        String effColor = effPercent >= 0 ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
        String effSign = effPercent > 0 ? "+" : "";
        return ImmutableList.of(
                TextFormatting.GOLD + Util.translateFormatted("stat.fuel_tank.max_fuel.name", maxFuel),
                TextFormatting.GOLD + Util.translateFormatted("stat.fuel_tank.heat_capacity.name", heatCapacity),
                TextFormatting.GOLD + Util.translateFormatted("stat.fuel_tank.efficiency.name", effColor + effSign + effPercent + "%" + TextFormatting.RESET)
        );
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(
                Util.translate("stat.fuel_tank.max_fuel.desc"),
                Util.translate("stat.fuel_tank.heat_capacity.desc"),
                Util.translate("stat.fuel_tank.efficiency.desc")
        );
    }
}