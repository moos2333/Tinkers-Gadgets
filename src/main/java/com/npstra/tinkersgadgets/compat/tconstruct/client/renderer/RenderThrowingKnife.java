package com.npstra.tinkersgadgets.compat.tconstruct.client.renderer;

import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import slimeknights.tconstruct.library.client.renderer.RenderProjectileBase;

public class RenderThrowingKnife extends RenderProjectileBase<EntityThrowingKnife> {

    public RenderThrowingKnife(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void customRendering(EntityThrowingKnife entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        GlStateManager.rotate(entity.rotationYaw, 0f, 1f, 0f);
        GlStateManager.rotate(-entity.rotationPitch, 1f, 0f, 0f);
        GlStateManager.rotate(90f, 1f, 0f, 0f);
        if (!entity.inGround) {
            entity.spin += 20 * partialTicks;
        }
        float r = entity.spin;
        GlStateManager.rotate(r, 0f, 0f, 1f);
    }
}