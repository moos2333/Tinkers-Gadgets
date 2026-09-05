package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ChainPartType;

import java.util.List;

public class ChainMaterialStats extends AbstractMaterialStats {
    public static final ChainMaterialStats UNKNOWN = new ChainMaterialStats(3, 4.0f, 0, 0.0f);
    public final int maxBounces;
    public final float bounceRange;
    public final int ammoBonus;
    public final float speedRate;

    public ChainMaterialStats(int maxBounces, float bounceRange, int ammoBonus, float speedRate) {
        super(ChainPartType.CHAIN);
        this.maxBounces = Math.max(1, Math.min(6, maxBounces));
        this.bounceRange = Math.max(1.0f, Math.min(6.0f, bounceRange));
        this.ammoBonus = Math.max(0, Math.min(30, ammoBonus));
        this.speedRate = Math.max(-0.5f, Math.min(1.0f, speedRate));
    }

    private String formatPercent(float value) {
        String sign = value >= 0 ? "+" : "-";
        String percent = String.format("%.0f", Math.abs(value * 100));
        TextFormatting color;
        if (value > 0) color = TextFormatting.GREEN;
        else if (value < 0) color = TextFormatting.RED;
        else color = TextFormatting.GRAY;
        return TextFormatting.GRAY + Util.translate("stat.chain.speed_rate.name") + " " +
                color + sign + percent + "%" + TextFormatting.RESET;
    }

    @Override
    public List<String> getLocalizedInfo() {
        return ImmutableList.of(
                TextFormatting.GRAY + Util.translate("stat.chain.max_bounces.name") + " " + TextFormatting.GOLD + maxBounces,
                TextFormatting.GRAY + Util.translate("stat.chain.bounce_range.name") + " " + TextFormatting.GOLD + String.format("%.1f", bounceRange) + "m",
                formatPercent(speedRate),
                TextFormatting.GRAY + Util.translate("stat.chain.ammo_bonus.name") + " " + TextFormatting.GOLD + "+" + ammoBonus
        );
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(
                Util.translate("stat.chain.max_bounces.desc"),
                Util.translate("stat.chain.bounce_range.desc"),
                Util.translate("stat.chain.speed_rate.desc"),
                Util.translate("stat.chain.ammo_bonus.desc")
        );
    }
}