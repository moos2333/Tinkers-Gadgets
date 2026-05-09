package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.npstra.tinkersgadgets.Config;
import com.npstra.tinkersgadgets.compat.tconstruct.traits.TraitsRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaterialRegister {

    public static Material glass;
    public static Material slimeball;
    public static Material poppedChorus;
    public static Material netherQuartz;
    public static Material magmaCream;
    public static Material shulkerShell;
    public static Material leather;
    public static Material redstone;
    public static Material blueSlimeball;
    private static boolean materialsInitialized = false;

    private static Set<String> disabledMaterialSet;
    private static Set<String> disabledTraitSet;

    public static void preInit(FMLPreInitializationEvent event) {
        disabledMaterialSet = new HashSet<>(Arrays.asList(Config.disabledMaterials));
        disabledTraitSet = new HashSet<>(Arrays.asList(Config.disabledTraits));

        Material.UNKNOWN.addStats(new ConnectorMaterialStats());

        if (!disabledMaterialSet.contains("glass")) {
            glass = new Material("glass", 0xFFFFFF, false);
            glass.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("fracture")) glass.addTrait(TraitsRegistry.FRACTURE);
            TinkerRegistry.addMaterial(glass);
            TinkerRegistry.addMaterialStats(glass, new ConnectorMaterialStats());
            glass.setVisible();
        }

        if (!disabledMaterialSet.contains("slimeball")) {
            slimeball = new Material("slimeball", 0x71ac63, false);
            slimeball.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("bouncing")) slimeball.addTrait(TraitsRegistry.BOUNCING);
            TinkerRegistry.addMaterial(slimeball);
            TinkerRegistry.addMaterialStats(slimeball, new ConnectorMaterialStats());
            slimeball.setVisible();
        }

        if (!disabledMaterialSet.contains("popped_chorus")) {
            poppedChorus = new Material("popped_chorus", 0xb78db7, false);
            poppedChorus.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("boomerang_piercing")) poppedChorus.addTrait(TraitsRegistry.PIERCING);
            TinkerRegistry.addMaterial(poppedChorus);
            TinkerRegistry.addMaterialStats(poppedChorus, new ConnectorMaterialStats());
            poppedChorus.setVisible();
        }

        if (!disabledMaterialSet.contains("nether_quartz")) {
            netherQuartz = new Material("nether_quartz", 0xe5dfd6, false);
            netherQuartz.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("keen")) netherQuartz.addTrait(TraitsRegistry.KEEN);
            TinkerRegistry.addMaterial(netherQuartz);
            TinkerRegistry.addMaterialStats(netherQuartz, new ConnectorMaterialStats());
            netherQuartz.setVisible();
        }

        if (!disabledMaterialSet.contains("magma_cream")) {
            magmaCream = new Material("magma_cream", 0xFF8C00, false);
            magmaCream.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("return_damage")) magmaCream.addTrait(TraitsRegistry.RETURN_DAMAGE);
            TinkerRegistry.addMaterial(magmaCream);
            TinkerRegistry.addMaterialStats(magmaCream, new ConnectorMaterialStats());
            magmaCream.setVisible();
        }

        if (!disabledMaterialSet.contains("shulker_shell")) {
            shulkerShell = new Material("shulker_shell", 0x976997, false);
            shulkerShell.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("collection_boomerang")) shulkerShell.addTrait(TraitsRegistry.COLLECTION);
            TinkerRegistry.addMaterial(shulkerShell);
            TinkerRegistry.addMaterialStats(shulkerShell, new ConnectorMaterialStats());
            shulkerShell.setVisible();
        }

        if (!disabledMaterialSet.contains("leather")) {
            leather = new Material("leather", 0xC76A43, false);
            leather.setCraftable(true).setCastable(false);
            TinkerRegistry.addMaterial(leather);
            TinkerRegistry.addMaterialStats(leather, new ConnectorMaterialStats());
            leather.setVisible();
        }

        if (!disabledMaterialSet.contains("redstone")) {
            redstone = new Material("redstone", 0xCC0000, false);
            redstone.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("interact_boomerang")) redstone.addTrait(TraitsRegistry.INTERACT);
            TinkerRegistry.addMaterial(redstone);
            TinkerRegistry.addMaterialStats(redstone, new ConnectorMaterialStats());
            redstone.setVisible();
        }

        if (!disabledMaterialSet.contains("blue_slimeball")) {
            blueSlimeball = new Material("blue_slimeball", 0x74c8c5, false);
            blueSlimeball.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("deflect_boomerang")) blueSlimeball.addTrait(TraitsRegistry.DEFLECT);
            TinkerRegistry.addMaterial(blueSlimeball);
            TinkerRegistry.addMaterialStats(blueSlimeball, new ConnectorMaterialStats());
            blueSlimeball.setVisible();
        }
    }

    public static void init(FMLInitializationEvent event) {
        if (!materialsInitialized) {
            setupMaterials();
            if (glass != null) TinkerRegistry.integrate(glass);
            if (slimeball != null) TinkerRegistry.integrate(slimeball);
            if (poppedChorus != null) TinkerRegistry.integrate(poppedChorus);
            if (netherQuartz != null) TinkerRegistry.integrate(netherQuartz);
            if (magmaCream != null) TinkerRegistry.integrate(magmaCream);
            if (shulkerShell != null) TinkerRegistry.integrate(shulkerShell);
            if (leather != null) TinkerRegistry.integrate(leather);
            if (redstone != null) TinkerRegistry.integrate(redstone);
            if (blueSlimeball != null) TinkerRegistry.integrate(blueSlimeball);
            materialsInitialized = true;
        }
    }

    private static void setupMaterials() {
        if (glass != null) {
            glass.addItem(new ItemStack(Item.getItemFromBlock(Blocks.GLASS)), 1, Material.VALUE_Ingot);
            glass.setRepresentativeItem(Item.getItemFromBlock(Blocks.GLASS));
        }
        if (slimeball != null) {
            slimeball.addItem(new ItemStack(Items.SLIME_BALL), 1, Material.VALUE_Ingot);
            slimeball.setRepresentativeItem(Items.SLIME_BALL);
        }
        if (poppedChorus != null) {
            poppedChorus.addItem(new ItemStack(Items.CHORUS_FRUIT_POPPED), 1, Material.VALUE_Ingot);
            poppedChorus.setRepresentativeItem(Items.CHORUS_FRUIT_POPPED);
        }
        if (netherQuartz != null) {
            netherQuartz.addItem(new ItemStack(Items.QUARTZ), 1, Material.VALUE_Ingot);
            netherQuartz.setRepresentativeItem(Items.QUARTZ);
        }
        if (magmaCream != null) {
            magmaCream.addItem(new ItemStack(Items.MAGMA_CREAM), 1, Material.VALUE_Ingot);
            magmaCream.setRepresentativeItem(Items.MAGMA_CREAM);
        }
        if (shulkerShell != null) {
            shulkerShell.addItem(new ItemStack(Items.SHULKER_SHELL), 1, Material.VALUE_Ingot);
            shulkerShell.setRepresentativeItem(Items.SHULKER_SHELL);
        }
        if (leather != null) {
            leather.addItem(new ItemStack(Items.LEATHER), 1, Material.VALUE_Ingot);
            leather.setRepresentativeItem(Items.LEATHER);
        }
        if (redstone != null) {
            redstone.addItem(new ItemStack(Items.REDSTONE), 1, Material.VALUE_Ingot);
            redstone.setRepresentativeItem(Items.REDSTONE);
        }
        if (blueSlimeball != null) {
            blueSlimeball.addItem(new ItemStack(TinkerCommons.matSlimeBallBlue.getItem()), 1, Material.VALUE_Ingot);
            blueSlimeball.setRepresentativeItem(TinkerCommons.matSlimeBallBlue);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerMaterialRenderInfo() {
        if (glass != null) glass.setRenderInfo(new MaterialRenderInfo.Default(0xD1F2EB));
        if (slimeball != null) slimeball.setRenderInfo(new MaterialRenderInfo.Default(0x71ac63));
        if (poppedChorus != null) poppedChorus.setRenderInfo(new MaterialRenderInfo.Default(0xb78db7));
        if (netherQuartz != null) netherQuartz.setRenderInfo(new MaterialRenderInfo.Default(0xe5dfd6));
        if (magmaCream != null) magmaCream.setRenderInfo(new MaterialRenderInfo.Default(0xFF8C00));
        if (shulkerShell != null) shulkerShell.setRenderInfo(new MaterialRenderInfo.Default(0x976997));
        if (leather != null) leather.setRenderInfo(new MaterialRenderInfo.Default(0xC76A43));
        if (redstone != null) redstone.setRenderInfo(new MaterialRenderInfo.Default(0xCC0000));
        if (blueSlimeball != null) blueSlimeball.setRenderInfo(new MaterialRenderInfo.Default(0x5BC7FF));
    }
}