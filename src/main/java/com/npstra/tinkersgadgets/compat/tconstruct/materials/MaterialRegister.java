package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.npstra.tinkersgadgets.Config;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ConnectorPartType;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.FuelTankPartType;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.GripPartType;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.HeatRayEmitterPartType;
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
import slimeknights.tconstruct.tools.TinkerTraits;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaterialRegister {

    public static Material glass, slimeball, poppedChorus, netherQuartz, magmaCream, shulkerShell, leather, redstone, blueSlimeball, enderpearl, wool, prismarineCrystals;
    public static Material brick, netherbrick;
    private static boolean materialsInitialized = false;
    private static Set<String> disabledMaterialSet;
    private static Set<String> disabledTraitSet;

    private static boolean isMaterialEnabled(String id) {
        return !disabledMaterialSet.contains(id);
    }

    private static boolean isTraitEnabled(String id) {
        return !disabledTraitSet.contains(id);
    }

    public static void preInit(FMLPreInitializationEvent event) {
        disabledMaterialSet = new HashSet<>(Arrays.asList(Config.disabledMaterials));
        disabledTraitSet = new HashSet<>(Arrays.asList(Config.disabledTraits));
        Material.UNKNOWN.addStats(new ConnectorMaterialStats());
        Material.UNKNOWN.addStats(new GripMaterialStats());
        Material.UNKNOWN.addStats(new FuelTankMaterialStats(0, 0, 1.0f));
        Material.UNKNOWN.addStats(new HeatRayEmitterMaterialStats(1.5f, 1.0f));

        if (isMaterialEnabled("glass")) {
            glass = createMaterial("glass", 0xFFFFFF, TraitsRegistry.FRACTURE, "fracture");
        }
        if (isMaterialEnabled("slimeball")) {
            slimeball = createMaterial("slimeball", 0x71ac63, null, null);
            if (isTraitEnabled("bouncing")) {
                slimeball.addTrait(TraitsRegistry.BOUNCING, ConnectorPartType.CONNECTOR);
            }
            if (isTraitEnabled("rebound_throwingknife")) {
                slimeball.addTrait(TraitsRegistry.REBOUND, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(slimeball, new GripMaterialStats());
        }
        if (isMaterialEnabled("popped_chorus")) {
            poppedChorus = createMaterial("popped_chorus", 0xb78db7, TraitsRegistry.PIERCING, "boomerang_piercing");
            if (isTraitEnabled("echo_throwingknife")) {
                poppedChorus.addTrait(TraitsRegistry.ECHO, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(poppedChorus, new GripMaterialStats());
        }
        if (isMaterialEnabled("nether_quartz")) {
            netherQuartz = createMaterial("nether_quartz", 0xe5dfd6, TraitsRegistry.KEEN, "keen");
            if (isTraitEnabled("keen")) {
                netherQuartz.addTrait(TraitsRegistry.KEEN, ConnectorPartType.CONNECTOR);
            }
            if (isTraitEnabled("trauma_throwingknife")) {
                netherQuartz.addTrait(TraitsRegistry.TRAUMA, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(netherQuartz, new GripMaterialStats());
        }
        if (isMaterialEnabled("magma_cream")) {
            magmaCream = createMaterial("magma_cream", 0xFF8C00, TraitsRegistry.RETURN_DAMAGE, "return_damage");
        }
        if (isMaterialEnabled("shulker_shell")) {
            shulkerShell = createMaterial("shulker_shell", 0x976997, null, null);
            if (isTraitEnabled("collection_boomerang")) {
                shulkerShell.addTrait(TraitsRegistry.COLLECTION, ConnectorPartType.CONNECTOR);
            }
            if (isTraitEnabled("recovery_throwingknife")) {
                shulkerShell.addTrait(TraitsRegistry.RECOVERY, GripPartType.GRIP);
            }
            if (isTraitEnabled("enderference")) {
                shulkerShell.addTrait(TinkerTraits.enderference, ConnectorPartType.CONNECTOR);
                shulkerShell.addTrait(TinkerTraits.enderference, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(shulkerShell, new GripMaterialStats());
            if (shulkerShell.getStats(FuelTankPartType.FUEL_TANK) == null) {
                shulkerShell.addStats(new FuelTankMaterialStats(4000, 20, 1.1f));
            }
        }
        if (isMaterialEnabled("leather")) {
            leather = createMaterial("leather", 0xC76A43, null, null);
            TinkerRegistry.addMaterialStats(leather, new GripMaterialStats());
        }
        if (isMaterialEnabled("redstone")) {
            redstone = createMaterial("redstone", 0xCC0000, TraitsRegistry.INTERACT, "interact_boomerang");
            if (isTraitEnabled("pulse_throwingknife")) {
                redstone.addTrait(TraitsRegistry.PULSE, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(redstone, new GripMaterialStats());
        }
        if (isMaterialEnabled("blue_slimeball")) {
            blueSlimeball = createMaterial("blue_slimeball", 0x5BC7FF, null, null);
            if (isTraitEnabled("deflect_boomerang")) {
                blueSlimeball.addTrait(TraitsRegistry.DEFLECT, ConnectorPartType.CONNECTOR);
            }
            if (isTraitEnabled("rebound_throwingknife")) {
                blueSlimeball.addTrait(TraitsRegistry.REBOUND, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterialStats(blueSlimeball, new GripMaterialStats());
        }
        if (isMaterialEnabled("enderpearl")) {
            enderpearl = new Material("enderpearl", 0x0A6E6E, false);
            enderpearl.setCraftable(true).setCastable(false);
            if (isTraitEnabled("enderference")) {
                enderpearl.addTrait(TinkerTraits.enderference, ConnectorPartType.CONNECTOR);
                enderpearl.addTrait(TinkerTraits.enderference, GripPartType.GRIP);
            }
            if (isTraitEnabled("instant_return_boomerang")) {
                enderpearl.addTrait(TraitsRegistry.INSTANT_RETURN, ConnectorPartType.CONNECTOR);
            }
            if (isTraitEnabled("guidance_throwingknife")) {
                enderpearl.addTrait(TraitsRegistry.GUIDANCE, GripPartType.GRIP);
            }
            TinkerRegistry.addMaterial(enderpearl);
            TinkerRegistry.addMaterialStats(enderpearl, new ConnectorMaterialStats());
            TinkerRegistry.addMaterialStats(enderpearl, new GripMaterialStats());
            enderpearl.setVisible();
        }
        if (isMaterialEnabled("wool")) {
            wool = new Material("wool", 0xBFB5B5, false);
            wool.setCraftable(true).setCastable(false);
            TinkerRegistry.addMaterial(wool);
            TinkerRegistry.addMaterialStats(wool, new GripMaterialStats());
            wool.setVisible();
        }
        if (isMaterialEnabled("prismarine_crystals")) {
            prismarineCrystals = new Material("prismarine_crystals", 0x5FCDCD, false);
            prismarineCrystals.setCraftable(true).setCastable(false);
            if (isTraitEnabled("shatter_boomerang")) {
                prismarineCrystals.addTrait(TraitsRegistry.SHATTER);
            }
            TinkerRegistry.addMaterial(prismarineCrystals);
            TinkerRegistry.addMaterialStats(prismarineCrystals, new ConnectorMaterialStats());
            prismarineCrystals.setVisible();
        }
        if (isMaterialEnabled("brick")) {
            brick = new Material("brick", 0xb75a40, false);
            brick.setCraftable(true).setCastable(false);
            TinkerRegistry.addMaterial(brick);
            TinkerRegistry.addMaterialStats(brick, new FuelTankMaterialStats(5000, 8, 0.9f));
            brick.setVisible();
        }
        if (isMaterialEnabled("netherbrick")) {
            netherbrick = new Material("netherbrick", 0x49282e, false);
            netherbrick.setCraftable(true).setCastable(false);
            TinkerRegistry.addMaterial(netherbrick);
            TinkerRegistry.addMaterialStats(netherbrick, new FuelTankMaterialStats(10000, 15, 1.0f));
            netherbrick.setVisible();
        }
        Material iron = TinkerRegistry.getMaterial("iron");
        if (iron != Material.UNKNOWN && iron.getStats(HeatRayEmitterPartType.HEAT_RAY_EMITTER) == null) {
            iron.addStats(new HeatRayEmitterMaterialStats(1.5f, 1.0f));
        }
        if (iron != Material.UNKNOWN && iron.getStats(FuelTankPartType.FUEL_TANK) == null) {
            iron.addStats(new FuelTankMaterialStats(10000, 10, 1.0f));
        }
        if (iron != Material.UNKNOWN) {
            if (iron.getStats(GripPartType.GRIP) == null) {
                TinkerRegistry.addMaterialStats(iron, new GripMaterialStats());
            }
            if (isTraitEnabled("inertia_throwingknife")) {
                iron.addTrait(TraitsRegistry.INERTIA, GripPartType.GRIP);
            }
        }
        Material stone = TinkerRegistry.getMaterial("stone");
        if (stone != Material.UNKNOWN && stone.getStats(HeatRayEmitterPartType.HEAT_RAY_EMITTER) == null) {
            stone.addStats(new HeatRayEmitterMaterialStats(3600.0f, 0.001f));
        }
        if (stone != Material.UNKNOWN && stone.getStats(FuelTankPartType.FUEL_TANK) == null) {
            stone.addStats(new FuelTankMaterialStats(1, 1, 0.01f));
        }
        Material cobalt = TinkerRegistry.getMaterial("cobalt");
        if (cobalt != Material.UNKNOWN && cobalt.getStats(HeatRayEmitterPartType.HEAT_RAY_EMITTER) == null) {
            cobalt.addStats(new HeatRayEmitterMaterialStats(0.75f, 0.75f));
        }
        if (cobalt != Material.UNKNOWN && cobalt.getStats(FuelTankPartType.FUEL_TANK) == null) {
            cobalt.addStats(new FuelTankMaterialStats(3000, 12, 1.5f));
        }
        Material magmaslime = TinkerRegistry.getMaterial("magmaslime");
        if (magmaslime != Material.UNKNOWN && magmaslime.getStats(HeatRayEmitterPartType.HEAT_RAY_EMITTER) == null) {
            magmaslime.addStats(new HeatRayEmitterMaterialStats(2.0f, 1.5f));
        }
    }

    private static Material createMaterial(String id, int color, slimeknights.tconstruct.library.traits.AbstractTrait trait, String traitId) {
        Material mat = new Material(id, color, false);
        mat.setCraftable(true).setCastable(false);
        if (trait != null && (traitId == null || isTraitEnabled(traitId))) {
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
        integrate(brick);
        integrate(netherbrick);
    }

    private static void integrate(Material mat) {
        if (mat != null) TinkerRegistry.integrate(mat);
    }

    private static void registerMaterial(Material mat, ItemStack stack, int value) {
        if (mat != null) {
            mat.addItem(stack, 1, value);
            mat.setRepresentativeItem(stack.getItem());
        }
    }

    private static void setupMaterials() {
        registerMaterial(glass, new ItemStack(Blocks.GLASS), Material.VALUE_Ingot);
        registerMaterial(slimeball, new ItemStack(Items.SLIME_BALL), 36);
        registerMaterial(poppedChorus, new ItemStack(Items.CHORUS_FRUIT_POPPED), Material.VALUE_Ingot);
        registerMaterial(netherQuartz, new ItemStack(Items.QUARTZ), Material.VALUE_Ingot);
        registerMaterial(magmaCream, new ItemStack(Items.MAGMA_CREAM), Material.VALUE_Ingot);
        registerMaterial(shulkerShell, new ItemStack(Items.SHULKER_SHELL), Material.VALUE_Ingot);
        registerMaterial(leather, new ItemStack(Items.LEATHER), Material.VALUE_Ingot);
        registerMaterial(redstone, new ItemStack(Items.REDSTONE), Material.VALUE_Shard);
        registerMaterial(enderpearl, new ItemStack(Items.ENDER_PEARL), Material.VALUE_Ingot);
        registerMaterial(wool, new ItemStack(Blocks.WOOL), Material.VALUE_Ingot);
        registerMaterial(prismarineCrystals, new ItemStack(Items.PRISMARINE_CRYSTALS), Material.VALUE_Ingot);
        registerMaterial(brick, new ItemStack(Items.BRICK), Material.VALUE_Ingot);
        registerMaterial(netherbrick, new ItemStack(Items.NETHERBRICK), Material.VALUE_Ingot);

        if (blueSlimeball != null) {
            Item blueSlimeItem = Item.getByNameOrId("tconstruct:edible");
            ItemStack blueSlimeStack = blueSlimeItem != null ? new ItemStack(blueSlimeItem, 1, 1) : ItemStack.EMPTY;
            if (!blueSlimeStack.isEmpty()) {
                registerMaterial(blueSlimeball, blueSlimeStack, 36);
            }
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
        setRenderColor(wool, 0xF3E9E3);
        setRenderColor(prismarineCrystals, 0xdfe9dc);
        setRenderColor(brick, 0xb75a40);
        setRenderColor(netherbrick, 0x49282e);
    }

    private static void setRenderColor(Material mat, int color) {
        if (mat != null) mat.setRenderInfo(new MaterialRenderInfo.Default(color));
    }
}