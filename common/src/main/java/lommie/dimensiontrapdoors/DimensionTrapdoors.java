package lommie.dimensiontrapdoors;

import com.mojang.serialization.Lifecycle;
import dev.architectury.event.events.common.LifecycleEvent;
import lommie.dimensiontrapdoors.blockentity.ModBlockEntities;
import lommie.dimensiontrapdoors.block.ModBlocks;
import lommie.dimensiontrapdoors.item.ModItems;
import lommie.dimensiontrapdoors.saveddata.TrapdoorRoomsSavedData;
import lommie.dimensiontrapdoors.trapdoorroom.BuiltInTrapdoorRoomTypes;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoomType;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

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

        LifecycleEvent.SERVER_STARTED.register(server -> {
            ServerLevel trapdoorDim = Objects.requireNonNull(server.getLevel(TRAPDOOR_DIM));
            migrateDataStoredInTrapdoorDimToOverworld(server, trapdoorDim);
        });

        BuiltInTrapdoorRoomTypes.register();
        ModBlocks.BLOCKS.register();
        ModItems.ITEMS.register();
        ModBlockEntities.BLOCK_ENTITIES.register();
    }

    private static void migrateDataStoredInTrapdoorDimToOverworld(MinecraftServer server, ServerLevel trapdoorDim) {
        if (trapdoorDim.getDataStorage().get(TrapdoorRoomsSavedData.TYPE) != null){
            server.overworld().getDataStorage().set(
                    TrapdoorRoomsSavedData.TYPE,
                    trapdoorDim.getDataStorage().get(TrapdoorRoomsSavedData.TYPE)
                            .mergeFiles(TrapdoorRoomsSavedData.getFromLevel(trapdoorDim))
            );
        }
    }
}
