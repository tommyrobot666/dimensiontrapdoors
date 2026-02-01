package lommie.dimensiontrapdoors.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CommonDataGen {
    public static void onDataGen(DataGenerator.PackGenerator packGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider){
        packGenerator.addProvider((packOutput -> new LootTableProvider(
                packOutput,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider
        )));

        packGenerator.addProvider(ModModelProvider::new);
    }
}
