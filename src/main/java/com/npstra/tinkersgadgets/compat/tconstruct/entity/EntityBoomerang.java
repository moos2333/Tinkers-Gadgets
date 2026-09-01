package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.tools.ToolCore;
import com.npstra.tinkersgadgets.compat.tconstruct.tools.Boomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.traits.TraitBouncing;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityBoomerang extends EntityProjectileBase {

    private static final int MAX_ALIVE = 600;
    private static final double BASE_RETURN_SPEED = 0.45D;
    private static final double SMOOTH_FACTOR = 0.5D;
    private static final double MAX_DISTANCE_BASE = 16.0D;
    private static final double SLOW_START_FACTOR = 0.75D;
    private static final double MIN_SPEED_FACTOR = 0.5D;
    private static final double SPLIT_SPEED_FACTOR = 0.7D;
    private static final double SPLIT_ANGLE_DEG = 22.5D;
    private static final double BOUNCE_SEARCH_RADIUS = 9.0D;
    private static final double BOUNCE_SEARCH_HEIGHT = 5.0D;
    private static final int STUCK_TIMEOUT = 60;
    private static final double RETURN_DIST_THRESHOLD = 1.2D;
    private static final double STUCK_SPEED_THRESHOLD = 0.1D;
    private static final double STUCK_DIST_THRESHOLD = 2.0D;
    private static final double DEFLECT_MULTIPLIER_XZ = -1.5D;
    private static final double DEFLECT_MULTIPLIER_Y = -0.5D;
    private static final float SPLIT_DAMAGE_RATIO = 0.5f;

    private boolean returning;
    private EntityPlayer associatedPlayer;
    private boolean split;
    private double totalDistanceTraveled;
    private double initialSpeed;
    private int stuckTicks;
    private String toolId = "";

    private boolean piercing;
    private int pierceCount;
    private boolean bouncing;
    private int bounceCount;
    private int initialBounceCount;
    private boolean returnDamageEnabled;
    private final Set<UUID> hitEntities = new HashSet<>();
    private boolean interactEnabled;
    private boolean interactUsed;
    private boolean deflectProjectiles;
    private boolean shatterEnabled;

    public EntityBoomerang(World world) {
        super(world);
    }

    public EntityBoomerang(World world, EntityPlayer player, float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
        super(world, player, speed, inaccuracy, power, stack, launchingStack);
        setSize(0.3f, 0.1f);
        pickupStatus = PickupStatus.ALLOWED;
        this.initialSpeed = speed;
    }

    public void setAssociatedPlayer(EntityPlayer player) { associatedPlayer = player; }
    public void setPiercing(boolean piercing) { this.piercing = piercing; }
    public void setPierceCount(int count) { this.pierceCount = count; }
    public void setBouncing(boolean bouncing) { this.bouncing = bouncing; }
    public void setBounceCount(int count) {
        this.bounceCount = count;
        this.initialBounceCount = count;
    }
    public void setReturnDamageEnabled(boolean enabled) { this.returnDamageEnabled = enabled; }
    public void setInteract(boolean enabled) { this.interactEnabled = enabled; }
    public void setDeflectProjectiles(boolean enabled) { this.deflectProjectiles = enabled; }
    public void setShatter(boolean shatter) { this.shatterEnabled = shatter; }
    public boolean hasSplit() { return split; }
    public void setToolId(String id) { this.toolId = id; }
    public boolean isBouncing() { return this.bouncing; }
    public int getBounceCount() { return this.bounceCount; }

    private double getCurrentSpeed() {
        return Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
    }

    private Vec3d getShooterTargetPos() {
        return new Vec3d(shootingEntity.posX, shootingEntity.posY + shootingEntity.getEyeHeight(), shootingEntity.posZ);
    }

    private List<String> getMaterialIds(ItemStack toolStack) {
        NBTTagCompound root = TagUtil.getTagSafe(toolStack);
        NBTTagCompound tinkerData = root.getCompoundTag("TinkerData");
        NBTTagList materialsTagList = tinkerData.getTagList("Materials", 8);
        if (materialsTagList.tagCount() == 0) {
            materialsTagList = TagUtil.getBaseMaterialsTagList(root);
        }
        List<String> ids = new java.util.ArrayList<>();
        for (int i = 0; i < materialsTagList.tagCount(); i++) {
            String id = materialsTagList.getStringTagAt(i);
            if (id != null && !id.isEmpty()) ids.add(id);
        }
        if (ids.size() < 3) {
            NBTTagCompound toolTag = TagUtil.getToolTag(root);
            String mat0 = toolTag.getString("Material0");
            String mat1 = toolTag.getString("Material1");
            String mat2 = toolTag.getString("Material2");
            if (!mat0.isEmpty() && !mat1.isEmpty() && !mat2.isEmpty()) {
                ids.clear();
                ids.add(mat0);
                ids.add(mat1);
                ids.add(mat2);
            }
        }
        while (ids.size() < 3) ids.add("wood");
        return ids;
    }

    public void split(EntityLivingBase target) {
        if (split || world.isRemote) return;
        split = true;
        ItemStack toolStack = tinkerProjectile.getItemStack();
        if (toolStack.isEmpty() || !(toolStack.getItem() instanceof ToolCore)) {
            setDead();
            return;
        }
        List<String> materialIds = getMaterialIds(toolStack);
        String blade1Id = materialIds.get(0);
        String blade2Id = materialIds.get(2);
        ItemStack renderStack1 = new ItemStack(TinkerTools.knifeBlade);
        ItemStack renderStack2 = new ItemStack(TinkerTools.knifeBlade);
        NBTTagCompound tag1 = new NBTTagCompound();
        tag1.setString("Material", blade1Id);
        renderStack1.setTagCompound(tag1);
        NBTTagCompound tag2 = new NBTTagCompound();
        tag2.setString("Material", blade2Id);
        renderStack2.setTagCompound(tag2);
        ItemStack[] renderStacks = new ItemStack[]{renderStack1, renderStack2};
        Vec3d forward = new Vec3d(motionX, motionY, motionZ).normalize();
        double speed = getCurrentSpeed() * SPLIT_SPEED_FACTOR;
        double angle = Math.toRadians(SPLIT_ANGLE_DEG);
        Vec3d right = forward.rotateYaw((float) angle);
        Vec3d left = forward.rotateYaw((float) -angle);
        Vec3d[] directions = new Vec3d[]{right, left};
        Vec3d spawnPos = target.getPositionVector().add(forward.scale(1.5D)).add(0, target.height * 0.5, 0);
        float damage = (float) (ToolHelper.getActualDamage(toolStack, (EntityLivingBase) shootingEntity) * SPLIT_DAMAGE_RATIO);
        for (int i = 0; i < 2; i++) {
            EntityBoomerangShard shard = new EntityBoomerangShard(world, associatedPlayer, (float) speed, 0.0f, damage, toolStack, renderStacks[i], target.getUniqueID());
            shard.setToolId(this.toolId);
            shard.setPiercing(this.piercing);
            shard.setPierceCount(this.pierceCount);
            shard.setReturnDamageEnabled(this.returnDamageEnabled);
            shard.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            shard.motionX = directions[i].x * speed;
            shard.motionY = directions[i].y * speed;
            shard.motionZ = directions[i].z * speed;
            shard.rotationYaw = (float) (MathHelper.atan2(directions[i].z, directions[i].x) * (180D / Math.PI)) - 90.0F;
            shard.rotationPitch = (float) (-MathHelper.atan2(directions[i].y, MathHelper.sqrt(directions[i].x * directions[i].x + directions[i].z * directions[i].z)) * (180D / Math.PI));
            world.spawnEntity(shard);
            Boomerang.addActiveBoomerang(this.toolId, shard);
        }
        setDead();
    }

    @Override
    protected void init() {
        bounceOnNoDamage = false;
    }

    @Override
    protected ItemStack getArrowStack() {
        return tinkerProjectile.getItemStack();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        double deltaX = posX - prevPosX;
        double deltaY = posY - prevPosY;
        double deltaZ = posZ - prevPosZ;
        totalDistanceTraveled += Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        if (world.isRemote) return;

        if (deflectProjectiles && !returning) {
            deflectNearbyProjectiles();
        }

        double maxDistance = MAX_DISTANCE_BASE * Math.min(1.0D, initialSpeed / 1.8D);
        double slowStartDistance = maxDistance * SLOW_START_FACTOR;

        if (!returning) {
            if (totalDistanceTraveled >= maxDistance) {
                returning = true;
            } else if (totalDistanceTraveled > slowStartDistance) {
                double progress = (totalDistanceTraveled - slowStartDistance) / (maxDistance - slowStartDistance);
                double speedMultiplier = 1.0D - progress * (1.0D - MIN_SPEED_FACTOR);
                motionX *= speedMultiplier;
                motionY *= speedMultiplier;
                motionZ *= speedMultiplier;
            }
        }

        if (returning && shootingEntity != null && shootingEntity.isEntityAlive()) {
            if (returnDamageEnabled) checkReturnHit();
            returnToShooter();
            double dist = getDistance(shootingEntity);
            double speed = getCurrentSpeed();
            if (speed < STUCK_SPEED_THRESHOLD && dist > STUCK_DIST_THRESHOLD) {
                stuckTicks++;
                if (stuckTicks > STUCK_TIMEOUT) setDead();
            } else {
                stuckTicks = 0;
            }
        }

        if (ticksExisted > MAX_ALIVE) setDead();
    }

    private void deflectNearbyProjectiles() {
        AxisAlignedBB box = getEntityBoundingBox().grow(1.5D);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, box);
        for (Entity entity : list) {
            if (entity instanceof EntityArrow || entity instanceof EntityFireball || entity instanceof EntityThrowable || entity instanceof EntityProjectileBase) {
                entity.motionX *= DEFLECT_MULTIPLIER_XZ;
                entity.motionY *= DEFLECT_MULTIPLIER_Y;
                entity.motionZ *= DEFLECT_MULTIPLIER_XZ;
                entity.velocityChanged = true;
            }
        }
    }

    private void checkReturnHit() {
        AxisAlignedBB box = getEntityBoundingBox().grow(0.5D);
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, box);
        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase && entity != shootingEntity && !hitEntities.contains(entity.getUniqueID())) {
                ItemStack stack = tinkerProjectile.getItemStack();
                if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
                    ToolHelper.attackEntity(stack, (ToolCore) stack.getItem(), (EntityLivingBase) shootingEntity, entity, this);
                    hitEntities.add(entity.getUniqueID());
                }
            }
        }
    }

    private void returnToShooter() {
        Vec3d targetPos = getShooterTargetPos();
        double dx = targetPos.x - posX;
        double dy = targetPos.y - posY;
        double dz = targetPos.z - posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist > RETURN_DIST_THRESHOLD) {
            double returnSpeed = Math.max(BASE_RETURN_SPEED, initialSpeed * 0.6D);
            motionX = dx / dist * returnSpeed;
            motionY = dy / dist * returnSpeed;
            motionZ = dz / dist * returnSpeed;
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
        if (!world.isRemote && interactEnabled && !interactUsed && !returning && raytraceResult.sideHit != null && shootingEntity instanceof EntityPlayer) {
            BlockPos pos = raytraceResult.getBlockPos();
            IBlockState state = world.getBlockState(pos);
            if (!state.getBlock().isAir(state, world, pos)) {
                state.getBlock().onBlockActivated(world, pos, state, (EntityPlayer) shootingEntity,
                        EnumHand.MAIN_HAND, raytraceResult.sideHit,
                        (float) raytraceResult.hitVec.x - pos.getX(),
                        (float) raytraceResult.hitVec.y - pos.getY(),
                        (float) raytraceResult.hitVec.z - pos.getZ());
                interactUsed = true;
            }
        }
        if (!world.isRemote && shatterEnabled && !returning && raytraceResult.sideHit != null) {
            BlockPos pos = raytraceResult.getBlockPos();
            IBlockState state = world.getBlockState(pos);
            float hardness = state.getBlockHardness(world, pos);
            if (hardness >= 0.0F && hardness <= 2.0F) {
                world.destroyBlock(pos, true);
                setDead();
                return;
            }
        }
        returning = true;
        inGround = false;
        arrowShake = 0;
        ticksInGround = 0;
    }

    @Override
    public void onHitEntity(RayTraceResult raytraceResult) {
        Entity entityHit = raytraceResult.entityHit;
        if (deflectProjectiles && !returning) {
            if (entityHit instanceof EntityArrow || entityHit instanceof EntityFireball || entityHit instanceof EntityThrowable || entityHit instanceof EntityProjectileBase) {
                entityHit.motionX *= DEFLECT_MULTIPLIER_XZ;
                entityHit.motionY *= DEFLECT_MULTIPLIER_Y;
                entityHit.motionZ *= DEFLECT_MULTIPLIER_XZ;
                entityHit.velocityChanged = true;
                return;
            }
        }

        boolean allowRehit = bouncing && bounceCount > 0;
        if (!allowRehit && hitEntities.contains(entityHit.getUniqueID())) return;

        hitEntities.add(entityHit.getUniqueID());
        if (allowRehit && entityHit instanceof EntityLivingBase) {
            entityHit.hurtResistantTime = 0;
        }

        if (bouncing) {
            int count = initialBounceCount - bounceCount;
            float mult = Math.max(0.0F, 1.0F - 0.25F * count);
            TraitBouncing.setDamageMult(entityHit.getUniqueID(), mult);
        }

        Vec3d savedMotion = new Vec3d(motionX, motionY, motionZ);
        super.onHitEntity(raytraceResult);

        if (piercing && !returning) {
            motionX = savedMotion.x;
            motionY = savedMotion.y;
            motionZ = savedMotion.z;
        }

        ItemStack toolStack = tinkerProjectile.getItemStack();
        NBTTagCompound toolTag = TagUtil.getToolTag(toolStack);
        boolean hasFracture = TinkerUtil.hasTrait(toolTag, "fracture");

        if (!split && !returning && hasFracture && entityHit instanceof EntityLivingBase) {
            split((EntityLivingBase) entityHit);
            return;
        }

        if (piercing && !returning) {
            pierceCount--;
            inGround = false;
            arrowShake = 0;
            ticksInGround = 0;
            totalDistanceTraveled = 0.0D;
            return;
        }

        if (bouncing && bounceCount > 0) {
            EntityLivingBase nextTarget = findNextBounceTarget(entityHit);
            if (nextTarget != null) {
                bounceCount--;
                totalDistanceTraveled = 0.0D;
                redirectToTarget(nextTarget);
                this.stuckTicks = 0;
                return;
            }
        }

        returning = true;
        redirectSpeedToShooter();
    }

    private void redirectSpeedToShooter() {
        if (shootingEntity != null) {
            Vec3d targetPos = getShooterTargetPos();
            double dx = targetPos.x - posX;
            double dy = targetPos.y - posY;
            double dz = targetPos.z - posZ;
            double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 0.1D) {
                double currentSpeed = getCurrentSpeed();
                motionX = dx / dist * currentSpeed;
                motionY = dy / dist * currentSpeed;
                motionZ = dz / dist * currentSpeed;
            }
        }
        stuckTicks = 0;
        inGround = false;
        arrowShake = 0;
        ticksInGround = 0;
    }

    public EntityLivingBase findNextBounceTarget(Entity currentHit) {
        AxisAlignedBB searchBox = new AxisAlignedBB(posX - BOUNCE_SEARCH_RADIUS, posY - BOUNCE_SEARCH_HEIGHT, posZ - BOUNCE_SEARCH_RADIUS,
                posX + BOUNCE_SEARCH_RADIUS, posY + BOUNCE_SEARCH_HEIGHT, posZ + BOUNCE_SEARCH_RADIUS);
        List<EntityLivingBase> candidates = world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox,
                e -> e != shootingEntity && e.isEntityAlive() && e != currentHit &&
                        !(e instanceof EntityTameable && ((EntityTameable) e).getOwner() == shootingEntity));
        if (candidates.isEmpty()) return null;

        EntityLivingBase firstFresh = candidates.stream()
                .filter(e -> !hitEntities.contains(e.getUniqueID()))
                .min(Comparator.comparingDouble(e -> e.getDistanceSq(currentHit)))
                .orElse(null);
        if (firstFresh != null) return firstFresh;

        return candidates.stream()
                .filter(e -> hitEntities.contains(e.getUniqueID()))
                .min(Comparator.comparingDouble(e -> e.getDistanceSq(currentHit)))
                .orElse(null);
    }

    private void redirectToTarget(Entity target) {
        double dx = target.posX - posX;
        double dy = (target.posY + target.height / 2) - posY;
        double dz = target.posZ - posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist > 0.1D) {
            double speed = getCurrentSpeed();
            motionX = dx / dist * speed;
            motionY = dy / dist * speed;
            motionZ = dz / dist * speed;
        }
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
        if (piercing && pierceCount <= 0) return null;
        return super.findEntityOnPath(start, end);
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
        data.writeBoolean(returning);
        data.writeBoolean(piercing);
        data.writeInt(pierceCount);
        data.writeBoolean(bouncing);
        data.writeInt(bounceCount);
        data.writeBoolean(returnDamageEnabled);
        data.writeBoolean(split);
        data.writeBoolean(shatterEnabled);
        data.writeDouble(totalDistanceTraveled);
        data.writeDouble(initialSpeed);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        returning = data.readBoolean();
        piercing = data.readBoolean();
        pierceCount = data.readInt();
        bouncing = data.readBoolean();
        bounceCount = data.readInt();
        returnDamageEnabled = data.readBoolean();
        split = data.readBoolean();
        shatterEnabled = data.readBoolean();
        totalDistanceTraveled = data.readDouble();
        initialSpeed = data.readDouble();
    }

    @Override
    protected void playHitEntitySound() {
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }
}