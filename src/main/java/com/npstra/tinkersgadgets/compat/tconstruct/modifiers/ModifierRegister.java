package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import com.npstra.tinkersgadgets.Config;

public class ModifierRegister {
    private static ModCapacity CAPACITY;
    private static ModRapid RAPID;
    private static ModPower POWER;
    private static ModEfficiency EFFICIENCY;

    public static void init(FMLInitializationEvent event) {
        if (Config.enableCapacity) {
            CAPACITY = new ModCapacity();
        }
        if (Config.enableRapid) {
            RAPID = new ModRapid();
        }
        if (Config.enablePower) {
            POWER = new ModPower();
        }
        if (Config.enableEfficiency) {
            EFFICIENCY = new ModEfficiency();
        }
    }
}