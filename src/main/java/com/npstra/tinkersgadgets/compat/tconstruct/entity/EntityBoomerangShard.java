package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.Boomerang;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityBoomerangShard extends EntityProjectileBase implements IEntityAdditionalSpawnData {

    private static final int MAX_ALIVE = 300;
    private static final double RETURN_SPEED = 0.4D;
    private static final double SMOOTH_FACTOR = 0.4D;

    private boolean returning;
    private EntityPlayer associatedPlayer;
    private ItemStack damageStack = ItemStack.EMPTY;
    private ItemStack renderStack = ItemStack.EMPTY;
    private String toolId = "";
    private UUID ignoreEntityId;
    private final Set<UUID> hitEntities = new HashSet<>();
    private int stuckTicks;
    private int staticTicks;
    private double totalDistanceTraveled;
    private double initialSpeed;

    private boolean piercing;
    private int pierceCount;
    private boolean returnDamageEnabled;

    public EntityBoomerangShard(World world) {
        super(world);
        setSize(0.3f, 0.1f);
    }

    public EntityBoomerangShard(World world, EntityPlayer shooter, float speed, float inaccuracy, float damage, ItemStack damageStack, ItemStack renderStack, UUID ignoreId) {
        super(world, shooter, speed, inaccuracy, 1.0f, damageStack.copy(), ItemStack.EMPTY);
        this.damageStack = damageStack.copy();
        this.renderStack = renderStack.copy();
        this.ignoreEntityId = ignoreId;
        this.associatedPlayer = shooter;
        this.shootingEntity = shooter;
        pickupStatus = PickupStatus.ALLOWED;
        setDamage(damage);
        this.initialSpeed = speed;
        if (this.renderStack.isEmpty()) {
            this.renderStack = new ItemStack(net.minecraft.init.Items.STICK);
        }
    }

    public void setToolId(String id) { this.toolId = id; }
    public void setPiercing(boolean piercing) { this.piercing = piercing; }
    public void setPierceCount(int count) { this.pierceCount = count; }
    public void setReturnDamageEnabled(boolean enabled) { this.returnDamageEnabled = enabled; }

    @Override
    protected void init() {
        bounceOnNoDamage = false;
    }

    @Override
    public ItemStack getArrowStack() {
        return renderStack;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        double deltaX = posX - prevPosX;
        double deltaY = posY - prevPosY;
        double deltaZ = posZ - prevPosZ;
        totalDistanceTraveled += Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        if (!world.isRemote) {
            double baseMaxDistance = 8.0D;
            double speedFactor = Math.min(1.0D, initialSpeed / 1.8D);
            double maxDistance = baseMaxDistance * speedFactor;
            double slowStartDistance = maxDistance * 0.75D;

            if (!returning) {
                if (totalDistanceTraveled >= maxDistance) {
                    returning = true;
                } else if (totalDistanceTraveled > slowStartDistance) {
                    double progress = (totalDistanceTraveled - slowStartDistance) / (maxDistance - slowStartDistance);
                    double minSpeed = 0.5D;
                    double speedMultiplier = 1.0D - progress * (1.0D - minSpeed);
                    motionX *= speedMultiplier;
                    motionY *= speedMultiplier;
                    motionZ *= speedMultiplier;
                }
            }

            if (ticksExisted > MAX_ALIVE) {
                returning = true;
            }

            double currentSpeed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            if (!returning && currentSpeed < 0.05D) {
                staticTicks++;
                if (staticTicks > 40) {
                    setDead();
                    return;
                }
            } else {
                staticTicks = 0;
            }

            if (returning) {
                if (shootingEntity != null && shootingEntity.isEntityAlive()) {
                    if (returnDamageEnabled) checkReturnHit();
                    returnToShooter();
                    double dist = getDistance(shootingEntity);
                    if (currentSpeed < 0.1D && dist > 2.0D) {
                        stuckTicks++;
                        if (stuckTicks > 60) {
                            setDead();
                        }
                    } else {
                        stuckTicks = 0;
                    }
                } else {
                    setDead();
                }
            }
        }
    }

    private void checkReturnHit() {
        AxisAlignedBB box = getEntityBoundingBox().grow(0.5D);
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, box);
        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase && entity != shootingEntity && !hitEntities.contains(entity.getUniqueID())) {
                if (!damageStack.isEmpty() && damageStack.getItem() instanceof ToolCore) {
                    ToolHelper.attackEntity(damageStack, (ToolCore) damageStack.getItem(), (EntityLivingBase) shootingEntity, entity, this);
                    hitEntities.add(entity.getUniqueID());
                }
            }
        }
    }

    private void returnToShooter() {
        Vec3d targetPos = new Vec3d(shootingEntity.posX, shootingEntity.posY + shootingEntity.getEyeHeight(), shootingEntity.posZ);
        double dx = targetPos.x - posX;
        double dy = targetPos.y - posY;
        double dz = targetPos.z - posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist > 1.2D) {
            double returnSpeed = Math.max(RETURN_SPEED, initialSpeed * 0.5D);
            double desiredX = dx / dist * returnSpeed;
            double desiredY = dy / dist * returnSpeed;
            double desiredZ = dz / dist * returnSpeed;
            motionX += (desiredX - motionX) * SMOOTH_FACTOR;
            motionY += (desiredY - motionY) * SMOOTH_FACTOR;
            motionZ += (desiredZ - motionZ) * SMOOTH_FACTOR;
        } else {
            if (shootingEntity instanceof EntityPlayer) onCollideWithPlayer((EntityPlayer) shootingEntity);
            setDead();
        }
    }

    @Override
    protected void doMoveUpdate() {
        if (returning && shootingEntity != null) {
            double dx = shootingEntity.posX - posX;
            double dy = (shootingEntity.posY + shootingEntity.getEyeHeight()) - posY;
            double dz = shootingEntity.posZ - posZ;
            double horizontalDist = MathHelper.sqrt(dx * dx + dz * dz);
            this.rotationYaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            this.rotationPitch = (float) (-(MathHelper.atan2(dy, horizontalDist) * (180D / Math.PI)));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
        } else {
            super.doMoveUpdate();
        }
    }

    @Override
    public void onHitBlock(RayTraceResult raytraceResult) {
        returning = true;
        inGround = false;
        arrowShake = 0;
        ticksInGround = 0;
    }

    @Override
    public void onHitEntity(RayTraceResult raytraceResult) {
        Entity entityHit = raytraceResult.entityHit;
        if (ignoreEntityId != null && ignoreEntityId.equals(entityHit.getUniqueID())) return;
        if (hitEntities.contains(entityHit.getUniqueID())) return;
        hitEntities.add(entityHit.getUniqueID());

        if (!world.isRemote && shootingEntity instanceof EntityLivingBase) {
            if (!damageStack.isEmpty() && damageStack.getItem() instanceof ToolCore) {
                ToolHelper.attackEntity(damageStack, (ToolCore) damageStack.getItem(), (EntityLivingBase) shootingEntity, entityHit, this);
            }
        }

        if (piercing && pierceCount > 0) {
            pierceCount--;
            Vec3d motion = new Vec3d(motionX, motionY, motionZ);
            if (motion.lengthSquared() > 0.0D) {
                motion = motion.normalize();
                motionX = motion.x * 0.95D;
                motionY = motion.y * 0.95D;
                motionZ = motion.z * 0.95D;
            }
            double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            if (speed < 0.25D || pierceCount <= 0) {
                returning = true;
                stuckTicks = 0;
            }
            if (pierceCount > 0 && speed >= 0.25D) return;
        }

        returning = true;
        stuckTicks = 0;
        inGround = false;
        arrowShake = 0;
        ticksInGround = 0;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {}

    @Override
    public double getGravity() { return 0.0D; }

    @Override
    public double getSlowdown() { return 0.0D; }

    @Nullable
    @Override
    protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
        if (returning) return null;
        Entity found = super.findEntityOnPath(start, end);
        if (found != null && ignoreEntityId != null && ignoreEntityId.equals(found.getUniqueID())) {
            Vec3d direction = end.subtract(start).normalize();
            Vec3d newStart = start.add(direction.scale(0.5));
            return super.findEntityOnPath(newStart, end);
        }
        return found;
    }

    @Override
    public void setDead() {
        if (!world.isRemote && !toolId.isEmpty()) {
            Boomerang.removeActiveBoomerang(toolId, this);
        }
        super.setDead();
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        super.writeSpawnData(data);
        ByteBufUtils.writeItemStack(data, renderStack);
        data.writeBoolean(returning);
        data.writeBoolean(piercing);
        data.writeInt(pierceCount);
        data.writeBoolean(returnDamageEnabled);
        boolean hasIgnore = ignoreEntityId != null;
        data.writeBoolean(hasIgnore);
        if (hasIgnore) {
            data.writeLong(ignoreEntityId.getMostSignificantBits());
            data.writeLong(ignoreEntityId.getLeastSignificantBits());
        }
        data.writeDouble(totalDistanceTraveled);
        data.writeDouble(initialSpeed);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        this.renderStack = ByteBufUtils.readItemStack(data);
        returning = data.readBoolean();
        piercing = data.readBoolean();
        pierceCount = data.readInt();
        returnDamageEnabled = data.readBoolean();
        if (data.readBoolean()) {
            ignoreEntityId = new UUID(data.readLong(), data.readLong());
        }
        totalDistanceTraveled = data.readDouble();
        initialSpeed = data.readDouble();
    }

    @Override
    protected void playHitEntitySound() {
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }
}