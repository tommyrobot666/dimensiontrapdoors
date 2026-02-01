package lommie.dimensiontrapdoors.fabric.datagen;

import lommie.dimensiontrapdoors.datagen.CommonDataGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        CommonDataGen.onDataGen(pack,fabricDataGenerator.getRegistries());

        pack.addProvider(LanguageProvider::new);
    }
}
