package com.npstra.tinkersgadgets.compat.tconstruct.materials;

import com.npstra.tinkersgadgets.Config;
import com.npstra.tinkersgadgets.compat.tconstruct.traits.TraitsRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.tools.TinkerTraits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.npstra.tinkersgadgets.compat.tconstruct.parts.ChainPartType.CHAIN;
import static com.npstra.tinkersgadgets.compat.tconstruct.parts.ConnectorPartType.CONNECTOR;
import static com.npstra.tinkersgadgets.compat.tconstruct.parts.FuelTankPartType.FUEL_TANK;
import static com.npstra.tinkersgadgets.compat.tconstruct.parts.GripPartType.GRIP;
import static com.npstra.tinkersgadgets.compat.tconstruct.parts.GunBarrelPartType.GUN_BARREL;
import static com.npstra.tinkersgadgets.compat.tconstruct.parts.HeatRayEmitterPartType.HEAT_RAY_EMITTER;

public class MaterialRegister {

    private static final MaterialDef[] MATERIALS = {
            new MaterialDef("glass", 0xD1F2EB, new ItemStack(Blocks.GLASS), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new FuelTankMaterialStats(4000, 10, 0.8f))
                    .stat(new GunBarrelMaterialStats(-3, 1.1f, 0))
                    .trait(TraitsRegistry.FRACTURE, "fracture_boomerang", CONNECTOR)
                    .trait(TraitsRegistry.FOCUS, "focus_heatraygun", FUEL_TANK),

            new MaterialDef("popped_chorus", 0xb78db7, new ItemStack(Items.CHORUS_FRUIT_POPPED), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new HeatRayEmitterMaterialStats(1.5f, 0.95f))
                    .stat(new ChainMaterialStats(2, 2.0f, 0, 0.0f, 0.04f))
                    .stat(new GunBarrelMaterialStats(0, 1.1f, 0))
                    .trait(TraitsRegistry.PIERCING, "boomerang_piercing", CONNECTOR)
                    .trait(TinkerTraits.enderference, "enderference", CONNECTOR, GRIP, HEAT_RAY_EMITTER, CHAIN, GUN_BARREL)
                    .trait(TraitsRegistry.ECHO, "echo_throwingknife", GRIP, HEAT_RAY_EMITTER, CHAIN, GUN_BARREL),

            new MaterialDef("nether_quartz", 0xe5dfd6, new ItemStack(Items.QUARTZ), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(20000, 10, 1.0f))
                    .stat(new ChainMaterialStats(1, 1.0f, 0, 0.0f, 0.03f))
                    .stat(new GunBarrelMaterialStats(-2, 1.0f, 0))
                    .trait(TraitsRegistry.TRAUMA, "trauma_throwingknife"),

            new MaterialDef("shulker_shell", 0x976997, new ItemStack(Items.SHULKER_SHELL), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(4000, 20, 1.0f))
                    .stat(new GunBarrelMaterialStats(2, 1.2f, 0))
                    .stat(new ChainMaterialStats(1, 3.0f, 0, 0.0f, 0.04f))
                    .trait(TraitsRegistry.COLLECTION, "collection_boomerang", CONNECTOR)
                    .trait(TraitsRegistry.SNIPING, "sniping_throwingknife", CONNECTOR, GUN_BARREL, GRIP, FUEL_TANK, CHAIN)
                    .trait(TinkerTraits.enderference, "enderference", CONNECTOR, GRIP, FUEL_TANK, GUN_BARREL, CHAIN),

            new MaterialDef("leather", 0xC76A43, new ItemStack(Items.LEATHER), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new ChainMaterialStats(2, 2.0f, 0, -0.25f, 0.0f)),

            new MaterialDef("redstone", 0xCC0000, new ItemStack(Items.REDSTONE), Material.VALUE_Shard)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(8000, 8, 1.0f))
                    .stat(new HeatRayEmitterMaterialStats(0.5f, 0.5f))
                    .stat(new ChainMaterialStats(1, 1.5f, 0, 1.0f, -0.01f))
                    .stat(new GunBarrelMaterialStats(-1, 0.9f, 0))
                    .trait(TraitsRegistry.INTERACT, "interact_boomerang")
                    .trait(TraitsRegistry.INTERACT, "interact_boomerang", CONNECTOR)
                    .trait(TraitsRegistry.PULSE, "pulse_throwingknife", GRIP, HEAT_RAY_EMITTER, CHAIN, CONNECTOR, GUN_BARREL)
                    .trait(TraitsRegistry.BURST, "burst_heatraygun", FUEL_TANK, GUN_BARREL),

