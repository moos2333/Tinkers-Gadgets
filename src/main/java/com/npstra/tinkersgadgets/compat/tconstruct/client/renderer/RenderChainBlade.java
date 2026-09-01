package com.npstra.tinkersgadgets.compat.tconstruct.client.renderer;

import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityChainBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderChainBlade extends Render<EntityChainBlade> {
    private final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

    public RenderChainBlade(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(EntityChainBlade entity, double x, double y, double z, float entityYaw, float partialTicks) {
        renderChainLine(entity, partialTicks);
        renderTool(entity, x, y, z, partialTicks);
    }

    private void renderChainLine(EntityChainBlade entity, float partialTicks) {
        if (entity.shootingEntity == null) return;
        double renderX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double renderY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double renderZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        double playerX = entity.shootingEntity.lastTickPosX + (entity.shootingEntity.posX - entity.shootingEntity.lastTickPosX) * partialTicks;
        double playerY = entity.shootingEntity.lastTickPosY + (entity.shootingEntity.posY - entity.shootingEntity.lastTickPosY) * partialTicks + entity.shootingEntity.getEyeHeight() * 0.7;
        double playerZ = entity.shootingEntity.lastTickPosZ + (entity.shootingEntity.posZ - entity.shootingEntity.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.glLineWidth(2.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        int segments = 16;
        double offX = this.renderManager.viewerPosX;
        double offY = this.renderManager.viewerPosY;
        double offZ = this.renderManager.viewerPosZ;

        for (int i = 0; i <= segments; i++) {
            float t = i / (float) segments;
            double lx = playerX + (renderX - playerX) * t;
            double ly = playerY + (renderY - playerY) * t + Math.sin(t * Math.PI * 2) * 0.1;
            double lz = playerZ + (renderZ - playerZ) * t;
            float alpha = entity.isReturning() ? 0.6F : 1.0F;
            buffer.pos(lx - offX, ly - offY, lz - offZ).color(0.6F, 0.6F, 0.6F, alpha).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderTool(EntityChainBlade entity, double x, double y, double z, float partialTicks) {
        ItemStack stack = entity.getArrowStack();
        if (stack.isEmpty()) {
            stack = new ItemStack(net.minecraft.init.Items.STICK);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.6F, 0.6F, 0.6F);

        double dx = entity.motionX;
        double dy = entity.motionY;
        double dz = entity.motionZ;
        if (dx == 0 && dy == 0 && dz == 0) {
            dx = 1;
        }

        float yaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dy, MathHelper.sqrt(dx * dx + dz * dz)) * (180D / Math.PI)));

        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.NONE);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityChainBlade entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}