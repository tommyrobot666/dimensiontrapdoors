package lommie.dimensiontrapdoors.trapdoorroom;

import net.minecraft.core.BlockPos;

public record TrapdoorRoom(TrapdoorRoomInfo in, TrapdoorRoomRegion roomRegion, int roomId) {
    public BlockPos globalSpawnPos(){
        return roomRegion.origin().offset(in.relativeSpawnPos());
    }

    public BlockPos origin(){
        return roomRegion.origin().offset(in.x()*roomRegion.roomLengthBlocks(),0,in.y()*roomRegion.roomLengthBlocks());
    }
}
