package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class ItemChain extends ToolPart {
    public ItemChain() {
        super(Material.VALUE_Ingot * 2);
        setRegistryName("chain");
        setTranslationKey("tinkersgadgets.chain");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return ChainPartType.CHAIN.equals(stat);
    }
}
