package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.capability.projectile.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.capability.projectile.ITinkerProjectile;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class EntityThrowingKnife extends EntityProjectileBase {

    public float spin;

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
        ITinkerProjectile cap = getCapability(CapabilityTinkerProjectile.PROJECTILE_CAPABILITY, null);
        return cap != null ? cap.getItemStack() : ItemStack.EMPTY;
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
    protected void playHitEntitySound() {
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }
}