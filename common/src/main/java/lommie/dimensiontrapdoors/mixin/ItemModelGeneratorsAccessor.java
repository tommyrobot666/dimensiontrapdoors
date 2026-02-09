package lommie.dimensiontrapdoors.mixin;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemModelGenerators.class)
public interface ItemModelGeneratorsAccessor {
    @Accessor("itemModelOutput")
    ItemModelOutput getItemModelOutput();
}
