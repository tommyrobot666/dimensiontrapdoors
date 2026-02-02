package lommie.dimensiontrapdoors.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.trapdoorroom.DimensionEntrypoint;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoom;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoomInfo;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoomRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrapdoorRoomsSavedData extends SavedData {
    public static Codec<@NotNull TrapdoorRoomsSavedData> CODEC = RecordCodecBuilder.create((instance)
    -> instance.group(
            TrapdoorRoomRegion.CODEC.listOf().fieldOf("roomRegions").forGetter(TrapdoorRoomsSavedData::getRoomRegionsList),
            TrapdoorRoomInfo.CODEC.listOf().fieldOf("rooms").forGetter(TrapdoorRoomsSavedData::getRoomInfosList),
            DimensionEntrypoint.CODEC.listOf().fieldOf("entrypoints").forGetter(TrapdoorRoomsSavedData::getEntrypointsList)
    ).apply(instance,TrapdoorRoomsSavedData::new));

    public TrapdoorRoomsSavedData(List<TrapdoorRoomRegion> trapdoorRoomRegions, List<TrapdoorRoomInfo> trapdoorRoomInfos, List<DimensionEntrypoint> dimensionEntrypoints) {
        roomRegions = new ArrayList<>(trapdoorRoomRegions);
        rooms = new ArrayList<>(trapdoorRoomInfos);
        entrypoints = new ArrayList<>(dimensionEntrypoints);
    }

    public static TrapdoorRoomsSavedData empty(){
        return new TrapdoorRoomsSavedData(List.of(),List.of(),List.of());
    }

    private List<DimensionEntrypoint> getEntrypointsList() {
        return entrypoints;
    }

    private List<TrapdoorRoomInfo> getRoomInfosList() {
        return rooms;
    }

    private List<TrapdoorRoomRegion> getRoomRegionsList() {
        return roomRegions;
    }

    public static SavedDataType<@NotNull TrapdoorRoomsSavedData> TYPE = new SavedDataType<>(
            DimensionTrapdoors.MOD_ID+"_trapdoors_rooms",
            TrapdoorRoomsSavedData::empty,
            CODEC,
            null
    );

    public final ArrayList<TrapdoorRoomRegion> roomRegions;
    public final ArrayList<TrapdoorRoomInfo> rooms;
    public final ArrayList<DimensionEntrypoint> entrypoints;

    public @Nullable DimensionEntrypoint findEntrypoint(BlockPos pos){
        // if this lags, use a data structure and regenerate it when entrypoints is changed
        return entrypoints.stream().filter((e) -> e.trapdoorPos() == pos).findFirst().orElse(null);
    }

    public static TrapdoorRoomsSavedData getFromLevel(ServerLevel serverLevel){
        return serverLevel.getDataStorage().get(TYPE);
    }

    public TrapdoorRoom getRoom(int idx){
        //TODO make a findRegion(pos) to use right region
        //TODO add a cache?
        return new TrapdoorRoom(rooms.get(idx),roomRegions.getFirst(),idx);
    }
}
