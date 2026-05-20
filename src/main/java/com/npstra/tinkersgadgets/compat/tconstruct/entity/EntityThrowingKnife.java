package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class EntityThrowingKnife extends EntityProjectileBase {

    public float spin;

    public EntityThrowingKnife(World world) {
        super(world);
    }

    public EntityThrowingKnife(World world, EntityPlayer player, float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
        super(world, player, speed, inaccuracy, power, stack, launchingStack);
        setSize(0.3f, 0.1f);
        pickupStatus = PickupStatus.ALLOWED;
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

    @Override
    public void onHitEntity(RayTraceResult raytraceResult) {
        super.onHitEntity(raytraceResult);
        this.setDead();
    }

    @Override
    protected void playHitEntitySound() {
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }
}