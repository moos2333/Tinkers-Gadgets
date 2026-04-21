package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import net.minecraft.item.ItemStack;
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
                || id.equals("magma_cream") || id.equals("shulker_shell");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return ConnectorPartType.CONNECTOR.equals(stat);
    }
}