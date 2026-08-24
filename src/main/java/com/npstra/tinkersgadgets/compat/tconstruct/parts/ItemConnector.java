package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemConnector extends ToolPart {
    private static final Set<String> ALLOWED_MATERIALS = new HashSet<>(Arrays.asList(
            "glass", "slimeball", "popped_chorus", "nether_quartz", "magma_cream",
            "shulker_shell", "leather", "redstone", "blue_slimeball",
            "enderpearl", "prismarine_crystals"
    ));

    public ItemConnector() {
        super(Material.VALUE_Ingot);
        setRegistryName("connector");
        setTranslationKey("tinkersgadgets.connector");
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        return ALLOWED_MATERIALS.contains(mat.getIdentifier());
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return ConnectorPartType.CONNECTOR.equals(stat);
    }
}