package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.HeatRayEmitterPartType;

import java.util.List;

public class HeatRayEmitterMaterialStats extends AbstractMaterialStats {
    public static final HeatRayEmitterMaterialStats UNKNOWN = new HeatRayEmitterMaterialStats(1.5f, 1.0f);
    public final float chargeTime;
    public final float power;

    public HeatRayEmitterMaterialStats(float chargeTime, float power) {
        super(HeatRayEmitterPartType.HEAT_RAY_EMITTER);
        this.chargeTime = Math.max(0.5f, chargeTime);
        this.power = Math.max(0.001f, power);
    }

    @Override
    public List<String> getLocalizedInfo() {
        int chargePercent = (int)((1.5f / chargeTime - 1.0f) * 100);
        int powerPercent = (int)((power - 1.0f) * 100);

        return ImmutableList.of(
                Util.translateFormatted("stat.heat_ray_emitter.charge_time.name", percentPart(chargePercent) + TextFormatting.RESET),
                Util.translateFormatted("stat.heat_ray_emitter.power.name", percentPart(powerPercent) + TextFormatting.RESET)
        );
    }

    private static String percentPart(int percent) {
        String color;
        String sign;
        if (percent > 0) {
            color = TextFormatting.GREEN.toString();
            sign = "+";
        } else if (percent < 0) {
            color = TextFormatting.RED.toString();
            sign = "";
        } else {
            color = TextFormatting.GRAY.toString();
            sign = "+";
        }
        return color + sign + percent + "%";
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(
                Util.translate("stat.heat_ray_emitter.charge_time.desc"),
                Util.translate("stat.heat_ray_emitter.power.desc")
        );
    }
}