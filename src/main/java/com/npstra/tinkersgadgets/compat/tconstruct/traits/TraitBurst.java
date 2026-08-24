package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

import java.util.List;

public class TraitBurst extends AbstractTrait {
    private static final float SPLASH_RADIUS = 2.5f;
    private static final float SPLASH_DAMAGE_RATIO = 0.25f;

    public TraitBurst() {
        super("burst_heatraygun", TextFormatting.GRAY);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (!wasHit || target.world.isRemote) return;
        if (!(player instanceof EntityPlayer)) return;
        if (target.isDead) return;
        float baseDamage = ToolHelper.getActualAttack(tool) + 1.0f;
        float splashDamage = baseDamage * SPLASH_DAMAGE_RATIO;
        if (splashDamage <= 0.0f) return;
        AxisAlignedBB aabb = target.getEntityBoundingBox().grow(SPLASH_RADIUS);
        List<EntityLivingBase> nearby = target.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb,
                e -> e != target && e != player && e.isEntityAlive());
        EntityPlayer shooter = (EntityPlayer) player;
        DamageSource source = DamageSource.causePlayerDamage(shooter);
        for (EntityLivingBase entity : nearby) {
            entity.attackEntityFrom(source, splashDamage);
        }
    }
}