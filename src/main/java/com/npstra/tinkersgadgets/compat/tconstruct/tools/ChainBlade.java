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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class ChainBlade extends ProjectileCore {
    private static final Map<String, Set<Entity>> activeChainBlades = new WeakHashMap<>();
    private static final int CHARGE_COST = 10;
    private static final int SWEEP_COOLDOWN = 40;
    private static final float SWEEP_BASE_RADIUS = 3.5f;
    private static final float SWEEP_BASE_ANGLE = 60.0f;
    private static final float SWEEP_MAX_RADIUS = 5.0f;
    private static final float SWEEP_MAX_ANGLE = 180.0f;
    private static final float BASE_DAMAGE_RATIO = 0.4f;
    private static final int THROW_CHARGE = 20;

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
        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = (float) data.attack * BASE_DAMAGE_RATIO;
        int maxBounces = getMaxBounces(stack);
        float bounceRange = getBounceRange(stack);
        float sweepRangeBonus = getSweepRangeBonus(stack);
        float damageBonus = getDamageBonus(stack);

        EntityChainBlade entity = new EntityChainBlade(
                world, player, stack, speed, baseDamage,
                maxBounces, bounceRange, sweepRangeBonus, damageBonus
        );
        entity.setToolId(toolId);
        entity.setPosition(player.posX, player.posY + player.getEyeHeight() - 0.1, player.posZ);
        Vec3d look = player.getLookVec();
        entity.motionX = look.x * speed;
        entity.motionY = look.y * speed + 0.05D;
        entity.motionZ = look.z * speed;
        world.spawnEntity(entity);
        addActiveChainBlade(toolId, entity);
    }

    private void performSweep(World world, EntityPlayer player, ItemStack stack) {
        if (world.isRemote) return;

        float bonus = getSweepRangeBonus(stack);
        float radius = Math.min(SWEEP_MAX_RADIUS, SWEEP_BASE_RADIUS * (1.0f + bonus));
        float angle = Math.min(SWEEP_MAX_ANGLE, SWEEP_BASE_ANGLE * (1.0f + bonus));
        float damageBonus = getDamageBonus(stack);
        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = (float) data.attack * BASE_DAMAGE_RATIO;
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
        boolean hasCharge = consumeCharge(stack, CHARGE_COST);
        float chargeMult = hasCharge ? 1.5f : 1.0f;

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
                float primaryDamage = (float) (baseDamage * (1.5 + count * 0.1 * (1.0 + damageBonus))) * chargeMult;
                primary.attackEntityFrom(DamageSource.causePlayerDamage(player), primaryDamage);
            }
            for (EntityLivingBase e : targets) {
                if (e == primary) continue;
                float secondaryDamage = (float) (baseDamage * (0.5 + count * 0.1 * (1.0 + damageBonus))) * chargeMult;
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

    private float getSweepRangeBonus(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);
        return tag != null && tag.hasKey("sweepRangeBonus") ? tag.getFloat("sweepRangeBonus") : 0.0f;
    }

    private float getDamageBonus(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);
        return tag != null && tag.hasKey("damageBonus") ? tag.getFloat("damageBonus") : 0.0f;
    }

    @Override
    public ProjectileNBT buildTagData(List<Material> materials) {
        ProjectileNBT data = new ProjectileNBT();

        Material handleMat = materials.size() > 0 && materials.get(0) != null ? materials.get(0) : Material.UNKNOWN;
        Material bladeMat = materials.size() > 1 && materials.get(1) != null ? materials.get(1) : Material.UNKNOWN;
        Material chainMat = materials.size() > 2 && materials.get(2) != null ? materials.get(2) : Material.UNKNOWN;

        data.head(bladeMat.getStatsOrUnknown(MaterialTypes.HEAD));
        data.extra(bladeMat.getStatsOrUnknown(MaterialTypes.EXTRA),
                handleMat.getStatsOrUnknown(MaterialTypes.EXTRA),
                chainMat.getStatsOrUnknown(MaterialTypes.EXTRA));
        data.accuracy = 0.9f;

        NBTTagCompound tag = data.get();
        if (chainMat != Material.UNKNOWN && chainMat.hasStats(ChainPartType.CHAIN)) {
            ChainMaterialStats stats = chainMat.getStatsOrUnknown(ChainPartType.CHAIN);
            if (stats != ChainMaterialStats.UNKNOWN) {
                tag.setInteger("maxBounces", stats.maxBounces);
                tag.setFloat("bounceRange", stats.bounceRange);
                tag.setFloat("sweepRangeBonus", stats.sweepRangeBonus);
                tag.setFloat("damageBonus", stats.damageBonus);
            }
        }
        if (!tag.hasKey("maxBounces")) tag.setInteger("maxBounces", 3);
        if (!tag.hasKey("bounceRange")) tag.setFloat("bounceRange", 4.0f);
        if (!tag.hasKey("sweepRangeBonus")) tag.setFloat("sweepRangeBonus", 0.0f);
        if (!tag.hasKey("damageBonus")) tag.setFloat("damageBonus", 0.0f);

        return data;
    }

    @Override
    public EntityProjectileBase getProjectile(ItemStack stack, ItemStack launcher, World world, EntityPlayer player,
                                              float speed, float inaccuracy, float power, boolean usedAmmo) {
        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = (float) data.attack * BASE_DAMAGE_RATIO;
        int maxBounces = getMaxBounces(stack);
        float bounceRange = getBounceRange(stack);
        float sweepRangeBonus = getSweepRangeBonus(stack);
        float damageBonus = getDamageBonus(stack);

        return new EntityChainBlade(world, player, stack, speed, baseDamage,
                maxBounces, bounceRange, sweepRangeBonus, damageBonus);
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