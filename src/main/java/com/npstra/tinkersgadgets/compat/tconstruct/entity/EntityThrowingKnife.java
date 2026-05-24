package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class EntityThrowingKnife extends EntityProjectileBase {

    public float spin;
    private boolean permanentlyDefused;

    public EntityThrowingKnife(World world) {
        super(world);
    }

    public EntityThrowingKnife(World world, EntityPlayer player, float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
        super(world, player, speed, inaccuracy, power, stack, launchingStack);
        setSize(0.3f, 0.1f);
        pickupStatus = PickupStatus.ALLOWED;
    }

    public void setPermanentlyDefused(boolean value) {
        permanentlyDefused = value;
        defused = value;
    }

    @Override
    protected void init() {
        bounceOnNoDamage = false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (permanentlyDefused && !defused) {
            defused = true;
        }
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

    @Override
    public void writeSpawnData(ByteBuf data) {
        super.writeSpawnData(data);
        data.writeBoolean(permanentlyDefused);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        boolean pd = data.readBoolean();
        setPermanentlyDefused(pd);
    }
}