package lommie.dimensiontrapdoors.trapdoorroom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record TrapdoorRoomType(BlockPos relativeSpawnPos, Identifier structure, int chunksSize, Optional<Identifier> biome) {
    //TODO: add codec for datadriven rooms
    //TODO: add datadriven rooms
}
