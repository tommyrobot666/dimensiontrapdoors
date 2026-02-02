package lommie.dimensiontrapdoors.trapdoorroom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record DimensionEntrypoint(int roomId, BlockPos trapdoorPos) {
    public static final Codec<DimensionEntrypoint> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.INT.fieldOf("roomId").forGetter(DimensionEntrypoint::roomId),
                    BlockPos.CODEC.fieldOf("trapdoorPos").forGetter(DimensionEntrypoint::trapdoorPos)
            ).apply(instance,DimensionEntrypoint::new));
}
