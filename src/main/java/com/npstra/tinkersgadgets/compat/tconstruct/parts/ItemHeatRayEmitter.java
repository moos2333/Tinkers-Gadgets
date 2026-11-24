package com.npstra.tinkersgadgets.compat.tconstruct.parts;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class ItemHeatRayEmitter extends ToolPart {
    public ItemHeatRayEmitter() {
        super(Material.VALUE_Ingot * 8);
        setRegistryName("heat_ray_emitter");
        setTranslationKey("tinkersgadgets.heat_ray_emitter");
    }

    @Override
    public boolean hasUseForStat(String stat) {
        return HeatRayEmitterPartType.HEAT_RAY_EMITTER.equals(stat);
    }
}
