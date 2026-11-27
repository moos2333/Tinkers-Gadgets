package com.npstra.tinkersgadgets.compat.tconstruct.book;

import com.google.common.collect.Lists;
import com.npstra.tinkersgadgets.TinkersGadgets;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.book.content.ContentSingleStatMultMaterial;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class GadgetsBookTransformer extends BookTransformer {

    private static final String PKG = "com.npstra.tinkersgadgets.compat.tconstruct";

    private static final String LINE_BREAK = "\\\\n|\\r?\\n";

    private static final List<String> PART_STAT_ORDER = Arrays.asList(
            "connector", "grip", "fuel_tank", "heat_ray_emitter", "chain", "gun_barrel");

    @Override
    public void transform(BookData book) {
        buildTools(book);
        buildModifiers(book);
        buildMaterials(book);
    }

    private void buildTools(BookData book) {
        SectionData section = findSection(book, "tools");
        if (section == null) {
            return;
        }

        List<ToolCore> tools = TinkerRegistry.getTools().stream()
                .filter(GadgetsBookTransformer::isOurs)
                .sorted(Comparator.comparing(ToolCore::getIdentifier))
                .collect(Collectors.toList());
        if (tools.isEmpty()) {
            return;
        }

        ContentListing listing = new ContentListing();
        listing.title = book.translate("tools");
        addPage(section, "tools_index", listing);

        for (ToolCore tool : tools) {
            ContentTool content = new ContentTool(tool);
            content.text = text(book, "tools." + tool.getIdentifier() + ".text");
            content.properties = partProperties(tool);

            PageData page = addPage(section, tool.getIdentifier(), content);
            listing.addEntry(tool.getLocalizedName(), page);
        }
    }

    
    private String[] partProperties(ToolCore tool) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (PartMaterialType component : tool.getToolBuildComponents()) {
            Set<IToolPart> parts = component.getPossibleParts();
            if (parts.isEmpty()) {
                continue;
            }
            IToolPart part = parts.iterator().next();
            String name = partName(part);
            counts.merge(name, 1, Integer::sum);
        }
        return counts.entrySet().stream()
                .map(e -> e.getKey() + " \u00d7" + e.getValue())
                .toArray(String[]::new);
    }

    private String partName(IToolPart part) {
        if (part instanceof Item) {
            String key = ((Item) part).getTranslationKey() + ".name";
            String localized = I18n.format(key);
            if (!localized.equals(key)) {
                return localized;
            }
        }
        ItemStack stack = part.getItemstackWithMaterial(Material.UNKNOWN);
        return stack.isEmpty() ? "" : stack.getDisplayName();
    }

    private void buildModifiers(BookData book) {
        SectionData section = findSection(book, "modifiers");
        if (section == null) {
            return;
        }

        List<IModifier> modifiers = TinkerRegistry.getAllModifiers().stream()
                .filter(m -> m.getClass().getName().startsWith(PKG + ".modifiers"))
                .sorted(Comparator.comparing(IModifier::getIdentifier))
                .collect(Collectors.toList());
        if (modifiers.isEmpty()) {
            return;
        }

        ContentListing listing = new ContentListing();
        listing.title = book.translate("modifiers");
        addPage(section, "modifiers_index", listing);

        String[] demoTools = TinkerRegistry.getTools().stream()
                .filter(GadgetsBookTransformer::isOurs)
                .map(tool -> tool.getRegistryName().toString())
                .toArray(String[]::new);

        for (IModifier modifier : modifiers) {
            ContentModifier content = new ContentModifier(modifier);

            List<String> lines = descLines(modifier);
            content.text = prose(book, "modifiers." + modifier.getIdentifier() + ".text",
                    lines.isEmpty() ? null : lines.get(0));
            content.effects = bullets(book, "modifiers." + modifier.getIdentifier() + ".effects",
                    lines.size() > 1 ? lines.subList(1, lines.size()) : Lists.newArrayList());
            if (demoTools.length > 0) {
                content.demoTool = demoTools;
            }

            PageData page = addPage(section, modifier.getIdentifier(), content);
            listing.addEntry(modifier.getLocalizedName(), page);
        }
    }

    private void buildMaterials(BookData book) {
        SectionData section = findSection(book, "materials");
        if (section == null) {
            return;
        }

        ContentListing listing = new ContentListing();
        listing.title = book.translate("materials");
        boolean listingUsed = false;

        for (String stat : PART_STAT_ORDER) {
            IMaterialStats template = Material.UNKNOWN.getStats(stat);
            if (template == null) {
                continue;
            }

            List<Material> materials = TinkerRegistry.getAllMaterials().stream()
                    .filter(m -> !m.isHidden())
                    .filter(Material::hasItems)
                    .filter(m -> m.hasStats(stat) && isUsable(stat, m))
                    .sorted(Comparator.comparing(Material::getLocalizedName))
                    .collect(Collectors.toList());
            if (materials.isEmpty()) {
                continue;
            }

            if (!listingUsed) {
                addPage(section, "materials_index", listing);
                listingUsed = true;
            }

            String partName = book.strings.containsKey("materials." + stat)
                    ? book.translate("materials." + stat)
                    : template.getLocalizedName();

            int pagesBefore = section.pages.size();
            List<ContentPageIconList> overviews =
                    ContentPageIconList.getPagesNeededForItemCount(materials.size(), section, stat);
            List<PageData> overviewPages = new ArrayList<>(
                    section.pages.subList(pagesBefore, section.pages.size()));
            for (int i = 0; i < overviews.size() && i < overviewPages.size(); i++) {
                overviews.get(i).title = partName;
                PageData overviewPage = overviewPages.get(i);
                overviewPage.name = stat + "_overview_" + i;
                if (i == 0) {
                    listing.addEntry(partName, overviewPage);
                }
            }

            Map<Material, PageData> pages = new LinkedHashMap<>();
            int index = 0;
            for (List<Material> group : Lists.partition(materials, 3)) {
                ContentSingleStatMultMaterial content = new ContentSingleStatMultMaterial(group, stat);
                PageData page = addPage(section, stat + "_" + index++, content);
                for (Material material : group) {
                    pages.put(material, page);
                }
            }

            int overviewIndex = 0;
            for (Material material : materials) {
                SizedBookElement icon;
                if (!material.getRepresentativeItem().isEmpty()) {
                    icon = new ElementItem(0, 0, 1f, material.getRepresentativeItem());
                } else {
                    icon = new ElementImage(ImageData.MISSING);
                }

                while (overviewIndex < overviews.size()
                        && !overviews.get(overviewIndex).addLink(
                                icon, material.getLocalizedNameColored(), pages.get(material))) {
                    overviewIndex++;
                }
            }
        }
    }

    private static boolean isOurs(ToolCore tool) {
        return tool.getRegistryName() != null
                && TinkersGadgets.MOD_ID.equals(tool.getRegistryName().getNamespace());
    }

    
    private static boolean isUsable(String stat, Material material) {
        for (IToolPart part : TinkerRegistry.getToolParts()) {
            if (part.hasUseForStat(stat) && part.canUseMaterial(material)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> descLines(IModifier modifier) {
        String desc = modifier.getLocalizedDesc();
        if (desc == null || desc.isEmpty()) {
            return Lists.newArrayList();
        }
        return Arrays.stream(desc.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    private static TextData[] text(BookData book, String key) {
        String raw = book.strings.get(key);
        if (raw == null || raw.isEmpty()) {
            return new TextData[0];
        }
        return lines(raw.split(LINE_BREAK));
    }

    private static TextData[] prose(BookData book, String key, String fallback) {
        TextData[] written = text(book, key);
        if (written.length > 0) {
            return written;
        }
        if (fallback == null || fallback.isEmpty()) {
            return new TextData[0];
        }
        return lines(fallback.split("\n"));
    }

    private static String[] bullets(BookData book, String key, List<String> fallback) {
        String raw = book.strings.get(key);
        if (raw != null && !raw.isEmpty()) {
            List<String> written = new ArrayList<>();
            for (String line : raw.split(LINE_BREAK)) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    written.add(trimmed);
                }
            }
            if (!written.isEmpty()) {
                return written.toArray(new String[0]);
            }
        }
        return fallback == null ? new String[0] : fallback.toArray(new String[0]);
    }

    private static TextData[] lines(String[] source) {
        List<TextData> result = new ArrayList<>();
        for (String line : source) {
            if (!result.isEmpty()) {
                result.add(new TextData("\n"));
            }
            result.add(new TextData(line));
        }
        return result.toArray(new TextData[0]);
    }

    private static SectionData findSection(BookData book, String name) {
        for (SectionData section : book.sections) {
            if (name.equals(section.name)) {
                return section;
            }
        }
        return null;
    }

    private static PageData addPage(SectionData section, String name, PageContent content) {
        PageData page = new PageData(true);
        page.source = section.source;
        page.parent = section;
        page.name = name;
        page.content = content;
        page.load();
        section.pages.add(page);
        return page;
    }
}
