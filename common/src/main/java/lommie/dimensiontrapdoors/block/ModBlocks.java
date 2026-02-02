package lommie.dimensiontrapdoors.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    public final static DeferredRegister<Block> BLOCKS = DeferredRegister.create(DimensionTrapdoors.MOD_ID, Registries.BLOCK);

    static DeferredSupplier<Block> register(String id, Function<BlockBehaviour.Properties,Block> blockConstructor, BlockBehaviour.Properties properties){
        return BLOCKS.register(id,() -> blockConstructor.apply(properties.setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(DimensionTrapdoors.MOD_ID,id)))));
    }

    public final static DeferredSupplier<Block> DIMENSION_DARKNESS =
            register("dimension_darkness", Block::new, BlockBehaviour.Properties.of());

    public final static DeferredSupplier<Block> DIMENSION_BLOCK =
            register("dimension_block", Block::new, BlockBehaviour.Properties.of());

    public static final DeferredSupplier<Block> PEDESTAL =
            register("pedestal", PedestalBlock::new, BlockBehaviour.Properties.of());

    public static final DeferredSupplier<Block> DIMENSION_TRAPDOOR =
            register("dimension_trapdoor", DimensionTrapdoor::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR));
}
