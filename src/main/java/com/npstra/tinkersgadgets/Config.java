package com.npstra.tinkersgadgets;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import java.io.File;

public class Config {

    public static boolean enableBoomerang = true;
    public static boolean enableThrowingKnife = true;
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

        disabledMaterials = config.getStringList("disabledMaterials", "materials",
                new String[0],
                "List of material IDs to disable. Valid IDs: glass, slimeball, popped_chorus, nether_quartz, magma_cream, shulker_shell, leather, redstone, blue_slimeball, enderpearl, wool.");

        disabledTraits = config.getStringList("disabledTraits", "traits",
                new String[0],
                "List of trait IDs to prevent materials from obtaining. Valid IDs: fracture, bouncing, boomerang_piercing, keen, return_damage, collection_boomerang, interact_boomerang, deflect_boomerang, enderference, instant_return_boomerang, rebound_throwingknife.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}