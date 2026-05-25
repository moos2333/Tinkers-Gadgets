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
    public boolean canUseMaterial(Material mat) {
        String id = mat.getIdentifier();
        return id.equals("leather") || id.equals("wool") || id.equals("slimeball") || id.equals("shulker_shell");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return GripPartType.GRIP.equals(stat);
    }
}