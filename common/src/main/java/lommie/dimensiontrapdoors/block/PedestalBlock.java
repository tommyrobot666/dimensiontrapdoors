package lommie.dimensiontrapdoors.block;

import com.mojang.serialization.MapCodec;
import lommie.dimensiontrapdoors.blockentity.ModBlockEntities;
import lommie.dimensiontrapdoors.blockentity.PedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PedestalBlock extends BaseEntityBlock implements WorldlyContainerHolder {
    protected PedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PedestalBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PedestalBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.PEDESTAL.get(),((level1, blockPos, blockState1, blockEntity) -> PedestalBlockEntity.tick(level1,blockPos,blockState1, blockEntity)));
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack itemStack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(pos) instanceof PedestalBlockEntity blockEntity)){
            return super.useItemOn(itemStack, state, level, pos, player, interactionHand, blockHitResult);
        }

        blockEntity.swapItem(itemStack,player,interactionHand,level,pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState blockState, LootParams.@NotNull Builder builder) {
        ArrayList<ItemStack> list = new ArrayList<>(super.getDrops(blockState, builder));
        list.add(((PedestalBlockEntity) builder.getParameter(LootContextParams.BLOCK_ENTITY)).item);
        return list;
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public @NotNull WorldlyContainer getContainer(@NotNull BlockState blockState, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos blockPos) {
        return ((PedestalBlockEntity) levelAccessor.getBlockEntity(blockPos));
    }
}
