package lommie.dimensiontrapdoors.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import lommie.dimensiontrapdoors.datagen.ModModelProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelProvider.class)
public class ModelProviderMixin {
    @WrapOperation(
            method = {"run(Lnet/minecraft/data/CachedOutput;)Ljava/util/concurrent/CompletableFuture;"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/data/models/BlockModelGenerators;run()V"
            )}
    )
    private void registerBlockStateModels(BlockModelGenerators instance, Operation<Void> original) {
        if (((Object) this) instanceof ModModelProvider fabricModelProvider) {
            fabricModelProvider.generateBlockStateModels(instance);
        } else {
            original.call(instance);
        }

    }

    @WrapOperation(
            method = {"run(Lnet/minecraft/data/CachedOutput;)Ljava/util/concurrent/CompletableFuture;"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/data/models/ItemModelGenerators;run()V"
            )}
    )
    private void registerItemModels(ItemModelGenerators instance, Operation<Void> original) {
        if (((Object) this) instanceof ModModelProvider fabricModelProvider) {
            fabricModelProvider.generateItemModels(instance);
        } else {
            original.call(instance);
        }

    }
}
