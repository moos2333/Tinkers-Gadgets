package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.ILauncher;
import slimeknights.tconstruct.library.tools.ranged.IProjectile;
import slimeknights.tconstruct.library.utils.ToolHelper;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerangShard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TraitReturnDamage extends ProjectileModifierTrait implements IBoomerangTrait {

    private static final UUID POWER_MODIFIER_UUID =
            UUID.fromString("c6aefc21-081a-4c4a-b076-8f9d6cef9122");
    private static final double RETURN_HIT_RADIUS = 0.5D;

    public TraitReturnDamage() {
        super("return_damage", 0xFF4500);
        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onBoomerangShardCreated(EntityBoomerang boomerang, EntityBoomerangShard shard) {
        shard.setReturnDamageEnabled(true);
    }

    @Override
    public void onBoomerangUpdate(EntityBoomerang boomerang, World world) {
        if (!boomerang.isReturning()) return;
        EntityLivingBase attacker = boomerang.getShooterLiving();
        if (attacker == null) return;
        ItemStack stack = boomerang.tinkerProjectile.getItemStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof ToolCore)) return;

        AxisAlignedBB box = boomerang.getEntityBoundingBox().grow(RETURN_HIT_RADIUS);
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(boomerang, box);
        List<EntityLivingBase> targets = new ArrayList<>(entities.size());
        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase && entity != attacker && !boomerang.hasHitEntity(entity)) {
                targets.add((EntityLivingBase) entity);
            }
        }
        if (targets.isEmpty()) return;

        ItemStack mainhand = attacker.getHeldItemMainhand();
        ItemStack offhand = attacker.getHeldItemOffhand();
        Multimap<String, AttributeModifier> mainhandMods = mainhand.isEmpty() ? null : mainhand.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        Multimap<String, AttributeModifier> offhandMods = offhand.isEmpty() ? null : offhand.getAttributeModifiers(EntityEquipmentSlot.OFFHAND);
        if (mainhandMods != null) attacker.getAttributeMap().removeAttributeModifiers(mainhandMods);
        if (offhandMods != null) attacker.getAttributeMap().removeAttributeModifiers(offhandMods);

        Multimap<String, AttributeModifier> projectileMods;
        if (stack.getItem() instanceof IProjectile) {
            projectileMods = ((IProjectile) stack.getItem()).getProjectileAttributeModifier(stack);
        } else {
            projectileMods = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        }
        ItemStack launcher = boomerang.tinkerProjectile.getLaunchingStack();
        if (launcher.getItem() instanceof ILauncher) {
            ((ILauncher) launcher.getItem()).modifyProjectileAttributes(projectileMods, launcher, stack, boomerang.tinkerProjectile.getPower());
        }
        projectileMods.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(POWER_MODIFIER_UUID, "Weapon damage multiplier",
                        (double) (boomerang.tinkerProjectile.getPower() - 1.0F), 2));
        attacker.getAttributeMap().applyAttributeModifiers(projectileMods);

        try {
            for (EntityLivingBase target : targets) {
                ToolHelper.attackEntity(stack, (ToolCore) stack.getItem(), attacker, target, boomerang);
                boomerang.markHitEntity(target);
            }
        } finally {
            attacker.getAttributeMap().removeAttributeModifiers(projectileMods);
            if (mainhandMods != null) attacker.getAttributeMap().applyAttributeModifiers(mainhandMods);
            if (offhandMods != null) attacker.getAttributeMap().applyAttributeModifiers(offhandMods);
        }
    }
}