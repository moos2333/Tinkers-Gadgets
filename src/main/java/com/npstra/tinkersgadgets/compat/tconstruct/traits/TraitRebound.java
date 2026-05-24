package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;

public class TraitRebound extends ProjectileModifierTrait {

    public TraitRebound() {
        super("rebound_throwingknife", 0xBFB5B5);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack,
                         EntityLivingBase attacker, Entity target, double impactSpeed) {
        if (!(projectile instanceof EntityThrowingKnife)) return;
        if (world.rand.nextFloat() < 0.5f) {
            EntityThrowingKnife knife = new EntityThrowingKnife(world,
                    (net.minecraft.entity.player.EntityPlayer) attacker,
                    0.3f, 6f, 0.5f,
                    ammoStack.copy(),
                    projectile.tinkerProjectile.getLaunchingStack());
            knife.setPosition(target.posX, target.posY + target.height * 0.5, target.posZ);
            double angle = world.rand.nextDouble() * Math.PI * 2;
            Vec3d velocity = new Vec3d(Math.cos(angle) * 0.3, 0.0, Math.sin(angle) * 0.3);
            knife.motionX = velocity.x;
            knife.motionY = velocity.y;
            knife.motionZ = velocity.z;
            knife.rotationYaw = (float) (Math.atan2(velocity.x, velocity.z) * 180.0 / Math.PI);
            knife.rotationPitch = 0f;
            knife.pickupStatus = EntityProjectileBase.PickupStatus.ALLOWED;
            knife.setPermanentlyDefused(true);
            world.spawnEntity(knife);
        }
    }
}