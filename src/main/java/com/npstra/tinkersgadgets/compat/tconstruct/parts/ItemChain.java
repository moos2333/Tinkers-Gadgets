package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemChain extends ToolPart {
    private static final Set<String> ALLOWED_MATERIALS = new HashSet<>(Arrays.asList("iron", "cobalt", "stone", "nether_quartz", "slimeball", "blue_slimeball"));

    public ItemChain() {
        super(Material.VALUE_Ingot * 2);
        setRegistryName("chain");
        setTranslationKey("tinkersgadgets.chain");
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        return ALLOWED_MATERIALS.contains(mat.getIdentifier());
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return ChainPartType.CHAIN.equals(stat);
    }
}