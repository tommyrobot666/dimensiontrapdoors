package lommie.dimensiontrapdoors.block;

import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.saveddata.TrapdoorRoomsSavedData;
import lommie.dimensiontrapdoors.trapdoorroom.DimensionEntrypoint;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoom;
import lommie.dimensiontrapdoors.trapdoorroom.TrapdoorRoomInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult blockHitResult) {
        if (!player.blockPosition().equals(blockPos) || !(level instanceof ServerLevel serverLevel) || level.isClientSide()){
            return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
        }
        TrapdoorRoomsSavedData savedData = TrapdoorRoomsSavedData.getFromLevel(serverLevel);

        DimensionEntrypoint entrypoint = savedData.findEntrypointOrCreate(blockPos);

        TrapdoorRoom room = savedData.getRoom(entrypoint.roomId());
        player.teleportTo(
                Objects.requireNonNull(serverLevel.getServer().getLevel(DimensionTrapdoors.TRAPDOOR_DIM)),
                room.globalSpawnPos().getX(),
                room.globalSpawnPos().getY(),
                room.globalSpawnPos().getZ(),
                Set.of(),
                0,0,
                false
        );


        return InteractionResult.SUCCESS;
    }
}
