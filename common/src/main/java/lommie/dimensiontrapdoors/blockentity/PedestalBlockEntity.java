package lommie.dimensiontrapdoors.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class PedestalBlockEntity extends BlockEntity implements ItemOwner, WorldlyContainer {
    public @NotNull ItemStack item = ItemStack.EMPTY;

    public PedestalBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PEDESTAL.get(), blockPos, blockState);
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public static void tick(Level level, BlockPos pos, BlockState state, PedestalBlockEntity e) {

    }

    public void swapItem(@NotNull ItemStack itemStack, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull Level level, @NotNull BlockPos pos) {
        player.setItemInHand(interactionHand,item);
        item = itemStack;
        setChanged();

        if (itemStack.isEmpty() && player.getItemInHand(interactionHand).isEmpty()){
            return;
        }
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

    @Override
    public @NotNull Level level() {
        return Objects.requireNonNull(getLevel());
    }

    @Override
    public @NotNull Vec3 position() {
        return getBlockPos().getCenter();
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return 0;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return ItemStack.isSameItemSameComponents(itemStack,item);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return item;
    }

    //TODO: items taken out aren't removed (duplicated)
    @Override
    public @NotNull ItemStack removeItem(int i, int j) {
        return item;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return item;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        item = itemStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        item = ItemStack.EMPTY;
    }
}
