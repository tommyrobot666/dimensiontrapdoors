package lommie.dimensiontrapdoors.block;

import net.minecraft.data.BlockFamily;

public class ModBlockFamilies {

    public final static BlockFamily DIMENSION_DARKNESS = new BlockFamily.Builder(ModBlocks.DIMENSION_DARKNESS.get())
            .getFamily();

    public final static BlockFamily DIMENSION_BLOCK = new BlockFamily.Builder(ModBlocks.DIMENSION_BLOCK.get())
            .getFamily();
}
