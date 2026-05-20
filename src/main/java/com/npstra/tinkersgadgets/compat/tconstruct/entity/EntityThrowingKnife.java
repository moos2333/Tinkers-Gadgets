package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class EntityThrowingKnife extends EntityProjectileBase {

    public EntityThrowingKnife(World world) {
        super(world);
    }

    public EntityThrowingKnife(World world, EntityPlayer player, float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
        super(world, player, speed, inaccuracy, power, stack, launchingStack);
        setSize(0.3f, 0.1f);
        pickupStatus = PickupStatus.DISALLOWED;
    }

    @Override
    protected void init() {
        bounceOnNoDamage = false;
    }

    @Override
    public ItemStack getArrowStack() {
        return tinkerProjectile.getItemStack();
    }

    @Override
    public double getGravity() {
        return 0.065D;
    }

    @Override
    public double getSlowdown() {
        return 0.01D;
    }
}