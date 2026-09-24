package com.npstra.tinkersgadgets.compat.tconstruct.tools;

import com.npstra.tinkersgadgets.compat.tconstruct.materials.GunBarrelMaterialStats;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.GunBarrelPartType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class PistolSword extends TinkerToolCore {

    public static final int BASE_CAPACITY = 6;
    public static final byte AMMO_EMPTY = 0;
    public static final byte AMMO_POISON = 1;
    public static final byte AMMO_PIERCE = 2;
    public static final byte AMMO_NORMAL = 3;

    public static final float SWEEP_RATIO = 0.5f;
    public static final double SWEEP_RADIUS = 2.0;
    public static final double SWEEP_HEIGHT = 1.0;
    public static final double RANGE = 12.0;
    public static final int POISON_DURATION = 100;
    public static final int POISON_AMPLIFIER = 2;
    public static final float PIERCE_RATIO = 0.5f;
    public static final int SHOT_COOLDOWN = 20;

    private static final String KEY_AMMO = "pistolSwordAmmo";
    private static final String KEY_PENDING = "pistolSwordPending";
    private static final String KEY_BARREL_AMMO = "gunBarrelAmmo";
    private static final String KEY_BARREL_RANGED = "gunBarrelRanged";

    private static final byte[] EMPTY_AMMO = new byte[0];

    public PistolSword() {
        super(PartMaterialType.head(TinkerTools.largeSwordBlade),
                PartMaterialType.handle(TinkerTools.toolRod),
                new PartMaterialType(GadgetsRegister.gunBarrel, GunBarrelPartType.GUN_BARREL));
        addCategory(Category.WEAPON);
        setRegistryName("pistol_sword");
        setTranslationKey("tinkersgadgets.pistol_sword");
    }

    @Override
    public float damagePotential() {
        return 1.5f;
    }

    @Override
    public double attackSpeed() {
        return 1.4d;
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{0};
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (player.world.isRemote) return true;
        if (!(entity instanceof EntityLivingBase)) return false;
        EntityLivingBase primary = (EntityLivingBase) entity;

        ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
        float baseDamage = (float) data.attack;

        ChainBlade.attackWithTraits(stack, player, primary, null, baseDamage);

        byte pending = getPending(stack);
        if (pending == AMMO_POISON) {
            primary.addPotionEffect(new PotionEffect(MobEffects.POISON, POISON_DURATION, POISON_AMPLIFIER));
            clearPending(stack);
        } else if (pending == AMMO_PIERCE) {
            primary.attackEntityFrom(DamageSource.causePlayerDamage(player), baseDamage * PIERCE_RATIO);
            clearPending(stack);
        }

        collectAmmo(stack, determineAmmoType(primary));
        performSweep(stack, player, primary, baseDamage);

        return true;
    }

    private void performSweep(ItemStack stack, EntityPlayer player, EntityLivingBase primary, float baseDamage) {
        AxisAlignedBB box = player.getEntityBoundingBox().grow(SWEEP_RADIUS, SWEEP_HEIGHT, SWEEP_RADIUS);
        List<EntityLivingBase> nearby = player.world.getEntitiesWithinAABB(EntityLivingBase.class, box,
                e -> e != player && e != primary && e.isEntityAlive());
        if (nearby.isEmpty()) return;
        float sweepDamage = baseDamage * SWEEP_RATIO;
        for (EntityLivingBase target : nearby) {
            ChainBlade.attackWithTraits(stack, player, target, null, sweepDamage);
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (ToolHelper.isBroken(stack)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (player.getCooldownTracker().hasCooldown(stack.getItem())) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (!world.isRemote) {
            if (getAmmo(stack).length == 0) {
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 0.8F, 1.2F);
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
            byte fired = consumeAmmo(stack);
            if (fired == AMMO_POISON || fired == AMMO_PIERCE) {
                setPending(stack, fired);
            }
            fireShot(world, player, stack);
            player.getCooldownTracker().setCooldown(stack.getItem(), SHOT_COOLDOWN);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void fireShot(World world, EntityPlayer player, ItemStack stack) {
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 1.8F);

        Vec3d eye = player.getPositionEyes(1.0F);
        Vec3d look = player.getLookVec();
        Vec3d end = eye.add(look.scale(RANGE));

        RayTraceResult blockHit = world.rayTraceBlocks(eye, end, false, true, false);
        if (blockHit != null) end = blockHit.hitVec;

        Vec3d muzzle = eye.add(look.scale(1.2));
        for (int i = 0; i < 4; i++) {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    muzzle.x + (world.rand.nextDouble() - 0.5) * 0.3,
                    muzzle.y + (world.rand.nextDouble() - 0.5) * 0.3,
                    muzzle.z + (world.rand.nextDouble() - 0.5) * 0.3,
                    0, 0.01, 0);
        }

        EntityLivingBase hit = findFirstHit(world, player, eye, end);
        if (hit != null) {
            ToolNBT data = new ToolNBT(TagUtil.getToolTag(stack));
            float baseDamage = (float) data.attack;
            float damage = baseDamage * getRangedMultiplier(stack);
            ChainBlade.attackWithTraits(stack, player, hit, null, damage);
        }
    }

    private EntityLivingBase findFirstHit(World world, EntityPlayer player, Vec3d eye, Vec3d end) {
        AxisAlignedBB box = new AxisAlignedBB(eye, end).grow(0.5);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, box,
                e -> e != player && e.isEntityAlive());
        if (targets.isEmpty()) return null;
        targets.sort((a, b) -> Double.compare(a.getDistanceSq(player), b.getDistanceSq(player)));
        for (EntityLivingBase target : targets) {
            if (target.getEntityBoundingBox().grow(0.3).calculateIntercept(eye, end) != null) {
                return target;
            }
        }
        return null;
    }

    private byte determineAmmoType(EntityLivingBase target) {
        if (target.isEntityUndead()) return AMMO_POISON;
        if (isArthropod(target)) return AMMO_PIERCE;
        return AMMO_NORMAL;
    }

    private boolean isArthropod(EntityLivingBase target) {
        return target instanceof EntitySpider
                || target instanceof EntityCaveSpider
                || target instanceof EntitySilverfish
                || target instanceof EntityEndermite;
    }

    private void collectAmmo(ItemStack stack, byte type) {
        byte[] current = getAmmo(stack);
        int capacity = getCapacity(stack);
        if (current.length >= capacity) {
            if (type != AMMO_NORMAL && current.length > 0 && current[0] == AMMO_NORMAL) {
                byte[] modified = Arrays.copyOf(current, current.length);
                modified[0] = type;
                setAmmo(stack, modified);
            }
            return;
        }
        byte[] newAmmo = new byte[current.length + 1];
        newAmmo[0] = type;
        System.arraycopy(current, 0, newAmmo, 1, current.length);
        setAmmo(stack, newAmmo);
    }

    private byte consumeAmmo(ItemStack stack) {
        byte[] current = getAmmo(stack);
        if (current.length == 0) return AMMO_EMPTY;
        byte fired = current[0];
        setAmmo(stack, Arrays.copyOfRange(current, 1, current.length));
        return fired;
    }

    private byte[] getAmmo(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(KEY_AMMO)) return EMPTY_AMMO;
        return tag.getByteArray(KEY_AMMO);
    }

    private void setAmmo(ItemStack stack, byte[] ammo) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setByteArray(KEY_AMMO, ammo);
    }

    private byte getPending(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(KEY_PENDING)) return AMMO_EMPTY;
        return tag.getByte(KEY_PENDING);
    }

    private void setPending(ItemStack stack, byte value) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setByte(KEY_PENDING, value);
    }

    private void clearPending(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) tag.removeTag(KEY_PENDING);
    }

    public static int getCapacity(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);
        int bonus = tag.hasKey(KEY_BARREL_AMMO) ? tag.getInteger(KEY_BARREL_AMMO) : 0;
        return Math.max(0, BASE_CAPACITY + bonus);
    }

    public static float getRangedMultiplier(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);
        return tag.hasKey(KEY_BARREL_RANGED) ? tag.getFloat(KEY_BARREL_RANGED) : 1.0f;
    }

    @Override
    public ToolNBT buildTagData(List<Material> materials) {
        ToolNBT data = buildDefaultTag(materials);
        NBTTagCompound tag = data.get();

        Material barrelMat = materials.size() > 2 ? materials.get(2) : Material.UNKNOWN;
        if (barrelMat == null) barrelMat = Material.UNKNOWN;
        GunBarrelMaterialStats stats = barrelMat.getStatsOrUnknown(GunBarrelPartType.GUN_BARREL);
        if (stats == null) stats = GunBarrelMaterialStats.UNKNOWN;

        int baseDurability = tag.getInteger("Durability");
        tag.setInteger("Durability", baseDurability + stats.durabilityBonus);
        tag.setInteger(KEY_BARREL_AMMO, stats.ammoBonus);
        tag.setFloat(KEY_BARREL_RANGED, stats.rangedDamage);

        return new ToolNBT(tag);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (flagIn.isAdvanced()) return;
        tooltip.add(buildAmmoTooltip(stack));
    }

    @Override
    public List<String> getInformation(ItemStack stack, boolean detailed) {
        List<String> info = super.getInformation(stack, detailed);
        info.add(buildAmmoTooltip(stack));
        return info;
    }

    private String buildAmmoTooltip(ItemStack stack) {
        byte[] ammo = getAmmo(stack);
        int capacity = getCapacity(stack);
        StringBuilder sb = new StringBuilder();
        sb.append(TextFormatting.GRAY).append(Util.translate("stat.pistol_sword.ammo.name"));
        for (int i = 0; i < capacity; i++) {
            sb.append(i < ammo.length ? colorFor(ammo[i]) : TextFormatting.DARK_GRAY);
            sb.append("▮");
        }
        sb.append(TextFormatting.RESET);
        return sb.toString();
    }

    private static String colorFor(byte ammoType) {
        switch (ammoType) {
            case AMMO_POISON: return TextFormatting.GREEN.toString();
            case AMMO_PIERCE: return TextFormatting.RED.toString();
            case AMMO_NORMAL: return TextFormatting.YELLOW.toString();
            default: return TextFormatting.GRAY.toString();
        }
    }
}