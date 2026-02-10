package lommie.dimensiontrapdoors.trapdoorroom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.Optional;

// x, y are trapdoorPos within TrapdoorRoomRegion
public record TrapdoorRoomInfo(BlockPos relativeSpawnPos, int x, int y, Identifier structure, Optional<Identifier> biome, int roomRegionId) {
    public static final Codec<TrapdoorRoomInfo> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockPos.CODEC.fieldOf("relativeSpawnPos").forGetter(TrapdoorRoomInfo::relativeSpawnPos),
                    Codec.INT.fieldOf("x").forGetter(TrapdoorRoomInfo::x),
                    Codec.INT.fieldOf("y").forGetter(TrapdoorRoomInfo::y),
                    Identifier.CODEC.fieldOf("structure").forGetter(TrapdoorRoomInfo::structure),
                    Identifier.CODEC.optionalFieldOf("biome").forGetter(TrapdoorRoomInfo::biome),
                    Codec.INT.fieldOf("roomRegionId").forGetter(TrapdoorRoomInfo::roomRegionId)
            ).apply(instance,TrapdoorRoomInfo::new));

    public static TrapdoorRoomInfo fromType(TrapdoorRoomType trapdoorRoomType, int x, int y, int roomRegionId) {
        return new TrapdoorRoomInfo(trapdoorRoomType.relativeSpawnPos(),x,y,trapdoorRoomType.structure(),trapdoorRoomType.biome(),roomRegionId);
    }
}
