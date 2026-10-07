package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitInteract extends ProjectileModifierTrait implements IBoomerangTrait {

    public TraitInteract() {
        super("interact_boomerang", 0xCC0000);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public boolean onBoomerangHitBlock(EntityBoomerang boomerang, RayTraceResult hit) {
        if (boomerang.world.isRemote) return false;
        if (boomerang.isReturning()) return false;
        if (boomerang.hasInteracted()) return false;
        if (hit.sideHit == null) return false;
        EntityPlayer player = boomerang.getShooterPlayer();
        if (player == null) return false;
        BlockPos pos = hit.getBlockPos();
        IBlockState state = boomerang.world.getBlockState(pos);
        if (state.getBlock().isAir(state, boomerang.world, pos)) return false;
        state.getBlock().onBlockActivated(boomerang.world, pos, state, player,
                EnumHand.MAIN_HAND, hit.sideHit,
                (float) hit.hitVec.x - pos.getX(),
                (float) hit.hitVec.y - pos.getY(),
                (float) hit.hitVec.z - pos.getZ());
        boomerang.markInteracted();
        return false;
    }
}