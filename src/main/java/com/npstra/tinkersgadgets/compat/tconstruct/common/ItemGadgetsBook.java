package com.npstra.tinkersgadgets.compat.tconstruct.common;

import com.npstra.tinkersgadgets.TinkersGadgets;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.TinkerRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * The guide book of this addon. It is deliberately a plain item: everything book related lives in
 * client-only code, handed off through the proxy, so the dedicated server never touches Mantle's
 * book GUI classes.
 */
public class ItemGadgetsBook extends Item {

    public ItemGadgetsBook() {
        setRegistryName(TinkersGadgets.MOD_ID, "gadget_book");
        setTranslationKey(TinkersGadgets.MOD_ID + ".gadget_book");
        setMaxStackSize(1);
        setCreativeTab(TinkerRegistry.tabGeneral);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            TinkersGadgets.proxy.openGuideBook(itemStack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String key = getTranslationKey() + ".tooltip";
        if (I18n.canTranslate(key)) {
            tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + LocUtils.translateRecursive(key)));
        }
    }
}
