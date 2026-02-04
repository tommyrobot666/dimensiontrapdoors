package lommie.dimensiontrapdoors.fabric.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import lommie.dimensiontrapdoors.blockentity.ModBlockEntities;
import lommie.dimensiontrapdoors.blockentity.PedestalBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;

public final class DimensionTrapdoorsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BlockEntityRendererRegistry.register(ModBlockEntities.PEDESTAL.get(),
                PedestalBlockEntityRenderer::new);
    }
}
