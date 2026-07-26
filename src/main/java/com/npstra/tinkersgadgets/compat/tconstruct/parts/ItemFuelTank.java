package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemFuelTank extends ToolPart {
    private static final Set<String> ALLOWED_MATERIALS = new HashSet<>(Arrays.asList("brick", "stone", "netherbrick", "iron", "cobalt", "shulker_shell", "prismarine_crystals"));

    public ItemFuelTank() {
        super(Material.VALUE_Ingot * 8);
        setRegistryName("fuel_tank");
        setTranslationKey("tinkersgadgets.fuel_tank");
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        return ALLOWED_MATERIALS.contains(mat.getIdentifier());
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return FuelTankPartType.FUEL_TANK.equals(stat);
    }
}