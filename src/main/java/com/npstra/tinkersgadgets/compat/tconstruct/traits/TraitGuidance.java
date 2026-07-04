package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;

import java.util.List;

public class TraitGuidance extends ProjectileModifierTrait {

    private static final double MAX_RANGE = 12.0;
    private static final double HOMING_STRENGTH = 0.03;

    public TraitGuidance() {
        super("guidance_throwingknife", 0x0A6E6E);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
        if (world.isRemote) return;
        if (!(projectile instanceof EntityThrowingKnife)) return;
        if (projectile.ticksExisted < 5) return;

        EntityLivingBase shooter = projectile.shootingEntity;
        if (!(shooter instanceof EntityPlayer)) return;

        AxisAlignedBB aabb = projectile.getEntityBoundingBox().grow(MAX_RANGE);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb,
                e -> e != shooter && e != projectile && e.isEntityAlive() && !e.isSpectator());

        if (targets.isEmpty()) return;

        EntityLivingBase target = targets.get(0);
        double minDist = Double.MAX_VALUE;
        for (EntityLivingBase e : targets) {
            double dist = projectile.getDistanceSq(e);
            if (dist < minDist) {
                minDist = dist;
                target = e;
            }
        }

        Vec3d toTarget = target.getPositionEyes(1.0F).subtract(projectile.getPositionVector());
        double dist = toTarget.length();
        if (dist < 0.5) return;

        Vec3d currentMotion = new Vec3d(projectile.motionX, projectile.motionY, projectile.motionZ);
        Vec3d desired = toTarget.normalize().scale(currentMotion.length());
        Vec3d newMotion = currentMotion.add(desired.subtract(currentMotion).scale(HOMING_STRENGTH));
        projectile.motionX = newMotion.x;
        projectile.motionY = newMotion.y;
        projectile.motionZ = newMotion.z;

        float yaw = (float) (Math.atan2(newMotion.x, newMotion.z) * 180.0 / Math.PI);
        float pitch = (float) (-Math.atan2(newMotion.y, Math.sqrt(newMotion.x * newMotion.x + newMotion.z * newMotion.z)) * 180.0 / Math.PI);
        projectile.rotationYaw = yaw;
        projectile.rotationPitch = pitch;
    }
}