package com.npstra.tinkersgadgets.compat.tconstruct.tools;

import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ConnectorPartType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import java.util.List;

public class ThrowingKnife extends ProjectileCore {

    private static final int NORMAL_CHARGE = 30;
    private static final int SNEAK_CHARGE = 60;

    public ThrowingKnife() {
        super(new PartMaterialType(TinkerTools.knifeBlade, MaterialTypes.HEAD),
                new PartMaterialType(TinkerTools.arrowShaft, MaterialTypes.SHAFT),
                new PartMaterialType(GadgetsRegister.connector, ConnectorPartType.CONNECTOR));
        addCategory(Category.NO_MELEE, Category.PROJECTILE);
        setRegistryName("throwing_knife");
        setTranslationKey("tinkersgadgets.throwing_knife");
    }

    @Override
    public float damagePotential() { return 0.66f; }

    @Override
    public int[] getRepairParts() { return new int[]{0, 1}; }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.BOW; }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) { return 72000; }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (ToolHelper.isBroken(itemStackIn)) return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
        playerIn.setActiveHand(hand);
        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            int chargeTime = this.getMaxItemUseDuration(stack) - count;
            int required = entityPlayer.isSneaking() ? SNEAK_CHARGE : NORMAL_CHARGE;
            if (chargeTime >= required && !entityPlayer.world.isRemote) {
                entityPlayer.stopActiveHand();
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (ToolHelper.isBroken(stack) || !(entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entityLiving;
        int useDuration = this.getMaxItemUseDuration(stack) - timeLeft;
        boolean sneaking = player.isSneaking();
        int required = sneaking ? SNEAK_CHARGE : NORMAL_CHARGE;
        if (useDuration < required) return;

        float progress = Math.min(1.0F, (float) useDuration / (float) required);
        float speed = 1.4F * progress;
        float inaccuracy = 0.5F * (1.0F - progress);
        float power = progress;

        if (!worldIn.isRemote) {
            EntityProjectileBase knife = getProjectile(stack, stack, worldIn, player, speed, inaccuracy, power, true);
            worldIn.spawnEntity(knife);
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

            if (sneaking) {
                EntityProjectileBase left = getProjectile(stack, stack, worldIn, player, speed, inaccuracy + 2.0F, power, false);
                left.rotationYaw = player.rotationYaw - 30.0F;
                worldIn.spawnEntity(left);

                EntityProjectileBase right = getProjectile(stack, stack, worldIn, player, speed, inaccuracy + 2.0F, power, false);
                right.rotationYaw = player.rotationYaw + 30.0F;
                worldIn.spawnEntity(right);
            }
        }
    }

    @Override
    public ProjectileNBT buildTagData(List<Material> materials) {
        ProjectileNBT data = new ProjectileNBT();
        data.head(materials.get(0).getStatsOrUnknown(MaterialTypes.HEAD));
        data.extra(materials.get(0).getStatsOrUnknown(MaterialTypes.EXTRA),
                materials.get(1).getStatsOrUnknown(MaterialTypes.EXTRA));
        ArrowShaftMaterialStats shaftStats = materials.get(1).getStatsOrUnknown(MaterialTypes.SHAFT);
        data.shafts(this, shaftStats);
        data.durability = 64;
        return data;
    }

    @Override
    public EntityProjectileBase getProjectile(ItemStack stack, ItemStack launcher, World world, EntityPlayer player,
                                              float speed, float inaccuracy, float power, boolean usedAmmo) {
        return new EntityThrowingKnife(world, player, speed, inaccuracy, power,
                getProjectileStack(stack, world, player, usedAmmo), launcher);
    }
}