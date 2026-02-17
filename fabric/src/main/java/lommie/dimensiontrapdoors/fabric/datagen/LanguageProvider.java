package lommie.dimensiontrapdoors.fabric.datagen;

import lommie.dimensiontrapdoors.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class LanguageProvider extends FabricLanguageProvider {
    protected LanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NotNull Provider p, TranslationBuilder tb) {
        tb.add(ModBlocks.DIMENSION_DARKNESS.get(),"True Darkness");
        tb.add(ModBlocks.DIMENSION_BLOCK.get(),"Dimension Block");
        tb.add(ModBlocks.DIMENSION_DARKNESS.get().asItem(),"True Darkness");
        tb.add(ModBlocks.DIMENSION_BLOCK.get().asItem(),"Dimension Block");
        tb.add(ModBlocks.DIMENSION_DARKNESS_SWIRL.get().asItem(),"Dimension Darkness Swirl");
        tb.add(ModBlocks.DIMENSION_TRAPDOOR.get(),"Dimension Trapdoor");
        tb.add(ModBlocks.DIMENSION_TRAPDOOR.get().asItem(),"Dimension Trapdoor");
        tb.add(ModBlocks.PEDESTAL.get().asItem(),"Pedestal");
        tb.add(ModBlocks.PEDESTAL.get(),"Pedestal");
    }
}
