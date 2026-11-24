package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class ItemGunBarrel extends ToolPart {
    public ItemGunBarrel() {
        super(Material.VALUE_Ingot * 2);
        setRegistryName("gun_barrel");
        setTranslationKey("tinkersgadgets.gun_barrel");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return GunBarrelPartType.GUN_BARREL.equals(stat);
    }
}
