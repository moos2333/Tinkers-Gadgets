package com.npstra.tinkersgadgets.compat.tconstruct.tools;

import com.npstra.tinkersgadgets.compat.tconstruct.parts.FuelTankPartType;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.HeatRayEmitterPartType;
import com.npstra.tinkersgadgets.compat.tconstruct.materials.FuelTankMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.materials.HeatRayEmitterMaterialStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class HeatRayGun extends TinkerToolCore {
    private static final UUID DAMAGE_BONUS_UUID = UUID.fromString("b8f6d5f0-8d5a-11e6-ae22-56b6b6499612");
    private static final int BASE_FUEL_PER_SHOT = 100;
    private static final int MAX_RANGE = 16;

    public HeatRayGun() {
        super(PartMaterialType.handle(TinkerTools.toughToolRod),
                PartMaterialType.head(TinkerTools.largePlate),
                new PartMaterialType(GadgetsRegister.fuelTank, FuelTankPartType.FUEL_TANK),
                new PartMaterialType(GadgetsRegister.heatRayEmitter, HeatRayEmitterPartType.HEAT_RAY_EMITTER));
        addCategory(Category.NO_MELEE, Category.PROJECTILE);
        setRegistryName("heat_ray_gun");
        setTranslationKey("tinkersgadgets.heat_ray_gun");
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag == null) return;
        NBTTagCompound itemTag = stack.getTagCompound();
        if (itemTag == null) itemTag = new NBTTagCompound();
        int maxFuel = toolTag.hasKey("maxFuel") ? toolTag.getInteger("maxFuel") : 10000;
        int threshold = toolTag.hasKey("heatCapacity") ? toolTag.getInteger("heatCapacity") : 10;
        float efficiency = toolTag.hasKey("fuelEfficiency") ? toolTag.getFloat("fuelEfficiency") : 1.0f;
        float power = toolTag.hasKey("powerMultiplier") ? toolTag.getFloat("powerMultiplier") : 1.0f;
        int chargeTicks = toolTag.hasKey("chargeTime") ? toolTag.getInteger("chargeTime") : 30;
        int fuel = itemTag.hasKey("Fuel") ? itemTag.getInteger("Fuel") : 0;
        int shots = itemTag.hasKey("ShotCount") ? itemTag.getInteger("ShotCount") : 0;
        if (!itemTag.hasKey("Fuel")) {
            itemTag.setInteger("Fuel", 0);
            stack.setTagCompound(itemTag);
        }
        if (!itemTag.hasKey("ShotCount")) {
            itemTag.setInteger("ShotCount", 0);
            stack.setTagCompound(itemTag);
        }
        tooltip.add(net.minecraft.util.text.TextFormatting.GOLD + "Fuel: " + fuel + "/" + maxFuel);
        tooltip.add(net.minecraft.util.text.TextFormatting.GOLD + "Heat: " + shots + "/" + threshold);
        int effPercent = (int)(efficiency * 100);
        String effColor = effPercent >= 100 ? net.minecraft.util.text.TextFormatting.GREEN.toString() : net.minecraft.util.text.TextFormatting.RED.toString();
        tooltip.add(net.minecraft.util.text.TextFormatting.GOLD + "Fuel Efficiency: " + effColor + effPercent + "%" + net.minecraft.util.text.TextFormatting.RESET);
        int powPercent = (int)(power * 100);
        String powColor = powPercent >= 100 ? net.minecraft.util.text.TextFormatting.GREEN.toString() : net.minecraft.util.text.TextFormatting.RED.toString();
        tooltip.add(net.minecraft.util.text.TextFormatting.GOLD + "Power: " + powColor + powPercent + "%" + net.minecraft.util.text.TextFormatting.RESET);
        float chargeTimeSec = chargeTicks / 20.0f;
        int chargeRatio = (int)((1.5f / chargeTimeSec) * 100);
        String chargeColor = chargeRatio >= 100 ? net.minecraft.util.text.TextFormatting.GREEN.toString() : net.minecraft.util.text.TextFormatting.RED.toString();
        tooltip.add(net.minecraft.util.text.TextFormatting.GOLD + "Charge Speed: " + chargeColor + chargeRatio + "%" + net.minecraft.util.text.TextFormatting.RESET);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) { return 72000; }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.BOW; }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (ToolHelper.isBroken(stack)) return new ActionResult<>(EnumActionResult.FAIL, stack);
        NBTTagCompound itemTag = stack.getTagCompound();
        if (itemTag == null) itemTag = new NBTTagCompound();
        if (!itemTag.hasKey("Fuel")) {
            itemTag.setInteger("Fuel", 0);
            itemTag.setInteger("ShotCount", 0);
            stack.setTagCompound(itemTag);
        }
        if (playerIn.isSneaking()) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            if (entityPlayer.isSneaking()) {
                int useTime = this.getMaxItemUseDuration(stack) - count;
                if (useTime % 20 == 0 && useTime > 0) reloadOneItem(entityPlayer.world, entityPlayer, stack);
            } else {
                int useTime = this.getMaxItemUseDuration(stack) - count;
                int chargeTicks = getChargeTicks(stack);
                if (useTime >= chargeTicks && !entityPlayer.world.isRemote) {
                    entityPlayer.stopActiveHand();
                }
                if (entityPlayer.world.isRemote && useTime >= chargeTicks) {
                    spawnClientParticles(entityPlayer.world, entityPlayer, stack);
                }
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer && !worldIn.isRemote) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            if (player.isSneaking()) return;
            int useDuration = getMaxItemUseDuration(stack) - timeLeft;
            int chargeTicks = getChargeTicks(stack);
            if (useDuration < chargeTicks) return;
            if (isOverheated(stack, player)) return;
            int fuel = getFuel(stack);
            int fuelCost = getFuelCost(stack);
            if (fuel < fuelCost) {
                boolean reloaded = tryAutoReload(worldIn, player, stack);
                if (reloaded) fuel = getFuel(stack);
            }
            if (fuel < fuelCost) {
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return;
            }
            setFuel(stack, fuel - fuelCost);
            shootRay(worldIn, player, stack);
            incrementShotCount(stack, player);
        }
    }

    private int getChargeTicks(ItemStack stack) {
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey("chargeTime")) return toolTag.getInteger("chargeTime");
        return 30;
    }

    private float getPowerMultiplier(ItemStack stack) {
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey("powerMultiplier")) return toolTag.getFloat("powerMultiplier");
        return 1.0f;
    }

    private int getMaxFuel(ItemStack stack) {
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey("maxFuel")) return toolTag.getInteger("maxFuel");
        return 10000;
    }

    private int getOverheatThreshold(ItemStack stack) {
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey("heatCapacity")) {
            int threshold = toolTag.getInteger("heatCapacity");
            return threshold > 0 ? threshold : 10;
        }
        return 10;
    }

    private float getFuelEfficiency(ItemStack stack) {
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey("fuelEfficiency")) return toolTag.getFloat("fuelEfficiency");
        return 1.0f;
    }

    private int getFuelCost(ItemStack stack) {
        float efficiency = getFuelEfficiency(stack);
        return (int) (BASE_FUEL_PER_SHOT / efficiency);
    }

    private void reloadOneItem(World world, EntityPlayer player, ItemStack stack) {
        int currentFuel = getFuel(stack);
        int maxFuel = getMaxFuel(stack);
        if (currentFuel >= maxFuel) { player.stopActiveHand(); return; }
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack invStack = player.inventory.getStackInSlot(i);
            if (invStack.isEmpty()) continue;
            int burnTime = TileEntityFurnace.getItemBurnTime(invStack);
            if (burnTime <= 0) continue;
            int fuelValue = (int) (burnTime / 20.0 * 20);
            ItemStack copy = invStack.copy();
            copy.shrink(1);
            player.inventory.setInventorySlotContents(i, copy.isEmpty() ? ItemStack.EMPTY : copy);
            int newFuel = Math.min(currentFuel + fuelValue, maxFuel);
            setFuel(stack, newFuel);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.8F, 1.2F);
            for (int p = 0; p < 8; p++) {
                world.spawnParticle(EnumParticleTypes.FLAME,
                        player.posX + (world.rand.nextDouble() - 0.5D) * 1.0D,
                        player.posY + world.rand.nextDouble() * 1.5D,
                        player.posZ + (world.rand.nextDouble() - 0.5D) * 1.0D,
                        0, 0.05D, 0);
            }
            if (newFuel >= maxFuel) player.stopActiveHand();
            return;
        }
        player.stopActiveHand();
    }

    private boolean tryAutoReload(World world, EntityPlayer player, ItemStack stack) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack invStack = player.inventory.getStackInSlot(i);
            if (invStack.isEmpty()) continue;
            int burnTime = TileEntityFurnace.getItemBurnTime(invStack);
            if (burnTime <= 0) continue;
            int fuelValue = (int) (burnTime / 20.0 * 20);
            ItemStack copy = invStack.copy();
            copy.shrink(1);
            player.inventory.setInventorySlotContents(i, copy.isEmpty() ? ItemStack.EMPTY : copy);
            int currentFuel = getFuel(stack);
            int maxFuel = getMaxFuel(stack);
            int newFuel = Math.min(currentFuel + fuelValue, maxFuel);
            setFuel(stack, newFuel);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.8F, 1.2F);
            for (int p = 0; p < 8; p++) {
                world.spawnParticle(EnumParticleTypes.FLAME,
                        player.posX + (world.rand.nextDouble() - 0.5D) * 1.0D,
                        player.posY + world.rand.nextDouble() * 1.5D,
                        player.posZ + (world.rand.nextDouble() - 0.5D) * 1.0D,
                        0, 0.05D, 0);
            }
            return true;
        }
        return false;
    }

    private void shootRay(World world, EntityPlayer player, ItemStack stack) {
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        Vec3d eyePos = player.getPositionEyes(1.0F);
        Vec3d lookVec = player.getLookVec();
        Vec3d rayEnd = eyePos.add(lookVec.scale(MAX_RANGE));
        RayTraceResult hitBlock = world.rayTraceBlocks(eyePos, rayEnd, false, true, false);
        if (hitBlock != null) rayEnd = hitBlock.hitVec;
        EntityLivingBase hitEntity = null;
        AxisAlignedBB checkBox = new AxisAlignedBB(eyePos, rayEnd).grow(0.5);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, checkBox,
                e -> e != player && e instanceof EntityLivingBase);
        if (!targets.isEmpty()) {
            targets.sort((a, b) -> Double.compare(a.getDistance(player), b.getDistance(player)));
            for (EntityLivingBase target : targets) {
                AxisAlignedBB entityBox = target.getEntityBoundingBox().grow(0.3);
                RayTraceResult entityHit = entityBox.calculateIntercept(eyePos, rayEnd);
                if (entityHit != null) {
                    hitEntity = target;
                    break;
                }
            }
        }
        float power = getPowerMultiplier(stack);
        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = data.attack;
        float finalDamage = baseDamage * 2.0f * power;
        if (hitEntity != null) {
            AttributeModifier damageMod = new AttributeModifier(DAMAGE_BONUS_UUID, "HeatRay damage bonus",
                    finalDamage - baseDamage, 0);
            player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(damageMod);
            ToolHelper.attackEntity(stack, this, player, hitEntity);
            player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(damageMod);
            hitEntity.setFire(4);
            world.playSound(null, hitEntity.posX, hitEntity.posY, hitEntity.posZ,
                    SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE, SoundCategory.PLAYERS, 0.7F, 0.9F);
        }
    }

    private void spawnClientParticles(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote) return;
        NBTTagCompound itemTag = stack.getTagCompound();
        if (itemTag != null && itemTag.hasKey("OverheatEndTick")) {
            long overheatEnd = itemTag.getLong("OverheatEndTick");
            if (world.getTotalWorldTime() < overheatEnd) {
                return;
            }
        }
        Vec3d eyePos = player.getPositionEyes(1.0F);
        Vec3d lookVec = player.getLookVec();
        Vec3d muzzlePos = eyePos.add(lookVec.scale(1.5));
        for (int i = 0; i < 6; i++) {
            world.spawnParticle(EnumParticleTypes.FLAME,
                    muzzlePos.x + (world.rand.nextDouble() - 0.5) * 0.5,
                    muzzlePos.y + (world.rand.nextDouble() - 0.5) * 0.5,
                    muzzlePos.z + (world.rand.nextDouble() - 0.5) * 0.5,
                    0, 0.02, 0);
        }
        Vec3d rayEnd = eyePos.add(lookVec.scale(MAX_RANGE));
        RayTraceResult hitBlock = world.rayTraceBlocks(eyePos, rayEnd, false, true, false);
        if (hitBlock != null) rayEnd = hitBlock.hitVec;
        double distance = eyePos.distanceTo(rayEnd);
        int steps = (int) (distance * 0.8);
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vec3d pathPos = eyePos.add(lookVec.scale(distance * t));
            world.spawnParticle(EnumParticleTypes.FLAME,
                    pathPos.x, pathPos.y, pathPos.z, 0, 0.01, 0);
            if (i % 4 == 0 && world.rand.nextInt(2) == 0) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                        pathPos.x, pathPos.y, pathPos.z, 0, 0.005, 0);
            }
        }
    }

    private boolean isOverheated(ItemStack stack, EntityPlayer player) {
        NBTTagCompound itemTag = stack.getTagCompound();
        if (itemTag == null) return false;
        long overheatEnd = itemTag.getLong("OverheatEndTick");
        long currentTime = player.world.getTotalWorldTime();
        if (currentTime < overheatEnd) return true;
        else if (itemTag.hasKey("OverheatEndTick")) {
            itemTag.removeTag("OverheatEndTick");
            setShotCount(stack, 0);
        }
        return false;
    }

    private void incrementShotCount(ItemStack stack, EntityPlayer player) {
        NBTTagCompound itemTag = stack.getTagCompound();
        if (itemTag == null) itemTag = new NBTTagCompound();
        int shots = itemTag.getInteger("ShotCount");
        shots++;
        int threshold = getOverheatThreshold(stack);
        if (shots >= threshold) {
            itemTag.setLong("OverheatEndTick", player.world.getTotalWorldTime() + 100);
            itemTag.setInteger("ShotCount", 0);
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 0.5F);
            for (int i = 0; i < 8; i++) {
                player.world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(),
                        player.posX + (player.world.rand.nextDouble() - 0.5D) * 1.0D,
                        player.posY + player.world.rand.nextDouble() * 1.5D,
                        player.posZ + (player.world.rand.nextDouble() - 0.5D) * 1.0D,
                        0, 0.1D, 0);
            }
        } else {
            itemTag.setInteger("ShotCount", shots);
        }
        stack.setTagCompound(itemTag);
    }

    private void setShotCount(ItemStack stack, int count) {
        NBTTagCompound itemTag = stack.getTagCompound();
        if (itemTag == null) itemTag = new NBTTagCompound();
        itemTag.setInteger("ShotCount", count);
        stack.setTagCompound(itemTag);
    }

    private int getFuel(ItemStack stack) {
        NBTTagCompound itemTag = stack.getTagCompound();
        return itemTag != null ? itemTag.getInteger("Fuel") : 0;
    }

    private void setFuel(ItemStack stack, int fuel) {
        NBTTagCompound itemTag = stack.getTagCompound();
        if (itemTag == null) itemTag = new NBTTagCompound();
        itemTag.setInteger("Fuel", fuel);
        stack.setTagCompound(itemTag);
    }

    @Override
    public float damagePotential() { return 1.25f; }

    @Override
    public double attackSpeed() { return 1.0d; }

    @Override
    public ToolNBT buildTagData(List<Material> materials) {
        ToolNBT data = buildDefaultTag(materials);
        NBTTagCompound tag = data.get();
        if (materials.size() > 2) {
            Material fuelMat = materials.get(2);
            FuelTankMaterialStats fuelStats = fuelMat.getStatsOrUnknown(FuelTankPartType.FUEL_TANK);
            if (fuelStats != FuelTankMaterialStats.UNKNOWN) {
                tag.setInteger("maxFuel", fuelStats.maxFuel);
                tag.setInteger("heatCapacity", fuelStats.heatCapacity);
                tag.setFloat("fuelEfficiency", fuelStats.efficiency);
            }
        }
        if (materials.size() > 3) {
            Material emitterMat = materials.get(3);
            HeatRayEmitterMaterialStats emitterStats = emitterMat.getStatsOrUnknown(HeatRayEmitterPartType.HEAT_RAY_EMITTER);
            if (emitterStats != HeatRayEmitterMaterialStats.UNKNOWN) {
                int chargeTicks = Math.max(10, (int)(emitterStats.chargeTime * 20));
                tag.setInteger("chargeTime", chargeTicks);
                tag.setFloat("powerMultiplier", emitterStats.power);
            }
        }
        if (!tag.hasKey("maxFuel")) tag.setInteger("maxFuel", 10000);
        if (!tag.hasKey("heatCapacity")) tag.setInteger("heatCapacity", 10);
        if (!tag.hasKey("fuelEfficiency")) tag.setFloat("fuelEfficiency", 1.0f);
        if (!tag.hasKey("chargeTime")) tag.setInteger("chargeTime", 30);
        if (!tag.hasKey("powerMultiplier")) tag.setFloat("powerMultiplier", 1.0f);
        return new ToolNBT(tag);
    }
}