package lommie.dimensiontrapdoors.block;

import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.saveddata.TrapdoorRoomsSavedData;
import lommie.dimensiontrapdoors.trapdoorroom.DimensionEntrypoint;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class DimensionTrapdoor extends TrapDoorBlock {
    public DimensionTrapdoor(Properties properties) {
        super(ModBlockSetTypes.DIMENSION_BLOCK, properties);
    }

    @Override
    protected void onPlace(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState2, boolean bl) {
        if (!(level instanceof ServerLevel serverLevel) || level.isClientSide()) return;

        TrapdoorRoomsSavedData savedData = TrapdoorRoomsSavedData.getFromLevel(serverLevel);
        if (savedData.rooms.isEmpty()){
            savedData.findEntrypointOrCreate(blockPos,null);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult blockHitResult) {
        if (!player.blockPosition().equals(blockPos) || !(level instanceof ServerLevel serverLevel) || level.isClientSide()){
            return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
        }
        TrapdoorRoomsSavedData savedData = TrapdoorRoomsSavedData.getFromLevel(serverLevel);

        DimensionEntrypoint entrypoint = savedData.findEntrypointOrCreate(blockPos,player);

        BlockPos tpTo;

        if (entrypoint.toEntrypointId().isPresent()){
            DimensionEntrypoint toEntrypoint = savedData.entrypoints.get(entrypoint.toEntrypointId().get());
            tpTo = toEntrypoint.trapdoorPos();
        } else {
            TrapdoorRoom room = savedData.getRoom(entrypoint.roomId());
            tpTo = room.globalSpawnPos();
        }

        player.teleportTo(
                Objects.requireNonNull(serverLevel.getServer().getLevel(DimensionTrapdoors.TRAPDOOR_DIM)),
                tpTo.getX()+.5,
                tpTo.getY()+.5,
                tpTo.getZ()+.5,
                Set.of(),
                0,0,
                false
        );


        return InteractionResult.SUCCESS;
    }
}
