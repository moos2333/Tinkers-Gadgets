package com.npstra.tinkersgadgets.compat.tconstruct.materials;

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
import com.npstra.tinkersgadgets.compat.tconstruct.traits.TraitsRegistry;

public class MaterialRegister {

    public static Material glass;
    public static Material slimeball;
    public static Material poppedChorus;
    public static Material netherQuartz;
    public static Material magmaCream;
    public static Material shulkerShell;
    public static Material leather;
    private static boolean materialsInitialized = false;

    public static void preInit(FMLPreInitializationEvent event) {
        Material.UNKNOWN.addStats(new ConnectorMaterialStats());

        glass = new Material("glass", 0xFFFFFF, false);
        slimeball = new Material("slimeball", 0x71ac63, false);
        poppedChorus = new Material("popped_chorus", 0xb78db7, false);
        netherQuartz = new Material("nether_quartz", 0xe5dfd6, false);
        magmaCream = new Material("magma_cream", 0xFF8C00, false);
        shulkerShell = new Material("shulker_shell", 0x976997, false);
        leather = new Material("leather", 0xC76A43, false);

        glass.setCraftable(true).setCastable(false);
        slimeball.setCraftable(true).setCastable(false);
        poppedChorus.setCraftable(true).setCastable(false);
        netherQuartz.setCraftable(true).setCastable(false);
        magmaCream.setCraftable(true).setCastable(false);
        shulkerShell.setCraftable(true).setCastable(false);
        leather.setCraftable(true).setCastable(false);

        glass.addTrait(TraitsRegistry.FRACTURE);
        slimeball.addTrait(TraitsRegistry.BOUNCING);
        poppedChorus.addTrait(TraitsRegistry.PIERCING);
        netherQuartz.addTrait(TraitsRegistry.KEEN);
        magmaCream.addTrait(TraitsRegistry.RETURN_DAMAGE);
        shulkerShell.addTrait(TraitsRegistry.COLLECTION);

        TinkerRegistry.addMaterial(glass);
        TinkerRegistry.addMaterial(slimeball);
        TinkerRegistry.addMaterial(poppedChorus);
        TinkerRegistry.addMaterial(netherQuartz);
        TinkerRegistry.addMaterial(magmaCream);
        TinkerRegistry.addMaterial(shulkerShell);
        TinkerRegistry.addMaterial(leather);

        registerMaterialStats();
    }

    public static void init(FMLInitializationEvent event) {
        if (!materialsInitialized) {
            setupMaterials();
            TinkerRegistry.integrate(glass);
            TinkerRegistry.integrate(slimeball);
            TinkerRegistry.integrate(poppedChorus);
            TinkerRegistry.integrate(netherQuartz);
            TinkerRegistry.integrate(magmaCream);
            TinkerRegistry.integrate(shulkerShell);
            TinkerRegistry.integrate(leather);
            materialsInitialized = true;
        }
    }

    private static void setupMaterials() {
        glass.addItem(new ItemStack(Item.getItemFromBlock(Blocks.GLASS)), 1, Material.VALUE_Ingot);
        glass.setRepresentativeItem(Item.getItemFromBlock(Blocks.GLASS));

        slimeball.addItem(new ItemStack(Items.SLIME_BALL), 1, Material.VALUE_Ingot);
        slimeball.setRepresentativeItem(Items.SLIME_BALL);

        poppedChorus.addItem(new ItemStack(Items.CHORUS_FRUIT_POPPED), 1, Material.VALUE_Ingot);
        poppedChorus.setRepresentativeItem(Items.CHORUS_FRUIT_POPPED);

        netherQuartz.addItem(new ItemStack(Items.QUARTZ), 1, Material.VALUE_Ingot);
        netherQuartz.setRepresentativeItem(Items.QUARTZ);

        magmaCream.addItem(new ItemStack(Items.MAGMA_CREAM), 1, Material.VALUE_Ingot);
        magmaCream.setRepresentativeItem(Items.MAGMA_CREAM);

        shulkerShell.addItem(new ItemStack(Items.SHULKER_SHELL), 1, Material.VALUE_Ingot);
        shulkerShell.setRepresentativeItem(Items.SHULKER_SHELL);

        leather.addItem(new ItemStack(Items.LEATHER), 1, Material.VALUE_Ingot);
        leather.setRepresentativeItem(Items.LEATHER);
    }

    private static void registerMaterialStats() {
        TinkerRegistry.addMaterialStats(glass, new ConnectorMaterialStats());
        TinkerRegistry.addMaterialStats(slimeball, new ConnectorMaterialStats());
        TinkerRegistry.addMaterialStats(poppedChorus, new ConnectorMaterialStats());
        TinkerRegistry.addMaterialStats(netherQuartz, new ConnectorMaterialStats());
        TinkerRegistry.addMaterialStats(magmaCream, new ConnectorMaterialStats());
        TinkerRegistry.addMaterialStats(shulkerShell, new ConnectorMaterialStats());
        TinkerRegistry.addMaterialStats(leather, new ConnectorMaterialStats());

        glass.setVisible();
        slimeball.setVisible();
        poppedChorus.setVisible();
        netherQuartz.setVisible();
        magmaCream.setVisible();
        shulkerShell.setVisible();
        leather.setVisible();
    }

    @SideOnly(Side.CLIENT)
    public static void registerMaterialRenderInfo() {
        glass.setRenderInfo(new MaterialRenderInfo.Default(0xD1F2EB));
        slimeball.setRenderInfo(new MaterialRenderInfo.Default(0x71ac63));
        poppedChorus.setRenderInfo(new MaterialRenderInfo.Default(0xb78db7));
        netherQuartz.setRenderInfo(new MaterialRenderInfo.Default(0xe5dfd6));
        magmaCream.setRenderInfo(new MaterialRenderInfo.Default(0xFF8C00));
        shulkerShell.setRenderInfo(new MaterialRenderInfo.Default(0x976997));
        leather.setRenderInfo(new MaterialRenderInfo.Default(0xC76A43));
    }
}