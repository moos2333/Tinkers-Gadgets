package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitEcho extends ProjectileModifierTrait {
    private static final String TAG_LAST_TARGET_ID = "echo_last_target_id";
    private static final String TAG_STACKS = "echo_stacks";
    private static final int MAX_STACKS = 5;
    private static final float ECHO_DAMAGE_RATIO = 0.2f;

    public TraitEcho() {
        super("echo_throwingknife", 0xAA00AA);
        addAspects(ModifierAspect.projectileOnly);
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
        int lastId = tag.getInteger(TAG_LAST_TARGET_ID);
        int currentId = target.getEntityId();
        boolean sameTarget = lastId != 0 && lastId == currentId;

        if (sameTarget) {
            if (stacks > 0) stacks--;
        } else {
            if (stacks > 0 && lastId != 0) {
                Entity entity = world.getEntityByID(lastId);
                if (entity instanceof EntityLivingBase && entity.isEntityAlive()) {
                    float baseDamage = (float) impactSpeed * 1.5f;
                    float extraDamage = baseDamage * stacks * ECHO_DAMAGE_RATIO;
                    entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), extraDamage);
                }
            }
            if (stacks < MAX_STACKS) stacks++;
        }

        tag.setInteger(TAG_LAST_TARGET_ID, currentId);
        tag.setInteger(TAG_STACKS, stacks);
        launcher.setTagCompound(tag);
    }
}