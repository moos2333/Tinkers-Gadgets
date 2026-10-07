package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;

public class TraitShatter extends ProjectileModifierTrait implements IBoomerangTrait {

    private static final float MAX_HARDNESS = 10.0F;

    public TraitShatter() {
        super("shatter_boomerang", 0xAAAAAA);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public boolean onBoomerangHitBlock(EntityBoomerang boomerang, RayTraceResult hit) {
        if (boomerang.world.isRemote) return false;
        if (boomerang.isReturning()) return false;
        if (hit.sideHit == null) return false;
        BlockPos pos = hit.getBlockPos();
        IBlockState state = boomerang.world.getBlockState(pos);
        if (!isBreakable(boomerang, pos, state)) return false;
        boomerang.world.destroyBlock(pos, true);
        return false;
    }

    private boolean isBreakable(EntityBoomerang boomerang, BlockPos pos, IBlockState state) {
        float hardness = state.getBlockHardness(boomerang.world, pos);
        if (hardness < 0.0F || hardness > MAX_HARDNESS) return false;
        int requiredLevel = state.getBlock().getHarvestLevel(state);
        if (requiredLevel < 0) return true;
        ItemStack stack = boomerang.tinkerProjectile.getItemStack();
        if (stack.isEmpty()) return false;
        return requiredLevel <= ProjectileNBT.from(stack).harvestLevel;
    }
}