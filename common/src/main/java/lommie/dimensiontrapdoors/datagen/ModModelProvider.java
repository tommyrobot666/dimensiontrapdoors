package lommie.dimensiontrapdoors.datagen;

import lommie.dimensiontrapdoors.block.ModBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput packOutput) {
        super(packOutput);
    }

    public void generateBlockStateModels(BlockModelGenerators g) {
        g.createTrivialCube(ModBlocks.DIMENSION_DARKNESS.get());
        g.createTrivialCube(ModBlocks.DIMENSION_BLOCK.get());
        g.registerSimpleFlatItemModel(ModBlocks.DIMENSION_DARKNESS.get());
        g.registerSimpleFlatItemModel(ModBlocks.DIMENSION_BLOCK.get());
        g.createTrapdoor(ModBlocks.DIMENSION_TRAPDOOR.get());
        g.createTrivialCube(ModBlocks.DIMENSION_DARKNESS_SWIRL.get());
        g.registerSimpleFlatItemModel(ModBlocks.DIMENSION_DARKNESS_SWIRL.get());
        g.createTrivialBlock(ModBlocks.PEDESTAL.get(),TexturedModel.COLUMN);
        /*Consumer<BlockModelDefinitionGenerator> blockStateOutput;
        try {
            Field f = BlockModelGenerators.class.getDeclaredField("blockStateOutput");
            f.setAccessible(true);
            blockStateOutput = (Consumer<BlockModelDefinitionGenerator>) f.get(g);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.PEDESTAL.get()).);*/
        g.registerSimpleFlatItemModel(ModBlocks.PEDESTAL.get());
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public void generateItemModels(ItemModelGenerators g) {
    }
}
