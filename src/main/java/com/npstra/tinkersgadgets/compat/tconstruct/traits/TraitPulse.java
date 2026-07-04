package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;

public class TraitPulse extends ProjectileModifierTrait {

    public TraitPulse() {
        super("pulse_throwingknife", 0xCC0000);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack,
                         EntityLivingBase attacker, Entity target, double impactSpeed) {
        if (target instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) target;
            living.hurtResistantTime = living.hurtResistantTime / 2;
        }
    }
}