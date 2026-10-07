package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitFracture extends ProjectileModifierTrait implements IBoomerangTrait {

    public TraitFracture() {
        super("fracture_boomerang", 0xAAAAAA);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public int getBoomerangPriority() {
        return 50;
    }

    @Override
    public boolean onBoomerangAfterHitEntity(EntityBoomerang boomerang, Entity target, Vec3d savedMotion) {
        if (boomerang.world.isRemote) return false;
        if (boomerang.isReturning()) return false;
        if (boomerang.hasSplit()) return false;
        if (!(target instanceof EntityLivingBase)) return false;
        boomerang.split((EntityLivingBase) target);
        boomerang.world.playSound(null, target.posX, target.posY, target.posZ,
                SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS,
                1.0F, 0.9F + boomerang.world.rand.nextFloat() * 0.2F);
        return true;
    }
}