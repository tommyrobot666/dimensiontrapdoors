package lommie.dimensiontrapdoors.trapdoorroom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public record TrapdoorRoomType(BlockPos relativeSpawnPos, Identifier structure, int chunksSize) {
}
