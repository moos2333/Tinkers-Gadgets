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

/**
 * The addon's own guide book.
 *
 * <p>It is a completely separate {@link BookData} instance with its own resource repository, so
 * Tinkers' Construct's "Materials and You" is never touched, extended or re-transformed. Nothing is
 * loaded until the book is opened for the first time: registering the book only puts an entry into
 * Mantle's reload-aware registry, and the JSON/resources are parsed lazily by
 * {@link BookData#openGui(ItemStack)}. That keeps the constant overhead at zero.</p>
 */
@SideOnly(Side.CLIENT)
public final class GadgetsBook {

    /** Must contain a namespace so Mantle never guesses the owning mod container. */
    public static final String BOOK_ID = TinkersGadgets.MOD_ID + ":gadget_book";
    /** Resource root, resolved as assets/tinkersgadgets/book/... */
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

        // Tinkers' pages title themselves through book.fontRenderer; without it every addTitle() would
        // NPE. Reuse the (already registered, unicode-capable) renderer of Materials and You.
        FontRenderer font = TinkerBook.INSTANCE.fontRenderer;
        data.fontRenderer = font != null ? font : Minecraft.getMinecraft().fontRenderer;

        book = data;
    }

    public static void open(ItemStack stack) {
        get().openGui(stack);
    }
}
