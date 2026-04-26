package com.npstra.tinkersgadgets;

import com.npstra.tinkersgadgets.compat.tconstruct.common.CommonProxy;
import com.npstra.tinkersgadgets.compat.tconstruct.materials.MaterialRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION,
        dependencies = "required-after:tconstruct@[1.12.2-2.13.0.183,)")
public class TinkersGadgets {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;

    @Mod.Instance(Tags.MOD_ID)
    public static TinkersGadgets instance;

    @SidedProxy(clientSide = "com.npstra.tinkersgadgets.compat.tconstruct.client.ClientProxy",
            serverSide = "com.npstra.tinkersgadgets.compat.tconstruct.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.registerRenderers();
        MaterialRegister.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.initToolGuis();
        MaterialRegister.init(event);
    }
}