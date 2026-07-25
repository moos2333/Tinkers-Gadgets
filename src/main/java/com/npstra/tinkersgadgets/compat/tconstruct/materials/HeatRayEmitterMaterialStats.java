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
        String chargeColor;
        String chargeSign;
        if (chargePercent > 0) {
            chargeColor = TextFormatting.GREEN.toString();
            chargeSign = "+";
        } else if (chargePercent < 0) {
            chargeColor = TextFormatting.RED.toString();
            chargeSign = "";
        } else {
            chargeColor = TextFormatting.GRAY.toString();
            chargeSign = "+";
        }

        int powerPercent = (int)((power - 1.0f) * 100);
        String powerColor;
        String powerSign;
        if (powerPercent > 0) {
            powerColor = TextFormatting.GREEN.toString();
            powerSign = "+";
        } else if (powerPercent < 0) {
            powerColor = TextFormatting.RED.toString();
            powerSign = "";
        } else {
            powerColor = TextFormatting.GRAY.toString();
            powerSign = "+";
        }

        return ImmutableList.of(
                TextFormatting.GRAY + Util.translateFormatted("stat.heat_ray_emitter.charge_time.name", chargeColor + chargeSign + chargePercent + "%" + TextFormatting.RESET),
                TextFormatting.GRAY + Util.translateFormatted("stat.heat_ray_emitter.power.name", powerColor + powerSign + powerPercent + "%" + TextFormatting.RESET)
        );
    }

    @Override
    public List<String> getLocalizedDesc() {
        return ImmutableList.of(
                Util.translate("stat.heat_ray_emitter.charge_time.desc"),
                Util.translate("stat.heat_ray_emitter.power.desc")
        );
    }
}