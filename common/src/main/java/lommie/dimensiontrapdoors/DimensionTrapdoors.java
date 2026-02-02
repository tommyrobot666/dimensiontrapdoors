package lommie.dimensiontrapdoors;

import lommie.dimensiontrapdoors.blockentity.ModBlockEntities;
import lommie.dimensiontrapdoors.block.ModBlocks;
import lommie.dimensiontrapdoors.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionTrapdoors {
    public static final String MOD_ID = "dimensiontrapdoors";
    public static final ResourceKey<Level> TRAPDOOR_DIM =
            ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(MOD_ID,"trapdoor"));

    public static void init() {
        // Write common init code here.

        ModBlocks.BLOCKS.register();
        ModItems.ITEMS.register();
        ModBlockEntities.BLOCK_ENTITIES.register();
    }
}
