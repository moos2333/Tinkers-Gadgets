package com.npstra.tinkersgadgets;

import com.npstra.tinkersgadgets.compat.tconstruct.common.CommonProxy;
import com.npstra.tinkersgadgets.compat.tconstruct.materials.MaterialRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TinkersGadgets.MOD_ID, name = TinkersGadgets.MOD_NAME, version = TinkersGadgets.VERSION,
        dependencies = "required-after:tconstruct@[1.12.2-2.13.0.183,)")
public class TinkersGadgets {
    public static final String MOD_ID = "tinkersgadgets";
    public static final String MOD_NAME = "Tinkers Gadgets";
    public static final String VERSION = "0.0.1";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Mod.Instance(MOD_ID)
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