package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;

public class TraitInertia extends ProjectileModifierTrait {
    private static final float DAMAGE_BONUS = 0.30F;
    private static final double GRAVITY_BONUS = 0.04D;

    public TraitInertia() {
        super("inertia_throwingknife", 0x8B8B8B);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        return newDamage + damage * DAMAGE_BONUS;
    }

    @Override
    public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
        projectile.motionY -= GRAVITY_BONUS;
    }
}