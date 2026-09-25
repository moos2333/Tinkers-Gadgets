package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemGunBarrel extends ToolPart {

    private static final Set<String> ALLOWED_MATERIALS = new HashSet<>(Arrays.asList(
            "stone", "iron", "cobalt", "nether_brick", "brick", "popped_chorus", "redstone", "nether_quartz", "shulker_shell"
    ));

    public ItemGunBarrel() {
        super(Material.VALUE_Ingot * 2);
        setRegistryName("gun_barrel");
        setTranslationKey("tinkersgadgets.gun_barrel");
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        return ALLOWED_MATERIALS.contains(mat.getIdentifier());
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return GunBarrelPartType.GUN_BARREL.equals(stat);
    }
}