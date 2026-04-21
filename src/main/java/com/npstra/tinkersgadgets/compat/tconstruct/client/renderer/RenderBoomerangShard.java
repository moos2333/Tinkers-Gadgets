package com.npstra.tinkersgadgets.compat.tconstruct.client.renderer;

import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerangShard;
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

@SideOnly(Side.CLIENT)
public class RenderBoomerangShard extends Render<EntityBoomerangShard> {
    private final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

    public RenderBoomerangShard(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityBoomerangShard entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = entity.getArrowStack();
        if (stack == null || stack.isEmpty()) {
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
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entity.ticksExisted + partialTicks) * 30.0F, 0.0F, 0.0F, 1.0F);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.NONE);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBoomerangShard entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}