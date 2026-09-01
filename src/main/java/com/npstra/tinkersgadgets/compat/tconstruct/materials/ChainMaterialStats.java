package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ChainPartType;

import java.util.List;

public class ChainMaterialStats extends AbstractMaterialStats {
    public static final ChainMaterialStats UNKNOWN = new ChainMaterialStats(3, 4.0f, 0.0f, 0.0f);
    public final int maxBounces;
    public final float bounceRange;
    public final float sweepRangeBonus;
    public final float damageBonus;

    public ChainMaterialStats(int maxBounces, float bounceRange, float sweepRangeBonus, float damageBonus) {
        super(ChainPartType.CHAIN);
        this.maxBounces = Math.max(2, Math.min(6, maxBounces));
        this.bounceRange = Math.max(2.0f, Math.min(6.0f, bounceRange));
        this.sweepRangeBonus = Math.max(0.0f, Math.min(1.0f, sweepRangeBonus));
        this.damageBonus = Math.max(-0.5f, Math.min(1.0f, damageBonus));
    }

    @Override
    public List<String> getLocalizedInfo() {
        return ImmutableList.of(
                TextFormatting.GRAY + Util.translateFormatted("stat.chain.max_bounces.name", TextFormatting.GOLD + String.valueOf(maxBounces)),
                TextFormatting.GRAY + Util.translateFormatted("stat.chain.bounce_range.name", TextFormatting.GOLD + String.format("%.1f", bounceRange) + "m"),
                TextFormatting.GRAY + Util.translateFormatted("stat.chain.sweep_range.name", TextFormatting.GOLD + formatPercent(sweepRangeBonus)),
                TextFormatting.GRAY + Util.translateFormatted("stat.chain.damage_bonus.name", TextFormatting.GOLD + formatPercent(damageBonus))
        );
    }

    private String formatPercent(float value) {
        int percent = (int)(value * 100);
        return (percent >= 0 ? "+" : "") + percent + "%";
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(
                Util.translate("stat.chain.max_bounces.desc"),
                Util.translate("stat.chain.bounce_range.desc"),
                Util.translate("stat.chain.sweep_range.desc"),
                Util.translate("stat.chain.damage_bonus.desc")
        );
    }
}