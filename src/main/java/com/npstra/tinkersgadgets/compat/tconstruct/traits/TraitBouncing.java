package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TraitBouncing extends ProjectileModifierTrait {

    private static final Map<UUID, Float> damageMultMap = new HashMap<>();

    public TraitBouncing() {
        super("bouncing", 0x32CD32);
        addAspects(ModifierAspect.projectileOnly);
    }

    public static void setDamageMult(UUID targetId, float mult) {
        damageMultMap.put(targetId, mult);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        if (projectile instanceof EntityBoomerang) {
            EntityBoomerang boomerang = (EntityBoomerang) projectile;
            boomerang.setBouncing(true);
            boomerang.setBounceCount(3);
        }
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        Float mult = damageMultMap.remove(target.getUniqueID());
        if (mult != null) {
            return newDamage * mult;
        }
        return newDamage;
    }
}