package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.GripPartType;

import java.util.List;

public class GripMaterialStats extends AbstractMaterialStats {

    public GripMaterialStats() {
        super(GripPartType.GRIP);
    }

    @Override
    public List<String> getLocalizedInfo() {
        return ImmutableList.of(TextFormatting.GRAY + Util.translate("stat.grip.none"));
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(Util.translate("stat.grip.desc"));
    }
}