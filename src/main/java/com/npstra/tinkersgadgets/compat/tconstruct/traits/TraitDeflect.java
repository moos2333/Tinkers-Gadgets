package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitDeflect extends ProjectileModifierTrait {

    public TraitDeflect() {
        super("deflect_boomerang", 0x5BC7FF);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        if (projectile instanceof EntityBoomerang) {
            ((EntityBoomerang) projectile).setDeflectProjectiles(true);
        }
    }
}