            new MaterialDef("enderpearl", 0x0A6E6E, new ItemStack(Items.ENDER_PEARL), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .trait(TinkerTraits.enderference, "enderference", CONNECTOR, GRIP)
                    .trait(TraitsRegistry.BURST, "burst_heatraygun", CONNECTOR)
                    .trait(TraitsRegistry.GUIDANCE, "guidance_throwingknife", GRIP),

            new MaterialDef("wool", 0xBFB5B5, new ItemStack(Blocks.WOOL), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new ChainMaterialStats(3, 2.5f, 0, -0.35f, 0.0f)),

            new MaterialDef("prismarine_crystals", 0x5FCDCD, new ItemStack(Items.PRISMARINE_CRYSTALS), Material.VALUE_Ingot)
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(5000, 8, 0.95f))
                    .stat(new GunBarrelMaterialStats(0, 1.05f, 0))
                    .stat(new HeatRayEmitterMaterialStats(1.5f, 0.9f))
                    .trait(TraitsRegistry.SHATTER, "shatter_boomerang", CONNECTOR)
                    .trait(TraitsRegistry.CONDENSATION, "condensation_heatraygun", FUEL_TANK)
                    .trait(TraitsRegistry.VORTEX, "vortex_throwingknife", GRIP, GUN_BARREL, CONNECTOR, HEAT_RAY_EMITTER),

            new MaterialDef("brick", 0xb75a40, new ItemStack(Items.BRICK), Material.VALUE_Ingot)
                    .stat(new FuelTankMaterialStats(5000, 8, 0.9f))
                    .stat(new GunBarrelMaterialStats(-1, 0.85f, 0))
                    .trait(TraitsRegistry.TEMPER, "temper_heatraygun", FUEL_TANK),

