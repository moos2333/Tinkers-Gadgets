package com.npstra.tinkersgadgets.compat.tconstruct.tools;

import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityChainBlade;
import com.npstra.tinkersgadgets.compat.tconstruct.materials.ChainMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ChainPartType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class ChainBlade extends ProjectileCore {
    private static final Map<String, Set<Entity>> activeChainBlades = new WeakHashMap<>();
    private static final int CHARGE_COST = 10;
    private static final int SWEEP_COOLDOWN = 40;
    private static final float SWEEP_BASE_RADIUS = 3.5f;
    private static final float SWEEP_BASE_ANGLE = 60.0f;
    private static final float BASE_DAMAGE_RATIO = 0.4f;
    private static final int THROW_CHARGE = 20;
    private static final float CHARGE_MULTIPLIER = 2.0f;
    private static final int MAX_CHARGE = 30;

    public ChainBlade() {
        super(PartMaterialType.handle(TinkerTools.toolRod),
                PartMaterialType.head(TinkerTools.arrowHead),
                new PartMaterialType(GadgetsRegister.chain, ChainPartType.CHAIN));
        addCategory(Category.WEAPON);
        addCategory(Category.PROJECTILE);
        addCategory(Category.NO_MELEE);
        setRegistryName("chain_blade");
        setTranslationKey("tinkersgadgets.chain_blade");
    }

    @Override
    public float damagePotential() {
        return 0.5f;
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{1};
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return true;
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (ToolHelper.isBroken(stack)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (player.isSneaking()) {
            if (isOnCooldown(stack)) {
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
            performSweep(world, player, stack);
            if (!world.isRemote) {
                setCooldown(stack);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            if (!world.isRemote) {
                String toolId = getToolId(stack);
                Set<Entity> set = activeChainBlades.get(toolId);
                if (set != null) {
                    set.removeIf(e -> !e.isEntityAlive());
                    if (!set.isEmpty()) {
                        return new ActionResult<>(EnumActionResult.FAIL, stack);
                    }
                }
            }
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (ToolHelper.isBroken(stack) || !(entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entityLiving;
        if (player.isSneaking()) return;
        int useDuration = getMaxItemUseDuration(stack) - timeLeft;
        if (useDuration < THROW_CHARGE) return;
        if (!world.isRemote) {
            String toolId = getToolId(stack);
            Set<Entity> set = activeChainBlades.get(toolId);
            if (set != null) {
                set.removeIf(e -> !e.isEntityAlive());
                if (!set.isEmpty()) {
                    return;
                }
            }
            boolean ammoDepleted = this.getCurrentAmmo(stack) < 1;
            boolean usedAmmo = !player.capabilities.isCreativeMode && !ammoDepleted && useAmmo(stack, player);
            float progress = Math.min(1.0F, (float) useDuration / (float) THROW_CHARGE);
            float speed = 1.6F * progress;
            float inaccuracy = 0.3F * (1.0F - progress);
            shootChainBlade(world, player, stack, speed, inaccuracy, progress, toolId, usedAmmo);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 0.6F, 1.2F);
            if (ammoDepleted) {
                ToolHelper.breakTool(stack, player);
            }
        }
    }

    private void shootChainBlade(World world, EntityPlayer player, ItemStack stack, float speed, float inaccuracy, float power, String toolId, boolean usedAmmo) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        if (!tag.hasKey("chain_uuid")) {
            tag.setString("chain_uuid", UUID.randomUUID().toString());
        }

        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = (float) data.attack * BASE_DAMAGE_RATIO;
        int maxBounces = getMaxBounces(stack);
        float bounceRange = getBounceRange(stack);

        EntityChainBlade entity = new EntityChainBlade(
                world, player, stack, speed, baseDamage,
                maxBounces, bounceRange
        );
        entity.setToolId(toolId);
        entity.setPosition(player.posX, player.posY + player.getEyeHeight() - 0.1, player.posZ);
        Vec3d look = player.getLookVec();
        entity.motionX = look.x * speed;
        entity.motionY = look.y * speed + 0.05D;
        entity.motionZ = look.z * speed;

        double dx = entity.motionX;
        double dy = entity.motionY;
        double dz = entity.motionZ;
        if (dx == 0 && dy == 0 && dz == 0) {
            dx = 1;
        }
        entity.rotationYaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
        entity.rotationPitch = (float) (-(MathHelper.atan2(dy, MathHelper.sqrt(dx * dx + dz * dz)) * (180D / Math.PI)));
        entity.prevRotationYaw = entity.rotationYaw;
        entity.prevRotationPitch = entity.rotationPitch;

        world.spawnEntity(entity);
        addActiveChainBlade(toolId, entity);
    }

    private void performSweep(World world, EntityPlayer player, ItemStack stack) {
        if (world.isRemote) return;

        if (getCurrentAmmo(stack) < 1) return;
        useAmmo(stack, player);

        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = (float) data.attack * BASE_DAMAGE_RATIO;

        boolean hasCharge = consumeCharge(stack, CHARGE_COST);
        float chargeMult = hasCharge ? CHARGE_MULTIPLIER : 1.0f;

        float radius = hasCharge ? 4.0f : SWEEP_BASE_RADIUS;
        float angle = hasCharge ? 120.0f : SWEEP_BASE_ANGLE;

        Vec3d origin = new Vec3d(player.posX, player.posY + player.getEyeHeight() * 0.5, player.posZ);
        Vec3d look = player.getLookVec();

        player.spawnSweepParticles();

        AxisAlignedBB box = new AxisAlignedBB(
                origin.x - radius, origin.y - radius, origin.z - radius,
                origin.x + radius, origin.y + radius, origin.z + radius
        );
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, box,
                e -> e != player && e.isEntityAlive());

        targets.removeIf(e -> {
            Vec3d toTarget = new Vec3d(e.posX - origin.x, e.posY + e.getEyeHeight() * 0.5 - origin.y, e.posZ - origin.z);
            if (toTarget.length() > radius) return true;
            Vec3d toTarget2D = new Vec3d(toTarget.x, 0, toTarget.z).normalize();
            Vec3d look2D = new Vec3d(look.x, 0, look.z).normalize();
            double cosAngle = look2D.dotProduct(toTarget2D);
            return Math.acos(MathHelper.clamp(cosAngle, -1.0, 1.0)) > Math.toRadians(angle * 0.5);
        });

        int count = targets.size();

        if (!targets.isEmpty()) {
            EntityLivingBase primary = null;
            double maxDist = -1;
            for (EntityLivingBase e : targets) {
                double d = e.getDistance(player);
                if (d > maxDist) {
                    maxDist = d;
                    primary = e;
                }
            }
            if (primary != null) {
                float primaryDamage = (float) (baseDamage * (1.5 + count * 0.1)) * chargeMult;
                primary.attackEntityFrom(DamageSource.causePlayerDamage(player), primaryDamage);
            }
            for (EntityLivingBase e : targets) {
                if (e == primary) continue;
                float secondaryDamage = (float) (baseDamage * (0.5 + count * 0.1)) * chargeMult;
                e.attackEntityFrom(DamageSource.causePlayerDamage(player), secondaryDamage);
            }
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 0.8F, 0.8F);
    }

    private boolean isOnCooldown(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return false;
        return tag.getLong("sweep_cooldown") > System.currentTimeMillis();
    }

    private void setCooldown(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setLong("sweep_cooldown", System.currentTimeMillis() + SWEEP_COOLDOWN * 50L);
    }

    private int getCharge(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null ? tag.getInteger("chain_charge") : 0;
    }

    private boolean consumeCharge(ItemStack stack, int amount) {
        int charge = getCharge(stack);
        if (charge < amount) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger("chain_charge", charge - amount);
        return true;
    }

    private int getMaxBounces(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);
        return tag != null && tag.hasKey("maxBounces") ? tag.getInteger("maxBounces") : 3;
    }

    private float getBounceRange(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);
        return tag != null && tag.hasKey("bounceRange") ? tag.getFloat("bounceRange") : 4.0f;
    }

    @Override
    public int getCurrentAmmo(ItemStack stack) {
        int base = super.getCurrentAmmo(stack);
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag != null && toolTag.hasKey("ammoBonus")) {
            return base + toolTag.getInteger("ammoBonus");
        }
        return base;
    }

    @Override
    public List<String> getInformation(ItemStack stack, boolean detailed) {
        List<String> info = super.getInformation(stack, detailed);
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag == null) return info;
        int maxBounces = toolTag.hasKey("maxBounces") ? toolTag.getInteger("maxBounces") : 3;
        float bounceRange = toolTag.hasKey("bounceRange") ? toolTag.getFloat("bounceRange") : 4.0f;
        info.add(Util.translateFormatted("stat.chain.max_bounces.name", maxBounces));
        info.add(Util.translateFormatted("stat.chain.bounce_range.name", bounceRange));
        if (detailed) {
            int charge = getCharge(stack);
            info.add(Util.translateFormatted("stat.chain.charge.tooltip",
                    TextFormatting.GOLD + String.valueOf(charge) + TextFormatting.RESET,
                    TextFormatting.GOLD + String.valueOf(MAX_CHARGE) + TextFormatting.RESET));
        }
        return info;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (flagIn.isAdvanced()) return;
        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        if (toolTag == null) return;
        int charge = getCharge(stack);
        tooltip.add(Util.translateFormatted("stat.chain.charge.tooltip",
                TextFormatting.GOLD + String.valueOf(charge) + TextFormatting.RESET,
                TextFormatting.GOLD + String.valueOf(MAX_CHARGE) + TextFormatting.RESET));
    }

    @Override
    public ProjectileNBT buildTagData(List<Material> materials) {
        if (materials == null) {
            materials = new ArrayList<>();
        }
        ToolNBT base = buildDefaultTag(materials);
        NBTTagCompound tag = base.get();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        Material chainMat = materials.size() > 2 ? materials.get(2) : Material.UNKNOWN;
        if (chainMat == null) {
            chainMat = Material.UNKNOWN;
        }
        ChainMaterialStats stats = chainMat.getStatsOrUnknown(ChainPartType.CHAIN);
        if (stats == null || stats == ChainMaterialStats.UNKNOWN) {
            stats = new ChainMaterialStats(3, 4.0f, 0);
        }
        tag.setInteger("maxBounces", stats.maxBounces);
        tag.setFloat("bounceRange", stats.bounceRange);
        tag.setInteger("ammoBonus", stats.ammoBonus);
        ProjectileNBT data = new ProjectileNBT(tag);
        data.accuracy = 0.9f;
        data.write(tag);
        return data;
    }

    @Override
    public EntityProjectileBase getProjectile(ItemStack stack, ItemStack launcher, World world, EntityPlayer player,
                                              float speed, float inaccuracy, float power, boolean usedAmmo) {
        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = (float) data.attack * BASE_DAMAGE_RATIO;
        int maxBounces = getMaxBounces(stack);
        float bounceRange = getBounceRange(stack);
        return new EntityChainBlade(world, player, stack, speed, baseDamage,
                maxBounces, bounceRange);
    }

    private static String getToolId(ItemStack stack) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        NBTTagCompound stats = TagUtil.getToolTag(root);
        if (stats.hasKey("UUID")) {
            return stats.getString("UUID");
        }
        return stack.getItem().getRegistryName().toString() + "#" + System.identityHashCode(stack);
    }

    public static void addActiveChainBlade(String toolId, Entity entity) {
        Set<Entity> set = activeChainBlades.computeIfAbsent(toolId, k -> new HashSet<>());
        set.add(entity);
    }

    public static void removeActiveChainBlade(String toolId, Entity entity) {
        Set<Entity> set = activeChainBlades.get(toolId);
        if (set != null) {
            set.remove(entity);
            if (set.isEmpty()) {
                activeChainBlades.remove(toolId);
            }
        }
    }
}