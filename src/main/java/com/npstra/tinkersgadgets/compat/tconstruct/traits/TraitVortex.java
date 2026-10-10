package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import slimeknights.tconstruct.library.traits.AbstractTrait;

import java.util.List;

public class TraitVortex extends AbstractTrait {

    private static final double RADIUS = 2.0D;
    private static final double PULL_STRENGTH = 0.4D;
    private static final double MIN_DISTANCE = 0.1D;

    public TraitVortex() {
        super("vortex_throwingknife", 0x5FCDCD);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                         float damageDealt, boolean wasCritical, boolean wasHit) {
        if (!wasHit) return;
        if (player.world.isRemote) return;
        AxisAlignedBB box = target.getEntityBoundingBox().grow(RADIUS);
        List<EntityLivingBase> nearby = player.world.getEntitiesWithinAABB(EntityLivingBase.class, box,
                e -> e != target && !(e instanceof EntityPlayer));
        for (EntityLivingBase entity : nearby) {
            double dx = target.posX - entity.posX;
            double dz = target.posZ - entity.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < MIN_DISTANCE) continue;
            entity.motionX += dx / dist * PULL_STRENGTH;
            entity.motionZ += dz / dist * PULL_STRENGTH;
            entity.velocityChanged = true;
        }
    }
}