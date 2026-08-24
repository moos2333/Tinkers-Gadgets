package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemGrip extends ToolPart {
    private static final Set<String> ALLOWED_MATERIALS = new HashSet<>(Arrays.asList(
            "leather", "wool", "slimeball", "shulker_shell", "blue_slimeball",
            "popped_chorus", "redstone", "enderpearl", "nether_quartz", "iron"
    ));

    public ItemGrip() {
        super(Material.VALUE_Ingot);
        setRegistryName("grip");
        setTranslationKey("tinkersgadgets.grip");
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        return ALLOWED_MATERIALS.contains(mat.getIdentifier());
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return GripPartType.GRIP.equals(stat);
    }
}