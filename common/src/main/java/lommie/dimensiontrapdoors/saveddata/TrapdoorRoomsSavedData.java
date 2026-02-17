package lommie.dimensiontrapdoors.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.block.ModBlocks;
import lommie.dimensiontrapdoors.trapdoorroom.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
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

    public @NotNull DimensionEntrypoint findEntrypointOrCreate(BlockPos pos, @Nullable Entity traveling){
        DimensionEntrypoint entrypoint = findEntrypoint(pos);
        if (entrypoint == null){
            return createEntrypoint(pos, traveling);
        }
        return entrypoint;
    }

    private @NotNull DimensionEntrypoint createEntrypoint(BlockPos pos, @Nullable Entity traveling) {
        TrapdoorRoom room = createRoom();
        DimensionEntrypoint entrypoint = new DimensionEntrypoint(room.roomId(), pos, traveling != null ? getCurrentRoom(traveling) : Optional.empty(),Optional.empty());
        entrypoints.add(entrypoint);
        if (entrypoint.fromRoomId().isPresent()){
            entrypoints.add(new DimensionEntrypoint(
                    entrypoint.fromRoomId().orElseThrow(),
                    room.globalSpawnPos(),
                    Optional.empty(),
                    Optional.of(entrypoints.indexOf(entrypoint))
            ));
            if (level.getBlockState(room.globalSpawnPos()).canBeReplaced()){
                level.setBlockAndUpdate(room.globalSpawnPos(), ModBlocks.DIMENSION_TRAPDOOR.get().defaultBlockState());
                BlockState below = level.getBlockState(room.globalSpawnPos().below());
                if (below.canBeReplaced() || below.is(ModBlocks.DIMENSION_DARKNESS.get())){
                    level.setBlockAndUpdate(room.globalSpawnPos().below(),ModBlocks.DIMENSION_DARKNESS_SWIRL.get().defaultBlockState());
                }
            }
        }
        setDirty();
        return entrypoint;
    }

    private Optional<Integer> getCurrentRoom(@NotNull Entity traveling) {
        if (traveling.level().dimension().equals(DimensionTrapdoors.TRAPDOOR_DIM)){
            ChunkPos chunkPos = traveling.chunkPosition();
            Optional<TrapdoorRoomRegion> roomRegion = findRegion(
                    chunkPos.x/TrapdoorRoomRegion.REGION_CHUNK_SIZE,
                    chunkPos.z/TrapdoorRoomRegion.REGION_CHUNK_SIZE);
            if (roomRegion.isEmpty()){
                return Optional.empty();
            }
            Stream<TrapdoorRoom> rooms = roomRegion.orElseThrow().roomsIds().stream().map(this::getRoom);
            rooms = rooms.filter((room) -> {
                BlockPos diff = traveling.blockPosition().subtract(room.origin());
                if (diff.getX() < 0 || diff.getZ() < 0){
                    return false;
                }
                return true;
            });
            TrapdoorRoom theRoom = rooms.min(Comparator.comparingDouble(r -> r.origin().distSqr(traveling.blockPosition()))).orElseThrow();
            return Optional.of(theRoom.roomId());
        }
        return Optional.empty();
    }

    public static TrapdoorRoomsSavedData getFromLevel(ServerLevel serverLevel){
        return serverLevel.getServer().overworld().getDataStorage().computeIfAbsent(TYPE).setLevel(serverLevel);
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

        generateStructure(trapdoorRoomType.structure(), room);

        placeBiome(trapdoorRoomType.biome(), trapdoorRoomType.chunksSize(), room);

        return room;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void placeBiome(Optional<Identifier> biome, int chunksSize, TrapdoorRoom room) {
        if (biome.isPresent()){
            Holder<Biome> biomeHolder = level.registryAccess().lookupOrThrow(Registries.BIOME)
                    .get(biome.get()).orElseThrow();
            BiomeResolver biomeSource = new FixedBiomeSource(biomeHolder);
            for (int i = 0; i < chunksSize; i++) {
                for (int j = 0; j < chunksSize; j++) {
                    level.getChunk(room.origin().offset(new BlockPos(i*TrapdoorRoomRegion.VANILLA_CHUNK_SIZE,0,j*TrapdoorRoomRegion.VANILLA_CHUNK_SIZE)))
                            .fillBiomesFromNoise(
                            biomeSource,
                            level.getChunkSource().randomState().sampler()
                    );
                }
            }
        }
    }

    private void generateStructure(Identifier structure, TrapdoorRoom room) {
        StructureTemplateManager structureManager = level.getServer().getStructureManager();
        Optional<StructureTemplate> optionalTemplate = structureManager.get(structure);
        if (optionalTemplate.isEmpty()){
            level.players().forEach(
                    (p) -> p.sendSystemMessage(Component.literal(
                                    "Error when generating structure: "
                                            + structure+" not found, generating default room")
                            .withStyle(ChatFormatting.RED,ChatFormatting.UNDERLINE))
            );
            optionalTemplate = structureManager.get(DimensionTrapdoors.TRAPDOOR_ROOM_TYPES.getDefaultKey());
        }
        StructureTemplate template = optionalTemplate.orElseThrow();
        // I have no clue what the number that I put into the flags argument does
        template.placeInWorld(level, room.origin(), room.origin(), new StructurePlaceSettings(), level.getRandom(), 0b1100110010);
    }

    private int[] arrayIndexToPos(int idx, int width) {
        int x = idx % width;
        int y = idx / width;
        return new int[]{x,y};
    }

    public TrapdoorRoomsSavedData mergeFiles(TrapdoorRoomsSavedData other){
        int roomOffset = rooms.size();
        int roomRegionOffset = roomRegions.size();
        int entrypointOffset = entrypoints.size();
        roomRegions.addAll(other.roomRegions.stream().map(rR -> {
            int[] pos = nextNewRegionPos();
            return new TrapdoorRoomRegion(rR.roomChunkSize(),pos[0],pos[1],
                    new ArrayList<>(rR.roomsIds().stream().map(i->i+roomOffset).toList()));
        }).toList());
        rooms.addAll(other.rooms.stream().map(r-> new TrapdoorRoomInfo(r.relativeSpawnPos(),r.x(),r.y(),r.structure(),r.biome(),r.roomRegionId()+roomRegionOffset,r.chunksSize())).toList());
        entrypoints.addAll(other.entrypoints.stream().map(e->new DimensionEntrypoint(e.roomId()+roomOffset,e.trapdoorPos(),
                e.fromRoomId().isPresent()? Optional.of(e.fromRoomId().get() + roomOffset) :Optional.empty(),
                e.toEntrypointId().isPresent()?Optional.of(e.toEntrypointId().get()+entrypointOffset):Optional.empty())).toList());

        rooms.subList(roomOffset,rooms.size()).forEach(r->{
            TrapdoorRoom ro = getRoom(rooms.indexOf(r));
            generateStructure(r.structure(),ro);
            placeBiome(r.biome(),r.chunksSize(),ro);
        });

        return this;
    }
}
