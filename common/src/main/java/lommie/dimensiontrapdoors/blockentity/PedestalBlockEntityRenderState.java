package lommie.dimensiontrapdoors.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.NotNull;

public class PedestalBlockEntityRenderState extends BlockEntityRenderState {
    // in vanilla BlockEntityRenderState, this would be nullable
    @NotNull ItemStackRenderState item = new ItemStackRenderState();
}
