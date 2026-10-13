package com.npstra.tinkersgadgets;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import java.io.File;

public class Config {

    public static boolean enableBoomerang = true;
    public static boolean enableThrowingKnife = true;
    public static boolean enableHeatRayGun = true;
    public static boolean enableChainBlade = true;
    public static boolean enableCapacity = true;
    public static boolean enableRapid = true;
    public static boolean enablePower = true;
    public static boolean enableEfficiency = true;
    public static boolean enablePistolSword = true;
    public static String[] disabledMaterials = new String[0];
    public static String[] disabledTraits = new String[0];

    private static Configuration config;

    public static void preInit(FMLPreInitializationEvent event) {
        File file = new File(event.getModConfigurationDirectory(), "tinkersgadgets.cfg");
        config = new Configuration(file);
        sync();
    }

    private static void sync() {
        String general = "general";

        enableBoomerang = config.getBoolean("enableBoomerang", general, true,
                "Enable the Boomerang tool and its Connector part.");
        enableThrowingKnife = config.getBoolean("enableThrowingKnife", general, true,
                "Enable the Throwing Knife tool and its Grip part.");
        enableHeatRayGun = config.getBoolean("enableHeatRayGun", general, true,
                "Enable the Heat Ray Gun tool and its parts.");
        enableChainBlade = config.getBoolean("enableChainBlade", general, true,
                "Enable the Chain Blade tool and its Chain part.");
        enablePistolSword = config.getBoolean("enablePistolSword", general, true,
                "Enable the Pistol Sword tool and its Gun Barrel part.");

        enableCapacity = config.getBoolean("enableCapacity", general, true,
                "Enable the Capacity modifier (Heat Ray Gun, Pistol Sword, Chain Blade, Boomerang).");
        enableRapid = config.getBoolean("enableRapid", general, true,
                "Enable the Rapid modifier (Heat Ray Gun, Chain Blade, Throwing Knife, Boomerang).");
        enablePower = config.getBoolean("enablePower", general, true,
                "Enable the Power modifier (Heat Ray Gun, Chain Blade, Pistol Sword).");
        enableEfficiency = config.getBoolean("enableEfficiency", general, true,
                "Enable the Efficiency modifier (Heat Ray Gun, Chain Blade, Boomerang).");

        disabledMaterials = config.getStringList("disabledMaterials", "materials",
                new String[0],
                "List of material IDs to disable. "
                        + "Examples: glass, slimeball, nether_quartz, redstone, prismarine_crystals. "
                        + "See the guide book for the full list.");

        disabledTraits = config.getStringList("disabledTraits", "traits",
                new String[0],
                "List of trait IDs to prevent materials from obtaining. "
                        + "Examples: fracture_boomerang, bouncing_boomerang, vortex_throwingknife, "
                        + "ebb_chainblade, seasoned_chainblade, pressurized_heatraygun. "
                        + "See the guide book for the full list.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}