package com.npstra.tinkersgadgets.compat.tconstruct.tools;

import com.npstra.tinkersgadgets.TinkersGadgets;
import com.npstra.tinkersgadgets.Config;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerang;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityBoomerangShard;
import com.npstra.tinkersgadgets.compat.tconstruct.entity.EntityThrowingKnife;
import com.npstra.tinkersgadgets.compat.tconstruct.parts.ItemConnector;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.TinkerTools;

@Mod.EventBusSubscriber
public class GadgetsRegister {
    public static ToolCore boomerang;
    public static ToolPart connector;
    public static ToolCore throwingKnife;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        if (Config.enableBoomerang) {
            connector = new ItemConnector();
            event.getRegistry().register(connector);
            TinkerRegistry.registerToolPart(connector);
            TinkerRegistry.registerStencilTableCrafting(Pattern.setTagForPart(new ItemStack(TinkerTools.pattern), connector));

            boomerang = new Boomerang();
            event.getRegistry().register(boomerang);
            TinkerRegistry.registerToolCrafting(boomerang);

            throwingKnife = new ThrowingKnife();
            event.getRegistry().register(throwingKnife);
            TinkerRegistry.registerToolCrafting(throwingKnife);
        }
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry entry = EntityEntryBuilder.create()
                .entity(EntityBoomerang.class)
                .id(new ResourceLocation(TinkersGadgets.MOD_ID, "boomerang"), 101)
                .name("boomerang")
                .tracker(64, 1, true)
                .build();
        event.getRegistry().register(entry);
        EntityRegistry.registerModEntity(new ResourceLocation("tinkersgadgets:boomerang_shard"), EntityBoomerangShard.class, "boomerang_shard", 102, TinkersGadgets.instance, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation("tinkersgadgets:throwing_knife"), EntityThrowingKnife.class, "throwing_knife", 103, TinkersGadgets.instance, 64, 1, true);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        if (Config.enableBoomerang) {
            if (boomerang != null) {
                ModelRegisterUtil.registerToolModel(boomerang);
            }
            if (connector != null) {
                ModelRegisterUtil.registerPartModel(connector);
            }
            if (throwingKnife != null) {
                ModelRegisterUtil.registerToolModel(throwingKnife);
            }
        }
    }
}