package com.npstra.tinkersgadgets.compat.tconstruct.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
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
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityBoomerang extends EntityProjectileBase {

    private static final int MAX_ALIVE = 600;
    private static final double BASE_RETURN_SPEED = 0.45D;
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
    private boolean returnDamageEnabled;
    private final Set<UUID> hitEntities = new HashSet<>();

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
    public void setBounceCount(int count) { this.bounceCount = count; }
    public void setReturnDamageEnabled(boolean enabled) { this.returnDamageEnabled = enabled; }
    public boolean hasSplit() { return split; }
    public void setToolId(String id) { this.toolId = id; }

    public void split(EntityLivingBase target) {
        if (split || world.isRemote) return;
        split = true;
        ItemStack toolStack = tinkerProjectile.getItemStack();
        if (toolStack.isEmpty() || !(toolStack.getItem() instanceof ToolCore)) {
            setDead();
            return;
        }
        NBTTagCompound root = TagUtil.getTagSafe(toolStack);
        NBTTagCompound tinkerData = root.getCompoundTag("TinkerData");
        NBTTagList materialsTagList = tinkerData.getTagList("Materials", 8);
        if (materialsTagList.tagCount() == 0) {
            materialsTagList = TagUtil.getBaseMaterialsTagList(root);
        }
        List<String> materialIds = new java.util.ArrayList<>();
        for (int i = 0; i < materialsTagList.tagCount(); i++) {
            String id = materialsTagList.getStringTagAt(i);
            if (id != null && !id.isEmpty()) materialIds.add(id);
        }
        if (materialIds.size() < 3) {
            NBTTagCompound toolTag = TagUtil.getToolTag(root);
            String mat0 = toolTag.getString("Material0");
            String mat1 = toolTag.getString("Material1");
            String mat2 = toolTag.getString("Material2");
            if (!mat0.isEmpty() && !mat1.isEmpty() && !mat2.isEmpty()) {
                materialIds.clear();
                materialIds.add(mat0);
                materialIds.add(mat1);
                materialIds.add(mat2);
            }
        }
        while (materialIds.size() < 3) {
            materialIds.add("wood");
        }
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
        double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) * 0.7D;
        double angle = 22.5D * Math.PI / 180.0D;
        Vec3d right = forward.rotateYaw((float) angle);
        Vec3d left = forward.rotateYaw((float) -angle);
        Vec3d[] directions = new Vec3d[]{right, left};
        Vec3d spawnPos = target.getPositionVector().add(forward.scale(1.5D)).add(0, target.height * 0.5, 0);
        float damage = (float) (ToolHelper.getActualDamage(toolStack, (EntityLivingBase) shootingEntity) * 0.5);
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

        if (!world.isRemote) {
            double baseMaxDistance = 16.0D;
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

            if (returning && shootingEntity != null && shootingEntity.isEntityAlive()) {
                if (returnDamageEnabled) checkReturnHit();
                returnToShooter();
                double dist = getDistance(shootingEntity);
                if (Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) < 0.1D && dist > 2.0D) {
                    stuckTicks++;
                    if (stuckTicks > 60) {
                        setDead();
                    }
                } else {
                    stuckTicks = 0;
                }
            }

            if (ticksExisted > MAX_ALIVE) {
                setDead();
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
        Vec3d targetPos = new Vec3d(shootingEntity.posX, shootingEntity.posY + shootingEntity.getEyeHeight(), shootingEntity.posZ);
        double dx = targetPos.x - posX;
        double dy = targetPos.y - posY;
        double dz = targetPos.z - posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist > 1.2D) {
            motionX = dx / dist * BASE_RETURN_SPEED;
            motionY = dy / dist * BASE_RETURN_SPEED;
            motionZ = dz / dist * BASE_RETURN_SPEED;
        } else {
            if (shootingEntity instanceof EntityPlayer) onCollideWithPlayer((EntityPlayer) shootingEntity);
            setDead();
        }
    }

    @Override
    public void onHitBlock(RayTraceResult raytraceResult) {
        returning = true;
        Vec3d motion = new Vec3d(motionX, motionY, motionZ);
        if (motion.lengthSquared() > 0.0D) {
            motion = motion.normalize();
            Vec3d normal = new Vec3d(raytraceResult.sideHit.getDirectionVec());
            double dot = motion.dotProduct(normal);
            if (dot > 0.0D) motion = normal.scale(2.0D * dot).subtract(motion);
            motionX = -motion.x * 0.8D;
            motionY = -motion.y * 0.8D;
            motionZ = -motion.z * 0.8D;
        }
        inGround = false;
        arrowShake = 0;
        ticksInGround = 0;
    }

    @Override
    public void onHitEntity(RayTraceResult raytraceResult) {
        Entity entityHit = raytraceResult.entityHit;
        if (hitEntities.contains(entityHit.getUniqueID())) return;
        hitEntities.add(entityHit.getUniqueID());
        super.onHitEntity(raytraceResult);

        ItemStack toolStack = tinkerProjectile.getItemStack();
        NBTTagCompound toolTag = TagUtil.getToolTag(toolStack);
        boolean hasFracture = TinkerUtil.hasTrait(toolTag, "fracture");

        if (!split && !returning && hasFracture && entityHit instanceof EntityLivingBase) {
            split((EntityLivingBase) entityHit);
            return;
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
            if (Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) < 0.2D) {
                returning = true;
            }
            if (pierceCount <= 0) returning = true;
            return;
        }

        if (bouncing && bounceCount > 0) {
            Entity nextTarget = findNextTarget();
            if (nextTarget != null) {
                bounceCount--;
                redirectToTarget(nextTarget);
                return;
            }
        }

        returning = true;
        Vec3d motion = new Vec3d(motionX, motionY, motionZ);
        if (motion.lengthSquared() > 0.0D) {
            motion = motion.normalize();
            motionX = -motion.x * 0.7D;
            motionY = -motion.y * 0.7D;
            motionZ = -motion.z * 0.7D;
        }
        inGround = false;
        arrowShake = 0;
        ticksInGround = 0;
    }

    private Entity findNextTarget() {
        AxisAlignedBB searchBox = new AxisAlignedBB(posX - 9, posY - 3, posZ - 9, posX + 9, posY + 3, posZ + 9);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox,
                e -> e != shootingEntity && !hitEntities.contains(e.getUniqueID()) && e.isEntityAlive() && canEntityBeSeen(e));
        return entities.isEmpty() ? null : entities.get(0);
    }

    private boolean canEntityBeSeen(Entity target) {
        Vec3d start = new Vec3d(posX, posY + height / 2, posZ);
        Vec3d end = new Vec3d(target.posX, target.posY + target.height / 2, target.posZ);
        RayTraceResult result = world.rayTraceBlocks(start, end, false, true, false);
        return result == null || result.typeOfHit != RayTraceResult.Type.BLOCK;
    }

    private void redirectToTarget(Entity target) {
        double dx = target.posX - posX;
        double dy = (target.posY + target.height / 2) - posY;
        double dz = target.posZ - posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist > 0.1D) {
            double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
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
        return returning ? null : super.findEntityOnPath(start, end);
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
        totalDistanceTraveled = data.readDouble();
        initialSpeed = data.readDouble();
    }

    @Override
    protected void playHitEntitySound() {
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }
}