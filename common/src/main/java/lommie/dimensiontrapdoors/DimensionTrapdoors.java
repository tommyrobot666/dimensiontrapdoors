package lommie.dimensiontrapdoors;

import lommie.dimensiontrapdoors.block.ModBlocks;
import lommie.dimensiontrapdoors.item.ModItems;

public final class DimensionTrapdoors {
    public static final String MOD_ID = "dimensiontrapdoors";

    public static void init() {
        // Write common init code here.

        ModBlocks.BLOCKS.register();
        ModItems.ITEMS.register();
    }
}
