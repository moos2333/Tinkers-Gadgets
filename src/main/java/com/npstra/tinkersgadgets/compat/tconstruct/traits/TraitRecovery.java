package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;

public class TraitRecovery extends ProjectileModifierTrait {

    public TraitRecovery() {
        super("recovery_throwingknife", 0x976997);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        if (projectile instanceof EntityThrowingKnife) {
            ((EntityThrowingKnife) projectile).setRecovery(true);
        }
    }

    public static void handleBlockHit(EntityThrowingKnife knife) {
        if (!knife.world.isRemote && knife.shootingEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) knife.shootingEntity;
            if (player != null && knife.tinkerProjectile != null) {
                knife.tinkerProjectile.pickup(player, false);
                knife.setDead();
            }
        }
    }
}