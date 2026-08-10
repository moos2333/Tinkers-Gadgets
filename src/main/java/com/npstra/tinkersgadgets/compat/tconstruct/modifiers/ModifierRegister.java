package com.npstra.tinkersgadgets.compat.tconstruct.modifiers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import slimeknights.tconstruct.library.TinkerRegistry;

public class ModifierRegister {
    public static final ModCapacity CAPACITY = new ModCapacity();
    public static final ModRapid RAPID = new ModRapid();
    public static final ModPower POWER = new ModPower();
    public static final ModEfficiency EFFICIENCY = new ModEfficiency();
    public static void init(FMLInitializationEvent event) {
        TinkerRegistry.registerModifier(CAPACITY);
        TinkerRegistry.registerModifier(RAPID);
        TinkerRegistry.registerModifier(POWER);
        TinkerRegistry.registerModifier(EFFICIENCY);
    }
}