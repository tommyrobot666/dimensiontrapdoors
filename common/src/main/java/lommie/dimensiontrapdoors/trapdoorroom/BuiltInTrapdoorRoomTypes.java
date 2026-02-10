package lommie.dimensiontrapdoors.trapdoorroom;

import lommie.dimensiontrapdoors.DimensionTrapdoors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public class BuiltInTrapdoorRoomTypes {
    static void registerType(String name, BlockPos spawnPos, int chunks, Identifier biome){
        Identifier id = Identifier.fromNamespaceAndPath(DimensionTrapdoors.MOD_ID,name);
        DimensionTrapdoors.TRAPDOOR_ROOM_TYPES.register(
                ResourceKey.create(DimensionTrapdoors.TRAPDOOR_ROOM_TYPES_KEY, id),
                new TrapdoorRoomType(spawnPos, id, chunks, biome != null ? Optional.of(biome) : Optional.empty()),
                RegistrationInfo.BUILT_IN
        );
    }

    static void registerType(String name, BlockPos spawnPos, int chunks){
        registerType(name,spawnPos,chunks,null);
    }

    public static void register(){
        registerType("empty",new BlockPos(8,1,8),1);
        registerType("hallway",new BlockPos(8,1,1),3);
    }
}
