package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;

public class TraitCollection extends ProjectileModifierTrait {
    private static final int MAX_PASSENGERS = 20;

    public TraitCollection() {
        super("collection_boomerang", 0x8B6B4D);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
        if (world.isRemote) return;
        if (projectile.getPassengers().size() >= MAX_PASSENGERS) return;
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(projectile,
                projectile.getEntityBoundingBox().grow(1.5D));
        for (Entity entity : entities) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) continue;
            if (projectile.getPassengers().size() >= MAX_PASSENGERS) break;
            if (entity.isDead || entity.isRiding()) continue;
            entity.startRiding(projectile, true);
        }
    }
}