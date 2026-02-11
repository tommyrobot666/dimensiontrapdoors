package lommie.dimensiontrapdoors.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public final static DeferredRegister<Item> ITEMS = DeferredRegister.create(DimensionTrapdoors.MOD_ID, Registries.ITEM);

    static DeferredSupplier<Item> register(String id, Function<Item.Properties,Item> blockConstructor, Item.Properties properties){
        return ITEMS.register(id,() -> blockConstructor.apply(properties.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DimensionTrapdoors.MOD_ID,id)))));
    }

    public final static DeferredSupplier<Item> DIMENSION_DARKNESS =
            register("dimension_darkness", (p) -> new BlockItem(ModBlocks.DIMENSION_DARKNESS.get(),p), new Item.Properties());

    public final static DeferredSupplier<Item> DIMENSION_BLOCK =
            register("dimension_block", (p) -> new BlockItem(ModBlocks.DIMENSION_BLOCK.get(),p), new Item.Properties());

    public final static DeferredSupplier<Item> PEDESTAL =
            register("pedestal", (p) -> new BlockItem(ModBlocks.PEDESTAL.get(),p), new Item.Properties());

    public final static DeferredSupplier<Item> DIMENSION_TRAPDOOR =
            register("dimension_trapdoor", (p) -> new BlockItem(ModBlocks.DIMENSION_TRAPDOOR.get(),p), new Item.Properties());

    public final static DeferredSupplier<Item> DIMENSION_DARKNESS_SWIRL =
            register("dimension_darkness_swirl", (p) -> new BlockItem(ModBlocks.DIMENSION_DARKNESS_SWIRL.get(),p), new Item.Properties());
}
