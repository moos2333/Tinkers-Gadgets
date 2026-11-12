package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.tools.modifiers.ModLuck;

public class TraitShatter extends ProjectileModifierTrait implements IBoomerangTrait {

    private static final float MAX_HARDNESS = 10.0F;
    private static final String LUCK_MODIFIER_ID = "luck";

    private ModLuck cachedLuckModifier;
    private boolean luckResolved;

    public TraitShatter() {
        super("shatter_boomerang", 0xAAAAAA);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote || !isSelected) return;
        ModLuck luck = getLuckModifier();
        if (luck == null) return;
        int targetLevel = luck.getLuckLevel(tool);
        if (targetLevel <= 0) return;
        NBTTagCompound root = TagUtil.getTagSafe(tool);
        if (ToolBuilder.getEnchantmentLevel(root, Enchantments.FORTUNE) >= targetLevel) return;
        while (targetLevel > ToolBuilder.getEnchantmentLevel(root, Enchantments.FORTUNE)) {
            ToolBuilder.addEnchantment(root, Enchantments.FORTUNE);
        }
    }

    private ModLuck getLuckModifier() {
        if (!luckResolved) {
            IModifier modifier = TinkerRegistry.getModifier(LUCK_MODIFIER_ID);
            if (modifier instanceof ModLuck) {
                cachedLuckModifier = (ModLuck) modifier;
                luckResolved = true;
            }
        }
        return cachedLuckModifier;
    }

    @Override
    public boolean onBoomerangHitBlock(EntityBoomerang boomerang, RayTraceResult hit) {
        World world = boomerang.world;
        if (world.isRemote) return false;
        if (boomerang.isReturning()) return false;
        if (hit.sideHit == null) return false;

        BlockPos pos = hit.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = boomerang.tinkerProjectile.getItemStack();
        if (!isBreakable(world, pos, state, stack)) return false;

        EntityPlayer player = boomerang.shootingEntity instanceof EntityPlayer
                ? (EntityPlayer) boomerang.shootingEntity : null;
        if (player == null || stack.isEmpty()) {
            world.destroyBlock(pos, true);
            return false;
        }

        state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
        world.destroyBlock(pos, false);
        return false;
    }

    private boolean isBreakable(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F || hardness > MAX_HARDNESS) return false;
        int requiredLevel = state.getBlock().getHarvestLevel(state);
        if (requiredLevel < 0) return true;
        if (stack.isEmpty()) return false;
        return requiredLevel <= ProjectileNBT.from(stack).harvestLevel;
    }
}