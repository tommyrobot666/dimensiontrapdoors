package lommie.dimensiontrapdoors.trapdoorroom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record TrapdoorRoomType(BlockPos relativeSpawnPos, Identifier structure, int chunksSize, Optional<Identifier> biome) {
}
