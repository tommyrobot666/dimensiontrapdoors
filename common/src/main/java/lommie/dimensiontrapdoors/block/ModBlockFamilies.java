package lommie.dimensiontrapdoors.block;

import net.minecraft.data.BlockFamily;

public class ModBlockFamilies {

    @SuppressWarnings("unused")
    public final static BlockFamily DIMENSION_DARKNESS = new BlockFamily.Builder(ModBlocks.DIMENSION_DARKNESS.get())
            .getFamily();

    @SuppressWarnings("unused")
    public final static BlockFamily DIMENSION_BLOCK = new BlockFamily.Builder(ModBlocks.DIMENSION_BLOCK.get())
            .getFamily();
}
