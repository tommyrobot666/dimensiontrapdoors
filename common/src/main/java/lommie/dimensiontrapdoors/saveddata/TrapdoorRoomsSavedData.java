package lommie.dimensiontrapdoors.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.trapdoorroom.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    ServerLevel level;

    public @Nullable DimensionEntrypoint findEntrypoint(BlockPos pos){
        // if this lags, use a data structure and regenerate it when entrypoints is changed
        return entrypoints.stream().filter((e) -> e.trapdoorPos().equals(pos)).findFirst().orElse(null);
    }

    public @NotNull DimensionEntrypoint findEntrypointOrCreate(BlockPos pos){
        DimensionEntrypoint entrypoint = findEntrypoint(pos);
        if (entrypoint == null){
            return createEntrypoint(pos);
        }
        return entrypoint;
    }

    private @NotNull DimensionEntrypoint createEntrypoint(BlockPos pos) {
        TrapdoorRoom room = createRoom();
        DimensionEntrypoint entrypoint = new DimensionEntrypoint(room.roomId(), pos);
        entrypoints.add(entrypoint);
        setDirty();
        return entrypoint;
    }

    public static TrapdoorRoomsSavedData getFromLevel(ServerLevel serverLevel){
        return serverLevel.getDataStorage().computeIfAbsent(TYPE).setLevel(serverLevel);
    }

    private TrapdoorRoomsSavedData setLevel(ServerLevel serverLevel) {
        level = serverLevel;
        return this;
    }

    public TrapdoorRoom getRoom(int idx){
        return new TrapdoorRoom(rooms.get(idx),findRegion(idx),idx);
    }

    public TrapdoorRoomRegion findRegion(int idOfRoomThatIsInRegion){
        return roomRegions.stream().filter((r) -> r.roomsIds().contains(idOfRoomThatIsInRegion)).findFirst().orElseThrow();
    }

    public Optional<TrapdoorRoomRegion> findRegion(int x, int y){
        return roomRegions.stream().filter((r) -> r.x() == x && r.y() == y).findFirst();
    }

    public @Nullable TrapdoorRoomRegion getNotFullRegionWithRoomSize(int roomChunkSize){
        return getNotFullRegions().filter((r) -> r.roomChunkSize() == roomChunkSize).findFirst().orElse(null);
    }

    public TrapdoorRoomRegion createRegion(int roomChunkSize){
        int[] trapdoorRoomPos;
        trapdoorRoomPos = nextNewRegionPos();
        int x = trapdoorRoomPos[0];
        int y = trapdoorRoomPos[1];
        TrapdoorRoomRegion trapdoorRoomRegion = new TrapdoorRoomRegion(roomChunkSize,x,y,new ArrayList<>());
        roomRegions.add(trapdoorRoomRegion);
        setDirty();
        return trapdoorRoomRegion;
    }

    private int @NotNull [] nextNewRegionPos() {
        int[] trapdoorRoomPos;
        int roomRegionPlacementFunctionOffset = -1;
        do {
            roomRegionPlacementFunctionOffset++;
            int i = roomRegions.size() + 1 + roomRegionPlacementFunctionOffset;
            trapdoorRoomPos = arrayIndexToPos(i, (int) Math.ceil(Math.sqrt(i)));
        } while (findRegion(trapdoorRoomPos[0],trapdoorRoomPos[1]).isPresent());
        return trapdoorRoomPos;
    }

    public @NotNull TrapdoorRoomRegion currentNotFullRegion(int roomChunkSize){
        TrapdoorRoomRegion trapdoorRoomRegion = getNotFullRegionWithRoomSize(roomChunkSize);
        if (trapdoorRoomRegion == null){
            return createRegion(roomChunkSize);
        }
        return trapdoorRoomRegion;
    }

    public Stream<TrapdoorRoomRegion> getNotFullRegions(){
        return roomRegions.stream().filter((r) -> r.roomsIds().size() < r.maxRooms());
    }

    public TrapdoorRoom createRoom(){
        TrapdoorRoomType trapdoorRoomType = DimensionTrapdoors.TRAPDOOR_ROOM_TYPES.byId(
                level.random.nextInt(0,DimensionTrapdoors.TRAPDOOR_ROOM_TYPES.size()));

        TrapdoorRoomRegion roomRegion = currentNotFullRegion(trapdoorRoomType.chunksSize());
        int[] trapdoorRoomPos = arrayIndexToPos(roomRegion.roomsIds().size(),roomRegion.width());
        TrapdoorRoomInfo trapdoorRoomInfo = TrapdoorRoomInfo.fromType(trapdoorRoomType,trapdoorRoomPos[0],trapdoorRoomPos[1], roomRegions.indexOf(roomRegion));
        rooms.add(trapdoorRoomInfo);
        setDirty();
        roomRegion.roomsIds().add(rooms.indexOf(trapdoorRoomInfo));
        TrapdoorRoom room = getRoom(rooms.indexOf(trapdoorRoomInfo));

        StructureTemplateManager structureManager = level.getServer().getStructureManager();
        Optional<StructureTemplate> optionalTemplate = structureManager.get(trapdoorRoomType.structure());
        if (optionalTemplate.isEmpty()){
            level.players().forEach(
                    (p) -> p.sendSystemMessage(Component.literal(
                                    "Error when generating structure: "
                                            +trapdoorRoomType.structure()+" not found, generating default room")
                            .withStyle(ChatFormatting.RED,ChatFormatting.UNDERLINE))
            );
        }
        optionalTemplate = structureManager.get(DimensionTrapdoors.TRAPDOOR_ROOM_TYPES.getDefaultKey());
        StructureTemplate template = optionalTemplate.orElseThrow();
        // I have no clue what the number that I put into the flags argument does
        template.placeInWorld(level, room.origin(), room.origin(), new StructurePlaceSettings(), level.getRandom(), 0b1100110010);


        return room;
    }

    private int[] arrayIndexToPos(int idx, int width) {
        int x = idx % width;
        int y = idx / width;
        return new int[]{x,y};
    }
}
