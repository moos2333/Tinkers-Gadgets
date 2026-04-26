package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitPiercing extends ProjectileModifierTrait {
    public TraitPiercing() {
        super("boomerang_piercing", 0x8B0000);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        if (projectile instanceof EntityBoomerang) {
            ((EntityBoomerang) projectile).setPiercing(true);
            ((EntityBoomerang) projectile).setPierceCount(3);
        }
    }
}