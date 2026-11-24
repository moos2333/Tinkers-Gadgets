package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class ItemFuelTank extends ToolPart {
    public ItemFuelTank() {
        super(Material.VALUE_Ingot * 8);
        setRegistryName("fuel_tank");
        setTranslationKey("tinkersgadgets.fuel_tank");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return FuelTankPartType.FUEL_TANK.equals(stat);
    }
}
