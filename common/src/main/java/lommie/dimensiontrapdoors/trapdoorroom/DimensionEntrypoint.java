package lommie.dimensiontrapdoors.trapdoorroom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.Optional;

public record DimensionEntrypoint(int roomId, BlockPos trapdoorPos, Optional<Integer> fromRoomId) {
    public static final Codec<DimensionEntrypoint> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.INT.fieldOf("roomId").forGetter(DimensionEntrypoint::roomId),
                    BlockPos.CODEC.fieldOf("trapdoorPos").forGetter(DimensionEntrypoint::trapdoorPos),
                    Codec.INT.optionalFieldOf("fromRoomId").forGetter(DimensionEntrypoint::fromRoomId)
            ).apply(instance,DimensionEntrypoint::new));
}
