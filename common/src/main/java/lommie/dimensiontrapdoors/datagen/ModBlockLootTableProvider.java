package lommie.dimensiontrapdoors.datagen;

import lommie.dimensiontrapdoors.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.BiConsumer;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.DIMENSION_BLOCK.get());
        dropOther(ModBlocks.DIMENSION_DARKNESS.get(),ModBlocks.DIMENSION_BLOCK.get().asItem());
    }

    @Override
    public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        generate();

        for (ResourceKey<LootTable> lootTableResourceKey : this.map.keySet()) {
            biConsumer.accept(lootTableResourceKey,this.map.get(lootTableResourceKey));
        }
    }
}
