package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.traits.AbstractProjectileTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitKinetic extends AbstractProjectileTrait {
    private static final int MAX_STACKS = 10;
    private static final float BONUS_PER_STACK = 0.10F;
    private static final int DECAY_INTERVAL = 60;
    private static final String KEY_STACKS = "kinetic_stacks";
    private static final String KEY_LAST_HIT = "kinetic_last_hit";

    public TraitKinetic() {
        super("kinetic_boomerang", 0xF5E66C);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
        if (world.isRemote || shooter == null) return;
        ItemStack weapon = findWeapon(shooter);
        if (weapon.isEmpty()) return;
        int stacks = getStacks(weapon, world.getTotalWorldTime());
        if (stacks <= 0) return;
        float mult = 1.0F + BONUS_PER_STACK * stacks;
        projectile.motionX *= mult;
        projectile.motionY *= mult;
        projectile.motionZ *= mult;
    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {
        if (world.isRemote || attacker == null) return;
        ItemStack weapon = findWeapon(attacker);
        if (weapon.isEmpty()) return;
        long now = world.getTotalWorldTime();
        int stacks = getStacks(weapon, now);
        setStacks(weapon, Math.min(MAX_STACKS, stacks + 1), now);
    }

    private int getStacks(ItemStack weapon, long now) {
        NBTTagCompound tag = TagUtil.getToolTag(weapon);
        int stacks = tag.getInteger(KEY_STACKS);
        long lastHit = tag.getLong(KEY_LAST_HIT);
        if (stacks <= 0 || lastHit <= 0) return stacks;
        long elapsed = now - lastHit;
        int decayed = (int) (elapsed / DECAY_INTERVAL);
        if (decayed <= 0) return stacks;
        stacks = Math.max(0, stacks - decayed);
        tag.setInteger(KEY_STACKS, stacks);
        tag.setLong(KEY_LAST_HIT, lastHit + (long) decayed * DECAY_INTERVAL);
        TagUtil.setToolTag(weapon, tag);
        return stacks;
    }

    private void setStacks(ItemStack weapon, int stacks, long now) {
        NBTTagCompound tag = TagUtil.getToolTag(weapon);
        tag.setInteger(KEY_STACKS, stacks);
        tag.setLong(KEY_LAST_HIT, now);
        TagUtil.setToolTag(weapon, tag);
    }

    private ItemStack findWeapon(EntityLivingBase entity) {
        ItemStack main = entity.getHeldItemMainhand();
        if (!main.isEmpty() && TinkerUtil.hasTrait(TagUtil.getTagSafe(main), getModifierIdentifier())) {
            return main;
        }
        ItemStack off = entity.getHeldItemOffhand();
        if (!off.isEmpty() && TinkerUtil.hasTrait(TagUtil.getTagSafe(off), getModifierIdentifier())) {
            return off;
        }
        return ItemStack.EMPTY;
    }
}