            new MaterialDef("netherbrick", 0x49282e, new ItemStack(Items.NETHERBRICK), Material.VALUE_Ingot)
                    .stat(new FuelTankMaterialStats(10000, 15, 1.0f))
                    .stat(new GunBarrelMaterialStats(0, 0.8f, 0))
                    .trait(TraitsRegistry.PRESSURIZED, "pressurized_heatraygun", FUEL_TANK, GUN_BARREL),
    };

    private static final MaterialDef[] PATCHES = {
            new MaterialDef("slime")
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(3000, 18, 0.7f))
                    .stat(new ChainMaterialStats(5, 3.0f, 35, 0.0f, 0.0f))
                    .stat(new GunBarrelMaterialStats(3, 0.8f, 350))
                    .trait(TraitsRegistry.BOUNCING, "bouncing_boomerang", CONNECTOR)
                    .trait(TraitsRegistry.REBOUND, "rebound_throwingknife", GRIP)
                    .trait(TraitsRegistry.CONDUCTION, "conduction_heatraygun", FUEL_TANK, CHAIN, GUN_BARREL),

            new MaterialDef("blueslime")
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(2000, 12, 1.3f))
                    .stat(new ChainMaterialStats(4, 5.0f, 20, 0.0f, 0.0f))
                    .stat(new GunBarrelMaterialStats(4, 0.75f, 200))
                    .trait(TraitsRegistry.DEFLECT, "deflect_boomerang", CONNECTOR)
                    .trait(TraitsRegistry.REBOUND, "rebound_throwingknife", GRIP)
                    .trait(TraitsRegistry.CONDUCTION, "conduction_heatraygun", FUEL_TANK, CHAIN, GUN_BARREL),

            new MaterialDef("magmaslime")
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new ChainMaterialStats(3, 3.0f, 15, 0.0f, 0.05f))
                    .stat(new HeatRayEmitterMaterialStats(2.0f, 1.5f))
                    .stat(new GunBarrelMaterialStats(1, 1.5f, 150))
                    .trait(TraitsRegistry.BOUNCING, "bouncing_boomerang", CONNECTOR)
                    .trait(TraitsRegistry.CONDUCTION, "conduction_heatraygun", CONNECTOR, GRIP)
                    .trait(TraitsRegistry.REBOUND, "rebound_throwingknife", GRIP)
                    .trait(TraitsRegistry.KINETIC, "kinetic_boomerang", CHAIN),

            new MaterialDef("iron")
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(10000, 10, 1.0f))
                    .stat(new ChainMaterialStats(3, 4.0f, 10, 0.0f, 0.0f))
                    .stat(new HeatRayEmitterMaterialStats(1.5f, 1.0f))
                    .stat(new GunBarrelMaterialStats(0, 1.0f, 100))
                    .trait(TraitsRegistry.INERTIA, "inertia_throwingknife", GRIP, CONNECTOR)
                    .trait(TraitsRegistry.SEASONED, "seasoned_chainblade", CHAIN, GUN_BARREL),

            new MaterialDef("stone")
                    .stat(new FuelTankMaterialStats(1, 1, 0.01f))
                    .stat(new ChainMaterialStats(1, 1.0f, 0, -0.5f, -0.05f))
                    .stat(new GunBarrelMaterialStats(-3, 0.5f, 0))
                    .stat(new HeatRayEmitterMaterialStats(3600.0f, 0.001f)),

            new MaterialDef("cobalt")
                    .stat(new ConnectorMaterialStats())
                    .stat(new GripMaterialStats())
                    .stat(new FuelTankMaterialStats(3000, 12, 1.5f))
                    .stat(new ChainMaterialStats(4, 3.0f, 30, 0.75f, 0.01f))
                    .stat(new HeatRayEmitterMaterialStats(0.75f, 0.75f))
                    .stat(new GunBarrelMaterialStats(1, 1.15f, 300))
                    .trait(TraitsRegistry.KINETIC, "kinetic_boomerang", CONNECTOR, GRIP, GUN_BARREL)
                    .trait(TraitsRegistry.EBB, "ebb_chainblade", CHAIN),
    };

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
        Material.UNKNOWN.addStats(ChainMaterialStats.UNKNOWN);
        Material.UNKNOWN.addStats(GunBarrelMaterialStats.UNKNOWN);

        for (MaterialDef def : MATERIALS) {
            apply(def);
        }
        for (MaterialDef def : PATCHES) {
            apply(def);
        }
    }

    public static void init(FMLInitializationEvent event) {
        if (materialsInitialized) {
            return;
        }
        materialsInitialized = true;

        for (MaterialDef def : MATERIALS) {
            if (def.material == null) {
                continue;
            }
            def.material.addItem(def.item, 1, def.value);
            def.material.setRepresentativeItem(def.item.getItem());
            TinkerRegistry.integrate(def.material);
        }
    }

    private static void apply(MaterialDef def) {
        Material mat;
        if (def.isPatch()) {
            mat = TinkerRegistry.getMaterial(def.id);
            if (mat == Material.UNKNOWN) {
                return;
            }
        } else {
            if (!isMaterialEnabled(def.id)) {
                return;
            }
            mat = new Material(def.id, def.color, false);
            mat.setCraftable(true).setCastable(false);
            TinkerRegistry.addMaterial(mat);
        }

        for (TraitDef trait : def.traits) {
            if (!isTraitEnabled(trait.configKey)) {
                continue;
            }
            if (trait.partTypes.length == 0) {
                mat.addTrait(trait.trait);
            } else {
                for (String partType : trait.partTypes) {
                    mat.addTrait(trait.trait, partType);
                }
            }
        }

        for (IMaterialStats stats : def.stats) {
            if (def.isPatch() && mat.getStats(stats.getIdentifier()) != null) {
                continue;
            }
            TinkerRegistry.addMaterialStats(mat, stats);
        }

        if (!def.isPatch()) {
            mat.setVisible();
            def.material = mat;
        }
    }

    private static final class MaterialDef {
        final String id;
        final int color;
        final ItemStack item;
        final int value;
        final List<IMaterialStats> stats = new ArrayList<>();
        final List<TraitDef> traits = new ArrayList<>();
        Material material;

        MaterialDef(String id, int color, ItemStack item, int value) {
            this.id = id;
            this.color = color;
            this.item = item;
            this.value = value;
        }

        MaterialDef(String id) {
            this(id, 0, null, 0);
        }

        boolean isPatch() {
            return item == null;
        }

        MaterialDef stat(IMaterialStats stats) {
            this.stats.add(stats);
            return this;
        }

        MaterialDef trait(ITrait trait, String configKey, String... partTypes) {
            this.traits.add(new TraitDef(trait, configKey, partTypes));
            return this;
        }
    }

    private static final class TraitDef {
        final ITrait trait;
        final String configKey;
        final String[] partTypes;

        TraitDef(ITrait trait, String configKey, String... partTypes) {
            this.trait = trait;
            this.configKey = configKey;
            this.partTypes = partTypes;
        }
    }
}
