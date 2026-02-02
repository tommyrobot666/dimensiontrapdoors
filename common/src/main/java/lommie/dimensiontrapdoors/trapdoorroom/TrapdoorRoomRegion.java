package lommie.dimensiontrapdoors.trapdoorroom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public record TrapdoorRoomRegion(int roomChunkSize, int x, int y, ArrayList<Integer> roomsIds) {
    public static final Codec<TrapdoorRoomRegion> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(Codec.INT.fieldOf("roomChunkSize").forGetter(TrapdoorRoomRegion::roomChunkSize),
                            Codec.INT.fieldOf("x").forGetter(TrapdoorRoomRegion::roomChunkSize),
                            Codec.INT.fieldOf("y").forGetter(TrapdoorRoomRegion::roomChunkSize),
                            Codec.INT.listOf().fieldOf("roomsIds").forGetter(TrapdoorRoomRegion::roomsIds))
                    .apply(instance,TrapdoorRoomRegion::noArrayList)
    );
    public static final int REGION_CHUNK_SIZE = 16;
    public static final int VANILLA_CHUNK_SIZE = 16;

    public static TrapdoorRoomRegion noArrayList(int roomChunkSize, int x, int y, List<Integer> roomsIds){
        return new TrapdoorRoomRegion(roomChunkSize,x,y,new ArrayList<>(roomsIds));
    }

    public int maxRooms(){
        return (int) Math.pow(REGION_CHUNK_SIZE/roomChunkSize,2);
    }

    public ChunkPos chunkPos(){
        return new ChunkPos(x*REGION_CHUNK_SIZE,y*REGION_CHUNK_SIZE);
    }

    public BlockPos origin(){
        ChunkPos chunkPos = chunkPos();
        return new BlockPos(chunkPos.x*VANILLA_CHUNK_SIZE,0,chunkPos.z*VANILLA_CHUNK_SIZE);
    }

    public int roomLengthBlocks(){
        return roomChunkSize*VANILLA_CHUNK_SIZE;
    }
}
