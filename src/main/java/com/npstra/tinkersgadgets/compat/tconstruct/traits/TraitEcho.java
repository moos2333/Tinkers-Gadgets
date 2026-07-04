package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

import java.util.List;
import java.util.UUID;

public class TraitEcho extends ProjectileModifierTrait {
    private static final String TAG_LAST_TARGET = "echo_last_target";
    private static final String TAG_STACKS = "echo_stacks";
    private static final int MAX_STACKS = 5;
    private static final float DAMAGE_PER_STACK = 0.5f;
    private static final float SPLASH_DAMAGE_RATIO = 0.5f;
    private static final double SPLASH_RADIUS = 1.5;

    public TraitEcho() {
        super("echo_throwingknife", 0xAA00AA);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        NBTTagCompound tag = TagUtil.getTagSafe(tool);
        int stacks = tag.getInteger(TAG_STACKS);
        return newDamage + stacks * DAMAGE_PER_STACK;
    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack,
                         EntityLivingBase attacker, Entity target, double impactSpeed) {
        if (world.isRemote) return;
        if (!(attacker instanceof EntityPlayer)) return;

        ItemStack launcher = projectile.tinkerProjectile.getLaunchingStack();
        if (launcher.isEmpty()) return;

        NBTTagCompound tag = TagUtil.getTagSafe(launcher);
        int stacks = tag.getInteger(TAG_STACKS);
        String lastTargetStr = tag.getString(TAG_LAST_TARGET);
        UUID lastTargetUUID = null;
        if (!lastTargetStr.isEmpty()) {
            try {
                lastTargetUUID = UUID.fromString(lastTargetStr);
            } catch (IllegalArgumentException ignored) {}
        }

        UUID currentTargetUUID = target.getUniqueID();
        boolean sameTarget = lastTargetUUID != null && lastTargetUUID.equals(currentTargetUUID);

        if (sameTarget) {
            if (stacks > 0) stacks--;
        } else {
            if (stacks < MAX_STACKS) stacks++;
        }

        tag.setString(TAG_LAST_TARGET, currentTargetUUID.toString());
        tag.setInteger(TAG_STACKS, stacks);
        launcher.setTagCompound(tag);

        if (stacks >= MAX_STACKS) {
            float baseDamage = (float) impactSpeed * 1.5f;
            float splashDamage = baseDamage * SPLASH_DAMAGE_RATIO;
            AxisAlignedBB aabb = target.getEntityBoundingBox().grow(SPLASH_RADIUS);
            List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb,
                    e -> e != target && e != attacker && e.isEntityAlive());
            for (EntityLivingBase e : nearby) {
                e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), splashDamage);
            }
            tag.setInteger(TAG_STACKS, 0);
            launcher.setTagCompound(tag);
        }
    }
}