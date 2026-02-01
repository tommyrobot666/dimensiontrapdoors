package lommie.dimensiontrapdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class PedestalBlockEntity extends BlockEntity {
    public @NotNull ItemStack item = ItemStack.EMPTY;

    public PedestalBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PEDESTAL.get(), blockPos, blockState);
    }

    static void tick(Level level, BlockPos pos, BlockState state, PedestalBlockEntity e) {

    }

    public void swapItem(@NotNull ItemStack itemStack, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull Level level, @NotNull BlockPos pos) {
        player.setItemInHand(interactionHand,item);
        item = itemStack;
        setChanged();

        level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.BLOCKS);
    }

    @Override
    protected void saveAdditional(ValueOutput o) {
        o.store("item",ItemStack.CODEC,item);
    }

    @Override
    protected void loadAdditional(ValueInput i) {
        item = i.read("item",ItemStack.CODEC).orElseThrow();
    }
}
