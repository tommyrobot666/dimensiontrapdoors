package lommie.dimensiontrapdoors.blockentity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.block.ModBlocks;
import lommie.dimensiontrapdoors.mixin.BlockEntityTypeAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockEntities {
    public final static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(DimensionTrapdoors.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final DeferredSupplier<BlockEntityType<@NotNull PedestalBlockEntity>> PEDESTAL = BLOCK_ENTITIES.register("pedestal",
            () -> BlockEntityTypeAccessor.BlockEntityType(PedestalBlockEntity::new, Set.of(ModBlocks.PEDESTAL.get())));

}
