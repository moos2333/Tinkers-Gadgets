package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitBouncing extends ProjectileModifierTrait {

    private static final int MAX_BOUNCES = 3;
    private static final float DAMAGE_DECREMENT = 0.25F;
    private static final String KEY_INITIAL_DAMAGE = "bounce_init_dmg";
    private static final String KEY_BOUNCE_INDEX = "bounce_index";
    private static final String KEY_ACTIVE = "bounce_active";

    public TraitBouncing() {
        super("bouncing", 0x32CD32);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        ItemStack ammo = projectile.tinkerProjectile.getItemStack();
        NBTTagCompound root = TagUtil.getTagSafe(ammo);
        root.setFloat(KEY_INITIAL_DAMAGE, 0.0F);
        root.setInteger(KEY_BOUNCE_INDEX, 0);
        root.setBoolean(KEY_ACTIVE, true);
        ammo.setTagCompound(root);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target,
                        float damage, float newDamage, boolean isCritical) {
        NBTTagCompound root = TagUtil.getTagSafe(tool);
        if (!root.getBoolean(KEY_ACTIVE)) {
            return newDamage;
        }

        float initial = root.getFloat(KEY_INITIAL_DAMAGE);
        if (initial <= 0.0F) {
            initial = damage;
            root.setFloat(KEY_INITIAL_DAMAGE, initial);
            tool.setTagCompound(root);
        }

        int index = root.getInteger(KEY_BOUNCE_INDEX);
        float multiplier = 1.0F - DAMAGE_DECREMENT * index;
        if (multiplier < 0.0F) multiplier = 0.0F;
        return initial * multiplier;
    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammo,
                         EntityLivingBase attacker, Entity target, double speed) {
        if (!(projectile instanceof EntityBoomerang)) return;
        NBTTagCompound root = TagUtil.getTagSafe(ammo);
        if (!root.getBoolean(KEY_ACTIVE)) return;

        int index = root.getInteger(KEY_BOUNCE_INDEX);
        if (index >= MAX_BOUNCES) {
            root.setBoolean(KEY_ACTIVE, false);
            ammo.setTagCompound(root);
            return;
        }

        EntityBoomerang boomerang = (EntityBoomerang) projectile;
        EntityLivingBase next = boomerang.findNextDifferentTarget(target);

        if (next == null && index > 0 && target instanceof EntityLivingBase && target.isEntityAlive()) {
            double dist = target.getDistance(projectile.posX, projectile.posY, projectile.posZ);
            if (dist <= 9.0D) {
                next = (EntityLivingBase) target;
            }
        }

        if (next != null) {
            root.setInteger(KEY_BOUNCE_INDEX, index + 1);
            ammo.setTagCompound(root);
            boomerang.redirectToTarget(next);
            boomerang.setCancelReturn(true);
        }
    }
}