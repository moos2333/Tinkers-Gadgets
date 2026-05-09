package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class ItemConnector extends ToolPart {

    public ItemConnector() {
        super(Material.VALUE_Ingot);
        setRegistryName("connector");
        setTranslationKey("tinkersgadgets.connector");
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        String id = mat.getIdentifier();
        return id.equals("glass") || id.equals("slimeball") ||
                id.equals("popped_chorus") || id.equals("nether_quartz")
                || id.equals("magma_cream") || id.equals("shulker_shell")
                || id.equals("leather") || id.equals("redstone") ||
                id.equals("blue_slimeball");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return ConnectorPartType.CONNECTOR.equals(stat);
    }
}