package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class ItemGrip extends ToolPart {
    public ItemGrip() {
        super(Material.VALUE_Ingot);
        setRegistryName("grip");
        setTranslationKey("tinkersgadgets.grip");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return GripPartType.GRIP.equals(stat);
    }
}
