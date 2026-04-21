package com.npstra.tinkersgadgets.compat.tconstruct.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ConnectorPartType;

public class Boomerang extends ProjectileCore {

    private static final Map<String, Set<Entity>> activeBoomerangs = new WeakHashMap<>();

    public Boomerang() {
        super(new PartMaterialType(TinkerTools.knifeBlade, MaterialTypes.HEAD),
                new PartMaterialType(GadgetsRegister.connector, ConnectorPartType.CONNECTOR),
                new PartMaterialType(TinkerTools.knifeBlade, MaterialTypes.HEAD));
        addCategory(Category.NO_MELEE, Category.PROJECTILE);
        setRegistryName("tinkersgadgets", "boomerang");
        setTranslationKey("tinkersgadgets.boomerang");
    }

    @Override
    public float damagePotential() { return 1.0f; }

    @Override
    public int[] getRepairParts() { return new int[]{0, 2}; }

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
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, net.minecraft.entity.EntityLivingBase entityLiving, int timeLeft) {
        if (ToolHelper.isBroken(stack) || !(entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entityLiving;
        int useDuration = this.getMaxItemUseDuration(stack) - timeLeft;
        float progress = Math.min(1.0F, (float) useDuration / 20.0F);
        if (progress < 0.1F) progress = 0.1F;
        if (!worldIn.isRemote) {
            String toolId = getToolId(stack);
            Set<Entity> set = activeBoomerangs.get(toolId);
            if (set != null && !set.isEmpty()) return;
            boolean usedAmmo = !player.capabilities.isCreativeMode && useAmmo(stack, player);
            EntityProjectileBase projectile = getProjectile(stack, stack, worldIn, player, 1.8F * progress, 0.5F * (1 - progress), progress, usedAmmo);
            if (projectile instanceof EntityBoomerang) {
                EntityBoomerang boomerang = (EntityBoomerang) projectile;
                boomerang.setToolId(toolId);
                addActiveBoomerang(toolId, boomerang);
                boomerang.setAssociatedPlayer(player);
            }
            worldIn.spawnEntity(projectile);
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_WITCH_THROW, net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            player.getCooldownTracker().setCooldown(stack.getItem(), 10);
        }
    }

    @Override
    public ProjectileNBT buildTagData(List<Material> materials) {
        ProjectileNBT data = new ProjectileNBT();
        data.head(materials.get(0).getStatsOrUnknown(MaterialTypes.HEAD),
                materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD));
        data.extra(materials.get(0).getStatsOrUnknown(MaterialTypes.EXTRA),
                materials.get(1).getStatsOrUnknown(MaterialTypes.EXTRA),
                materials.get(2).getStatsOrUnknown(MaterialTypes.EXTRA));
        data.accuracy = 0.95f;
        return data;
    }

    @Override
    public EntityProjectileBase getProjectile(ItemStack stack, ItemStack launcher, World world, EntityPlayer player, float speed, float inaccuracy, float power, boolean usedAmmo) {
        inaccuracy *= ProjectileNBT.from(stack).accuracy;
        return new EntityBoomerang(world, player, speed, inaccuracy, power, getProjectileStack(stack, world, player, usedAmmo), launcher);
    }

    private static String getToolId(ItemStack stack) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        NBTTagCompound stats = TagUtil.getToolTag(root);
        if (stats.hasKey("UUID")) return stats.getString("UUID");
        return stack.getItem().getRegistryName().toString() + "#" + System.identityHashCode(stack);
    }

    public static void addActiveBoomerang(String toolId, Entity entity) {
        Set<Entity> set = activeBoomerangs.computeIfAbsent(toolId, k -> new HashSet<>());
        set.add(entity);
    }

    public static void removeActiveBoomerang(String toolId, Entity entity) {
        Set<Entity> set = activeBoomerangs.get(toolId);
        if (set != null) {
            set.remove(entity);
            if (set.isEmpty()) activeBoomerangs.remove(toolId);
        }
    }
}