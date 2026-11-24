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
    public boolean hasUseForStat(String stat) {
        return ConnectorPartType.CONNECTOR.equals(stat);
    }
}
