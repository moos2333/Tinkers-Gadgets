package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ConnectorPartType;

import java.util.List;

public class ConnectorMaterialStats extends AbstractMaterialStats {

    public ConnectorMaterialStats() {
        super(ConnectorPartType.CONNECTOR);
    }

    @Override
    public List<String> getLocalizedInfo() {
        return ImmutableList.of(TextFormatting.GRAY + Util.translate("stat.connector.none"));
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(Util.translate("stat.connector.desc"));
    }
}