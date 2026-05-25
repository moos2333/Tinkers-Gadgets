package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.npstra.tinkersgadgets.Config;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ConnectorPartType;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.GripPartType;
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
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTraits;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaterialRegister {

    public static Material glass, slimeball, poppedChorus, netherQuartz, magmaCream, shulkerShell, leather, redstone, blueSlimeball, enderpearl, wool, prismarineCrystals;
    private static boolean materialsInitialized = false;
    private static Set<String> disabledMaterialSet;
    private static Set<String> disabledTraitSet;

    public static void preInit(FMLPreInitializationEvent event) {
        disabledMaterialSet = new HashSet<>(Arrays.asList(Config.disabledMaterials));
        disabledTraitSet = new HashSet<>(Arrays.asList(Config.disabledTraits));
        Material.UNKNOWN.addStats(new ConnectorMaterialStats());
        Material.UNKNOWN.addStats(new GripMaterialStats());

        if (!disabledMaterialSet.contains("glass")) {
            glass = createMaterial("glass", 0xFFFFFF, TraitsRegistry.FRACTURE, "fracture");
        }
        if (!disabledMaterialSet.contains("slimeball")) {
            slimeball = createMaterial("slimeball", 0x71ac63, null, null);
            if (!disabledTraitSet.contains("bouncing")) {
                slimeball.addTrait(TraitsRegistry.BOUNCING, ConnectorPartType.CONNECTOR);
            }
            if (!disabledTraitSet.contains("rebound_throwingknife")) {
                slimeball.addTrait(TraitsRegistry.REBOUND, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(slimeball, new GripMaterialStats());
        }
        if (!disabledMaterialSet.contains("popped_chorus")) {
            poppedChorus = createMaterial("popped_chorus", 0xb78db7, TraitsRegistry.PIERCING, "boomerang_piercing");
        }
        if (!disabledMaterialSet.contains("nether_quartz")) {
            netherQuartz = createMaterial("nether_quartz", 0xe5dfd6, TraitsRegistry.KEEN, "keen");
        }
        if (!disabledMaterialSet.contains("magma_cream")) {
            magmaCream = createMaterial("magma_cream", 0xFF8C00, TraitsRegistry.RETURN_DAMAGE, "return_damage");
        }
        if (!disabledMaterialSet.contains("shulker_shell")) {
            shulkerShell = createMaterial("shulker_shell", 0x976997, null, null);
            if (!disabledTraitSet.contains("collection_boomerang")) {
                shulkerShell.addTrait(TraitsRegistry.COLLECTION, ConnectorPartType.CONNECTOR);
            }
            if (!disabledTraitSet.contains("recovery_throwingknife")) {
                shulkerShell.addTrait(TraitsRegistry.RECOVERY, GripPartType.GRIP);
            }
            if (!disabledTraitSet.contains("enderference")) {
                shulkerShell.addTrait(TinkerTraits.enderference, ConnectorPartType.CONNECTOR);
                shulkerShell.addTrait(TinkerTraits.enderference, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(shulkerShell, new GripMaterialStats());
        }
        if (!disabledMaterialSet.contains("leather")) {
            leather = createMaterial("leather", 0xC76A43, null, null);
            TinkerRegistry.addMaterialStats(leather, new GripMaterialStats());
        }
        if (!disabledMaterialSet.contains("redstone")) {
            redstone = createMaterial("redstone", 0xCC0000, TraitsRegistry.INTERACT, "interact_boomerang");
        }
        if (!disabledMaterialSet.contains("blue_slimeball")) {
            blueSlimeball = createMaterial("blue_slimeball", 0x5BC7FF, TraitsRegistry.DEFLECT, "deflect_boomerang");
        }
        if (!disabledMaterialSet.contains("enderpearl")) {
            enderpearl = new Material("enderpearl", 0x0A6E6E, false);
            enderpearl.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("enderference")) {
                enderpearl.addTrait(TinkerTraits.enderference);
            }
            if (!disabledTraitSet.contains("instant_return_boomerang")) {
                enderpearl.addTrait(TraitsRegistry.INSTANT_RETURN);
            }
            TinkerRegistry.addMaterial(enderpearl);
            TinkerRegistry.addMaterialStats(enderpearl, new ConnectorMaterialStats());
            enderpearl.setVisible();
        }
        if (!disabledMaterialSet.contains("wool")) {
            wool = new Material("wool", 0xBFB5B5, false);
            wool.setCraftable(true).setCastable(false);
            TinkerRegistry.addMaterial(wool);
            TinkerRegistry.addMaterialStats(wool, new GripMaterialStats());
            wool.setVisible();
        }
        if (!disabledMaterialSet.contains("prismarine_crystals")) {
            prismarineCrystals = new Material("prismarine_crystals", 0x5FCDCD, false);
            prismarineCrystals.setCraftable(true).setCastable(false);
            if (!disabledTraitSet.contains("shatter_boomerang")) {
                prismarineCrystals.addTrait(TraitsRegistry.SHATTER);
            }
            TinkerRegistry.addMaterial(prismarineCrystals);
            TinkerRegistry.addMaterialStats(prismarineCrystals, new ConnectorMaterialStats());
            prismarineCrystals.setVisible();
        }
    }

    private static Material createMaterial(String id, int color, slimeknights.tconstruct.library.traits.AbstractTrait trait, String traitId) {
        Material mat = new Material(id, color, false);
        mat.setCraftable(true).setCastable(false);
        if (trait != null && (traitId == null || !disabledTraitSet.contains(traitId))) {
            mat.addTrait(trait);
        }
        TinkerRegistry.addMaterial(mat);
        TinkerRegistry.addMaterialStats(mat, new ConnectorMaterialStats());
        mat.setVisible();
        return mat;
    }

    public static void init(FMLInitializationEvent event) {
        if (materialsInitialized) return;
        materialsInitialized = true;
        setupMaterials();
        integrate(glass);
        integrate(slimeball);
        integrate(poppedChorus);
        integrate(netherQuartz);
        integrate(magmaCream);
        integrate(shulkerShell);
        integrate(leather);
        integrate(redstone);
        integrate(blueSlimeball);
        integrate(enderpearl);
        integrate(wool);
        integrate(prismarineCrystals);
    }

    private static void integrate(Material mat) {
        if (mat != null) TinkerRegistry.integrate(mat);
    }

    private static void setupMaterials() {
        setMaterialItems(glass, new ItemStack(Blocks.GLASS));
        setMaterialItems(slimeball, new ItemStack(Items.SLIME_BALL));
        setMaterialItems(poppedChorus, new ItemStack(Items.CHORUS_FRUIT_POPPED));
        setMaterialItems(netherQuartz, new ItemStack(Items.QUARTZ));
        setMaterialItems(magmaCream, new ItemStack(Items.MAGMA_CREAM));
        setMaterialItems(shulkerShell, new ItemStack(Items.SHULKER_SHELL));
        setMaterialItems(leather, new ItemStack(Items.LEATHER));
        setMaterialItems(redstone, new ItemStack(Items.REDSTONE));
        setMaterialItems(enderpearl, new ItemStack(Items.ENDER_PEARL));
        setMaterialItems(wool, new ItemStack(Blocks.WOOL));
        setMaterialItems(prismarineCrystals, new ItemStack(Items.PRISMARINE_CRYSTALS));

        if (blueSlimeball != null) {
            Item blueSlimeItem = Item.getByNameOrId("tconstruct:edible");
            ItemStack blueSlimeStack = blueSlimeItem != null ? new ItemStack(blueSlimeItem, 1, 1) : ItemStack.EMPTY;
            if (!blueSlimeStack.isEmpty()) {
                blueSlimeball.addItem(blueSlimeStack, 1, Material.VALUE_Ingot);
                blueSlimeball.setRepresentativeItem(blueSlimeStack);
            }
        }
    }

    private static void setMaterialItems(Material mat, ItemStack stack) {
        if (mat != null) {
            mat.addItem(stack, 1, Material.VALUE_Ingot);
            mat.setRepresentativeItem(stack.getItem());
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerMaterialRenderInfo() {
        setRenderColor(glass, 0xD1F2EB);
        setRenderColor(slimeball, 0x71ac63);
        setRenderColor(poppedChorus, 0xb78db7);
        setRenderColor(netherQuartz, 0xe5dfd6);
        setRenderColor(magmaCream, 0xFF8C00);
        setRenderColor(shulkerShell, 0x976997);
        setRenderColor(leather, 0xC76A43);
        setRenderColor(redstone, 0xCC0000);
        setRenderColor(blueSlimeball, 0x5BC7FF);
        setRenderColor(enderpearl, 0x0A6E6E);
        setRenderColor(wool, 0xBFB5B5);
        setRenderColor(prismarineCrystals, 0xdfe9dc);
    }

    private static void setRenderColor(Material mat, int color) {
        if (mat != null) mat.setRenderInfo(new MaterialRenderInfo.Default(color));
    }
}