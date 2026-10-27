package com.npstra.tinkersgadgets.compat.tconstruct.client;

import com.npstra.tinkersgadgets.compat.tconstruct.client.renderer.RenderBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.client.renderer.RenderBoomerangShard;
import com.npstra.tinkersgadgets.compat.tconstruct.client.renderer.RenderChainBlade;
import com.npstra.tinkersgadgets.compat.tconstruct.client.renderer.RenderThrowingKnife;
import com.npstra.tinkersgadgets.compat.tconstruct.common.CommonProxy;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerangShard;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityChainBlade;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.GadgetsRegister;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.tools.ToolCore;

public class ClientProxy extends CommonProxy {

    @Override
    public void initToolGuis() {
        registerGui(GadgetsRegister.boomerang,
                45, 30, 21, 30, 21, 54);
        registerGui(GadgetsRegister.throwingKnife,
                51, 24, 33, 42, 15, 60);
        registerGui(GadgetsRegister.heatRayGun,
                51, 60, 51, 42, 33, 42, 15, 24);
        registerGui(GadgetsRegister.chainBlade,
                33, 60, 33, 24, 33, 42);
        registerGui(GadgetsRegister.pistolSword,
                18, 57, 48, 27, 48, 57);
    }

    private static void registerGui(ToolCore tool, int... slotCoords) {
        if (tool == null) return;
        ToolBuildGuiInfo info = new ToolBuildGuiInfo(tool);
        for (int i = 0; i < slotCoords.length; i += 2) {
            info.addSlotPosition(slotCoords[i], slotCoords[i + 1]);
        }
        TinkerRegistryClient.addToolBuilding(info);
    }

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityBoomerang.class, RenderBoomerang::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBoomerangShard.class, RenderBoomerangShard::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityThrowingKnife.class, RenderThrowingKnife::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityChainBlade.class, RenderChainBlade::new);
    }
}