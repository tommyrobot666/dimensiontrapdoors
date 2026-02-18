package lommie.dimensiontrapdoors.neoforge.datagen;

import dev.architectury.platform.Platform;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import lommie.dimensiontrapdoors.datagen.CommonDataGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = DimensionTrapdoors.MOD_ID)
public class DataGen {

    @SubscribeEvent
    public static void serverGatherData(GatherDataEvent.Server event){
        // DataGenerator is the parent class of FabricDataGenerator
        DataGenerator generator = event.getGenerator();
        // like "Pack pack = fabricDataGenerator.createPack();"
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        DataGenerator.PackGenerator packGenerator = generator.getPackGenerator(true,
                Platform.getMod(DimensionTrapdoors.MOD_ID).getName(),packOutput.getOutputFolder().toString());

        CommonDataGen.onDataGen(packGenerator,lookupProvider);
    }
}
