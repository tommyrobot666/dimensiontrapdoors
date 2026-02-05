package lommie.dimensiontrapdoors;

import com.mojang.serialization.Lifecycle;
import lommie.dimensiontrapdoors.blockentity.ModBlockEntities;
import lommie.dimensiontrapdoors.block.ModBlocks;
import lommie.dimensiontrapdoors.item.ModItems;
import lommie.dimensiontrapdoors.trapdoorroom.BuiltInTrapdoorRoomTypes;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoomType;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionTrapdoors {
    public static final String MOD_ID = "dimensiontrapdoors";
    public static final ResourceKey<Level> TRAPDOOR_DIM =
            ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(MOD_ID,"trapdoor"));
    public static final ResourceKey<Registry<TrapdoorRoomType>> TRAPDOOR_ROOM_TYPES_KEY = ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(MOD_ID,"room_types")
    );
    public static final DefaultedMappedRegistry<TrapdoorRoomType> TRAPDOOR_ROOM_TYPES = new DefaultedMappedRegistry<>(
            MOD_ID+":empty",
            TRAPDOOR_ROOM_TYPES_KEY,
            Lifecycle.stable(),
            false
    );

    public static void init() {
        // Write common init code here.

        BuiltInTrapdoorRoomTypes.register();
        ModBlocks.BLOCKS.register();
        ModItems.ITEMS.register();
        ModBlockEntities.BLOCK_ENTITIES.register();
    }
}
