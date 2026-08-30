package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemHeatRayEmitter extends ToolPart {
    private static final Set<String> ALLOWED_MATERIALS = new HashSet<>(Arrays.asList("iron", "stone", "cobalt", "magmaslime", "popped_chorus", "redstone"));

    public ItemHeatRayEmitter() {
        super(Material.VALUE_Ingot * 8);
        setRegistryName("heat_ray_emitter");
        setTranslationKey("tinkersgadgets.heat_ray_emitter");
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        return ALLOWED_MATERIALS.contains(mat.getIdentifier());
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return HeatRayEmitterPartType.HEAT_RAY_EMITTER.equals(stat);
    }
}