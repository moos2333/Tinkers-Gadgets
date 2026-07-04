package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitFracture extends ProjectileModifierTrait {
    public TraitFracture() {
        super("fracture_boomerang", 0xAAAAAA);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammo, EntityLivingBase attacker, Entity target, double speed) {
        if (world.isRemote) return;
        if (!(projectile instanceof EntityBoomerang)) return;
        EntityBoomerang boomerang = (EntityBoomerang) projectile;
        if (boomerang.hasSplit()) return;
        if (!(target instanceof EntityLivingBase)) return;
        boomerang.split((EntityLivingBase) target);
        world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.BLOCK_GLASS_BREAK, net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 0.9F + world.rand.nextFloat() * 0.2F);
    }
}