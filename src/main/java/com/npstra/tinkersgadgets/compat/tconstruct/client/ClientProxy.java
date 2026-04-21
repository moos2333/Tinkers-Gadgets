package com.npstra.tinkersgadgets.compat.tconstruct.client;

import com.npstra.tinkersgadgets.compat.tconstruct.client.renderer.RenderBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.client.renderer.RenderBoomerangShard;
import com.npstra.tinkersgadgets.compat.tconstruct.common.CommonProxy;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerangShard;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.GadgetsRegister;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;

public class ClientProxy extends CommonProxy {

    @Override
    public void initToolGuis() {
        if (GadgetsRegister.boomerang != null) {
            ToolBuildGuiInfo info = new ToolBuildGuiInfo(GadgetsRegister.boomerang);
            info.addSlotPosition(33 + 12, 42 - 12);
            info.addSlotPosition(33 - 12, 42 - 12);
            info.addSlotPosition(33 - 12, 42 + 12);
            TinkerRegistryClient.addToolBuilding(info);
        }
    }

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityBoomerang.class, RenderBoomerang::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBoomerangShard.class, RenderBoomerangShard::new);
    }
}