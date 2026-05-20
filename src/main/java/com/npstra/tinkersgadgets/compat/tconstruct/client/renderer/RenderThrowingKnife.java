package com.npstra.tinkersgadgets.compat.tconstruct.client.renderer;

import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.capability.projectile.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.capability.projectile.ITinkerProjectile;

@SideOnly(Side.CLIENT)
public class RenderThrowingKnife extends Render<EntityThrowingKnife> {

    private final RenderItem itemRenderer;

    public RenderThrowingKnife(RenderManager renderManager) {
        super(renderManager);
        this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void doRender(EntityThrowingKnife entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ITinkerProjectile cap = entity.getCapability(CapabilityTinkerProjectile.PROJECTILE_CAPABILITY, null);
        ItemStack stack = cap != null ? cap.getItemStack() : ItemStack.EMPTY;
        if (stack.isEmpty()) {
            stack = entity.getArrowStack();
        }
        if (stack.isEmpty()) {
            stack = new ItemStack(net.minecraft.init.Items.STICK);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);

        if (!entity.inGround) {
            entity.spin += 20.0F * partialTicks;
        }
        GlStateManager.rotate(entity.spin, 0.0F, 0.0F, 1.0F);

        if (entity.onGround) {
            GlStateManager.translate(0.0F, 0.0F, -entity.getStuckDepth());
        }

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.NONE);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityThrowingKnife entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}