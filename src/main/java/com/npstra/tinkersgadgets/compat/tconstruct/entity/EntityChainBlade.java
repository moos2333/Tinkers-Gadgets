package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.utils.TagUtil;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.ChainBlade;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityChainBlade extends EntityProjectileBase implements IEntityAdditionalSpawnData {
    private static final int MAX_ALIVE = 120;
    private static final double GRAVITY = 0.065D;
    private static final double MAX_RETURN_SPEED = 3.5D;
    private static final float PULL_STRENGTH_HIT = 0.5f;
    private static final float PULL_STRENGTH_RETURN = 1.0f;
    private static final int STUCK_TIMEOUT = 80;

    private EntityPlayer shooter;
    private ItemStack weaponStack = ItemStack.EMPTY;
    private final Set<UUID> hitEntities = new HashSet<>();
    private int maxBounces;
    private float bounceRange;
    private int bounceCount;
    protected boolean returning;
    private int hitCount;
    private double baseDamage;
    private String toolId = "";
    private String weaponUuid = "";
    private int stuckTicks;

    public EntityChainBlade(World world) {
        super(world);
        setSize(0.3f, 0.1f);
    }

    public EntityChainBlade(World world, EntityPlayer shooter, ItemStack weapon, float speed, float damage,
                            int maxBounces, float bounceRange) {
        super(world, shooter, speed, 1.0f, 1.0f, ItemStack.EMPTY, ItemStack.EMPTY);
        this.shooter = shooter;
        this.weaponStack = weapon.copy();
        this.baseDamage = damage;
        NBTTagCompound toolTag = TagUtil.getToolTag(weapon);
        if (toolTag != null) {
            if (toolTag.hasKey("maxBounces")) {
                this.maxBounces = Math.max(1, toolTag.getInteger("maxBounces"));
            } else {
                this.maxBounces = Math.max(1, maxBounces);
            }
            if (toolTag.hasKey("bounceRange")) {
                this.bounceRange = Math.max(0.5f, toolTag.getFloat("bounceRange"));
            } else {
                this.bounceRange = Math.max(0.5f, bounceRange);
            }
        } else {
            this.maxBounces = Math.max(1, maxBounces);
            this.bounceRange = Math.max(0.5f, bounceRange);
        }
        this.shootingEntity = shooter;
        this.hitCount = 0;
        this.bounceCount = 0;
        this.returning = false;
        this.stuckTicks = 0;
        NBTTagCompound tag = weapon.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            weapon.setTagCompound(tag);
        }
        if (!tag.hasKey("chain_uuid")) {
            tag.setString("chain_uuid", UUID.randomUUID().toString());
        }
        this.weaponUuid = tag.getString("chain_uuid");
        setDamage((float) damage);
    }

    public boolean isReturning() {
        return returning;
    }

    public void setToolId(String id) {
        this.toolId = id;
    }

    private double getCurrentSpeed() {
        return Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
    }

    private Vec3d getShooterTargetPos() {
        if (shooter == null) return new Vec3d(posX, posY, posZ);
        return new Vec3d(shooter.posX, shooter.posY + shooter.getEyeHeight() * 0.7, shooter.posZ);
    }

    private double calculateReturnSpeed(double distance) {
        if (distance > 8.0D) {
            return 2.4D;
        } else if (distance > 4.0D) {
            return 1.6D + (distance - 4.0D) * 0.2D;
        } else if (distance > 2.0D) {
            return 1.2D + (distance - 2.0D) * 0.2D;
        } else {
            return 0.8D + distance * 0.2D;
        }
    }

    @Override
    public double getGravity() {
        return returning ? 0.0D : GRAVITY;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) return;
        if (ticksExisted > MAX_ALIVE) {
            returning = true;
        }
        if (returning) {
            if (shooter != null && shooter.isEntityAlive()) {
                returnToShooter();
                double dist = getDistance(shooter);
                double speed = getCurrentSpeed();

                if (speed < 0.05D && dist > 3.0D) {
                    stuckTicks++;
                    if (stuckTicks > STUCK_TIMEOUT) {
                        Vec3d dir = new Vec3d(shooter.posX - posX, shooter.posY + shooter.getEyeHeight() * 0.7 - posY, shooter.posZ - posZ).normalize();
                        motionX += dir.x * 1.5D;
                        motionY += dir.y * 1.5D;
                        motionZ += dir.z * 1.5D;
                        stuckTicks = 0;
                    }
                } else {
                    stuckTicks = 0;
                }

                if (dist < 1.5D) {
                    onCollideWithPlayer(shooter);
                    setDead();
                }
            } else {
                setDead();
            }
        }
        if (!returning) {
            float yaw = (float) (MathHelper.atan2(motionZ, motionX) * (180D / Math.PI)) - 90.0F;
            float pitch = (float) (-(MathHelper.atan2(motionY, MathHelper.sqrt(motionX * motionX + motionZ * motionZ)) * (180D / Math.PI)));
            rotationYaw = yaw;
            rotationPitch = pitch;
            prevRotationYaw = rotationYaw;
            prevRotationPitch = rotationPitch;
        }
    }

    private void returnToShooter() {
        if (shooter == null || !shooter.isEntityAlive()) {
            setDead();
            return;
        }
        Vec3d target = getShooterTargetPos();
        Vec3d delta = target.subtract(new Vec3d(posX, posY, posZ));
        double dist = delta.length();

        if (dist < 1.2D) {
            onCollideWithPlayer(shooter);
            setDead();
            return;
        }

        Vec3d dir = delta.normalize();
        double desiredSpeed = calculateReturnSpeed(dist);
        double currentSpeed = getCurrentSpeed();
        double smoothFactor = Math.min(0.3D, 0.1D + 0.2D / (currentSpeed + 0.1D));

        motionX += (dir.x * desiredSpeed - motionX) * smoothFactor;
        motionY += (dir.y * desiredSpeed - motionY) * smoothFactor;
        motionZ += (dir.z * desiredSpeed - motionZ) * smoothFactor;

        double newSpeed = getCurrentSpeed();
        if (newSpeed > MAX_RETURN_SPEED) {
            motionX = motionX / newSpeed * MAX_RETURN_SPEED;
            motionY = motionY / newSpeed * MAX_RETURN_SPEED;
            motionZ = motionZ / newSpeed * MAX_RETURN_SPEED;
        }

        dealReturnDamage();
    }

    private void dealReturnDamage() {
        if (shooter == null) return;
        double radius = 2.0D;
        AxisAlignedBB box = getEntityBoundingBox().grow(radius);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, box,
                e -> e != shooter && e.isEntityAlive() && !hitEntities.contains(e.getUniqueID()));
        for (EntityLivingBase target : targets) {
            float damage = (float) (baseDamage * (1.0D + hitCount * 0.5D));
            damage = Math.min((float) (baseDamage * 4.0D), damage);
            target.attackEntityFrom(DamageSource.causePlayerDamage(shooter), damage);
            hitCount++;
            hitEntities.add(target.getUniqueID());
            pullEntityTowardsPlayer(target, shooter, PULL_STRENGTH_RETURN);
            playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 0.8F + rand.nextFloat() * 0.4F);
        }
    }

    private void pullEntityTowardsPlayer(EntityLivingBase target, EntityPlayer player, float strength) {
        if (target == null || player == null || target.world.isRemote) return;
        if (target == player) return;
        double dx = player.posX - target.posX;
        double dy = (player.posY + player.getEyeHeight() * 0.5) - target.posY;
        double dz = player.posZ - target.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 1.0D) return;
        double pullSpeed = Math.min(1.2D, strength * 1.5D);
        double speed = Math.min(pullSpeed, dist * 0.2D);
        target.motionX = dx / dist * speed;
        target.motionY = dy / dist * speed * 0.4D;
        target.motionZ = dz / dist * speed;
        target.velocityChanged = true;
        target.hurtResistantTime = 0;
    }

    @Override
    public void onHitEntity(RayTraceResult result) {
        Entity target = result.entityHit;
        if (target == shooter || target == this) return;
        UUID id = target.getUniqueID();
        if (hitEntities.contains(id)) return;
        if (!world.isRemote && shootingEntity instanceof EntityLivingBase) {
            float damage = (float) (baseDamage * (1.0D + hitCount * 0.5D));
            damage = Math.min((float) (baseDamage * 4.0D), damage);
            target.attackEntityFrom(DamageSource.causePlayerDamage(shooter), damage);
            hitCount++;
            hitEntities.add(id);
            if (target instanceof EntityLivingBase) {
                pullEntityTowardsPlayer((EntityLivingBase) target, shooter, PULL_STRENGTH_HIT);
            }
            playHitEntitySound();
            playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 0.8F + rand.nextFloat() * 0.4F);
        }
        if (bounceCount < maxBounces) {
            EntityLivingBase next = findNextTarget();
            if (next != null) {
                bounceCount++;
                Vec3d dir = new Vec3d(
                        next.posX - posX,
                        next.posY + next.getEyeHeight() * 0.5 - posY,
                        next.posZ - posZ
                ).normalize();
                double speed = 1.2D + rand.nextDouble() * 0.3D;
                motionX = dir.x * speed;
                motionY = dir.y * speed + 0.1D;
                motionZ = dir.z * speed;
                return;
            }
        }
        returning = true;
    }

    @Nullable
    private EntityLivingBase findNextTarget() {
        if (shooter == null) return null;
        AxisAlignedBB box = new AxisAlignedBB(
                posX - bounceRange, posY - bounceRange, posZ - bounceRange,
                posX + bounceRange, posY + bounceRange, posZ + bounceRange
        );
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, box,
                e -> e != shooter && !hitEntities.contains(e.getUniqueID()) && e.isEntityAlive());
        if (targets.isEmpty()) return null;
        targets.sort((a, b) -> Double.compare(a.getDistance(this), b.getDistance(this)));
        return targets.get(0);
    }

    @Override
    public void onHitBlock(RayTraceResult result) {
        if (!returning) {
            returning = true;
            inGround = false;
            arrowShake = 0;
            ticksInGround = 0;
        } else if (shooter != null) {
            Vec3d dir = new Vec3d(shooter.posX - posX, shooter.posY + shooter.getEyeHeight() * 0.7 - posY, shooter.posZ - posZ).normalize();
            motionX += dir.x * 0.5D;
            motionY += dir.y * 0.5D;
            motionZ += dir.z * 0.5D;
        }
    }

    @Override
    public ItemStack getArrowStack() {
        return weaponStack;
    }

    private ItemStack findActualWeapon() {
        if (shooter == null || weaponUuid.isEmpty()) return ItemStack.EMPTY;
        ItemStack main = shooter.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() instanceof ChainBlade) {
            NBTTagCompound tag = main.getTagCompound();
            if (tag != null && weaponUuid.equals(tag.getString("chain_uuid"))) {
                return main;
            }
        }
        ItemStack off = shooter.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ChainBlade) {
            NBTTagCompound tag = off.getTagCompound();
            if (tag != null && weaponUuid.equals(tag.getString("chain_uuid"))) {
                return off;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setDead() {
        if (!world.isRemote && !toolId.isEmpty()) {
            ChainBlade.removeActiveChainBlade(toolId, this);
        }
        if (!world.isRemote && !weaponUuid.isEmpty() && shooter != null && hitCount > 0) {
            ItemStack actual = findActualWeapon();
            if (!actual.isEmpty()) {
                int charge = getCharge(actual);
                charge = Math.min(30, charge + hitCount);
                setCharge(actual, charge);
            }
        }
        super.setDead();
    }

    private int getCharge(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        return tag.getInteger("chain_charge");
    }

    private void setCharge(ItemStack stack, int value) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger("chain_charge", value);
    }

    @Override
    protected void onEntityHit(Entity entity) {}

    @Override
    protected void playHitEntitySound() {
        playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
    }

    protected boolean canFitInBlock() {
        return true;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        super.writeSpawnData(data);
        ByteBufUtils.writeItemStack(data, weaponStack);
        data.writeInt(shooter != null ? shooter.getEntityId() : -1);
        data.writeInt(maxBounces);
        data.writeFloat(bounceRange);
        data.writeInt(bounceCount);
        data.writeBoolean(returning);
        data.writeInt(hitCount);
        data.writeDouble(baseDamage);
        ByteBufUtils.writeUTF8String(data, weaponUuid);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        this.weaponStack = ByteBufUtils.readItemStack(data);
        int shooterId = data.readInt();
        if (shooterId != -1 && world != null) {
            Entity entity = world.getEntityByID(shooterId);
            if (entity instanceof EntityPlayer) {
                this.shooter = (EntityPlayer) entity;
                this.shootingEntity = entity;
            }
        }
        maxBounces = data.readInt();
        bounceRange = data.readFloat();
        bounceCount = data.readInt();
        returning = data.readBoolean();
        hitCount = data.readInt();
        baseDamage = data.readDouble();
        weaponUuid = ByteBufUtils.readUTF8String(data);
    }
}