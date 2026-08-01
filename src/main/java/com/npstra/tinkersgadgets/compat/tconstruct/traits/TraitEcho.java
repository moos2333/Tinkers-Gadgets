package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitEcho extends AbstractTrait {
    private static final String TAG_LAST_TARGET_ID = "echo_last_target_id";
    private static final String TAG_STACKS = "echo_stacks";
    private static final int MAX_STACKS = 5;
    private static final float ECHO_DAMAGE_RATIO = 0.2f;

    public TraitEcho() {
        super("echo_throwingknife", TextFormatting.LIGHT_PURPLE);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (!wasHit || target.world.isRemote) return;
        if (!(player instanceof EntityPlayer)) return;
        if (tool.isEmpty()) return;

        float baseDamage = ToolHelper.getActualAttack(tool);
        World world = target.world;
        NBTTagCompound tag = TagUtil.getTagSafe(tool);
        int stacks = tag.getInteger(TAG_STACKS);
        int lastId = tag.getInteger(TAG_LAST_TARGET_ID);
        int currentId = target.getEntityId();
        boolean sameTarget = lastId != 0 && lastId == currentId;

        if (sameTarget) {
            if (stacks > 0) stacks--;
        } else {
            if (lastId != 0) {
                if (stacks < MAX_STACKS) stacks++;
                if (stacks > 0) {
                    Entity entity = world.getEntityByID(lastId);
                    if (entity instanceof EntityLivingBase && entity.isEntityAlive()) {
                        EntityLivingBase previous = (EntityLivingBase) entity;
                        float extra = baseDamage * stacks * ECHO_DAMAGE_RATIO;
                        if (extra > 0.001f) {
                            int oldHurt = previous.hurtResistantTime;
                            float oldLast = previous.lastDamage;
                            previous.hurtResistantTime = 0;
                            previous.lastDamage = 0;
                            previous.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), extra);
                            previous.hurtResistantTime = Math.max(oldHurt, previous.hurtResistantTime);
                            previous.lastDamage = Math.max(oldLast, previous.lastDamage);
                        }
                    }
                }
            }
        }

        tag.setInteger(TAG_LAST_TARGET_ID, currentId);
        tag.setInteger(TAG_STACKS, stacks);
        tool.setTagCompound(tag);
    }
}