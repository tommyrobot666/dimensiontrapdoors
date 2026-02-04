package lommie.dimensiontrapdoors.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.trapdoorroom.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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

    public @Nullable TrapdoorRoomRegion getNotFullRegionWithRoomSize(int roomChunkSize){
        return getNotFullRegions().filter((r) -> r.roomChunkSize() == roomChunkSize).findFirst().orElse(null);
    }

    public TrapdoorRoomRegion createRegion(int roomChunkSize){
        //TODO calculate x and y from roomRegions.size() in a way that covers the least space
        //TODO fail if room exists or add offset (to listSize) that gets ++ until no fail
        int x = roomRegions.size();
        int y = 0;
        TrapdoorRoomRegion trapdoorRoomRegion = new TrapdoorRoomRegion(roomChunkSize,x,y,new ArrayList<>());
        roomRegions.add(trapdoorRoomRegion);
        setDirty();
        return trapdoorRoomRegion;
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
        //TODO randomly select from TrapdoorRoomType from registry (use a randomSource that's set in getFromLevel)
        TrapdoorRoomType trapdoorRoomType = new TrapdoorRoomType(new BlockPos(8,1,8), Identifier.withDefaultNamespace(""),2);

        TrapdoorRoomRegion roomRegion = currentNotFullRegion(trapdoorRoomType.chunksSize());
        int[] trapdoorRoomPos = arrayIndexToPos(roomRegion.roomsIds().size(),roomRegion.width());
        TrapdoorRoomInfo trapdoorRoomInfo = TrapdoorRoomInfo.fromType(trapdoorRoomType,trapdoorRoomPos[0],trapdoorRoomPos[1], roomRegions.indexOf(roomRegion));
        rooms.add(trapdoorRoomInfo);
        setDirty();
        roomRegion.roomsIds().add(rooms.indexOf(trapdoorRoomInfo));
        TrapdoorRoom room = getRoom(rooms.indexOf(trapdoorRoomInfo));

        if (!trapdoorRoomType.structure().getPath().isEmpty()) {
            placeStructure(trapdoorRoomType, room);
        }

        return room;
    }

    private void placeStructure(TrapdoorRoomType trapdoorRoomType, TrapdoorRoom room) {
        RegistryAccess.Frozen registryAccess = level.getServer().registryAccess();
        Holder.Reference<Structure> structureReference = registryAccess.get(Registries.STRUCTURE)
                .orElseThrow().value().get(trapdoorRoomType.structure()).orElseThrow();
        Structure structure = structureReference.value();
        ServerChunkCache chunkSource = level.getChunkSource();
        StructureStart structureStart = structure.generate(structureReference,level.dimension(),registryAccess,chunkSource.getGenerator(),
                chunkSource.getGenerator().getBiomeSource(), chunkSource.randomState(),
                level.getStructureManager(), level.getSeed(), new ChunkPos(room.origin()),
                0, level, (biome) -> true);

        BoundingBox boundingBox = structureStart.getBoundingBox();
        ChunkPos chunkPos = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
        ChunkPos chunkPos2 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));
        ChunkPos.rangeClosed(chunkPos, chunkPos2).forEach((chunkPosX) -> {
            structureStart.placeInChunk(level, level.structureManager(), chunkSource.getGenerator(), level.getRandom(),
                    new BoundingBox(chunkPosX.getMinBlockX(), level.getMinY(), chunkPosX.getMinBlockZ(),
                            chunkPosX.getMaxBlockX(), level.getMaxY() + 1, chunkPosX.getMaxBlockZ()), chunkPosX);
        });
    }

    private int[] arrayIndexToPos(int idx, int width) {
        int x = idx % width;
        int y = idx / width;
        return new int[]{x,y};
    }
}
