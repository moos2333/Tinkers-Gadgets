package com.npstra.tinkersgadgets.compat.tconstruct.book;

import com.npstra.tinkersgadgets.TinkersGadgets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tconstruct.library.book.TinkerBook;

@SideOnly(Side.CLIENT)
public final class GadgetsBook {

    public static final String BOOK_ID = TinkersGadgets.MOD_ID + ":gadget_book";
    public static final String BOOK_RESOURCE = TinkersGadgets.MOD_ID + ":book";

    private static BookData book;

    private GadgetsBook() {
    }

    public static BookData get() {
        if (book == null) {
            init();
        }
        return book;
    }

    private static void init() {
        if (book != null) {
            return;
        }

        BookData data = BookLoader.registerBook(BOOK_ID, true, false);
        data.addRepository(new FileRepository(BOOK_RESOURCE));
        data.addTransformer(new GadgetsBookTransformer());

        FontRenderer font = TinkerBook.INSTANCE.fontRenderer;
        data.fontRenderer = font != null ? font : Minecraft.getMinecraft().fontRenderer;

        book = data;
    }

    public static void open(ItemStack stack) {
        get().openGui(stack);
    }
}
